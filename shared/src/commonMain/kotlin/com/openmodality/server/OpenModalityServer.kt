package com.openmodality.server

import com.openmodality.sensor.PlatformSensors
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlin.random.Random
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.json.*

/**
 * WebSocket-based sensor server inspired by SensorServer.
 *
 * Clients connect via:
 *   ws://<phone-ip>:<port>/ws?pin=<6-digit-pin>   (WebSocket, requires PIN)
 *   POST http://<phone-ip>:<port>/call             (HTTP one-shot, requires PIN header)
 *   GET  http://<phone-ip>:<port>/info             (no auth, discovery)
 *   GET  http://<phone-ip>:<port>/health           (no auth)
 *
 * WebSocket message flow:
 *   → {"id":"1","method":"list_tools"}
 *   ← {"id":"1","result":{"tools":[...]}}
 *
 *   → {"id":"2","method":"take_photo","params":{"camera":"back"}}
 *   ← {"id":"2","result":{"content":[...]}}
 */
class OpenModalityServer(
    private val tools: List<Tool>,
    private val sensors: PlatformSensors,
    private val sessionManager: SessionManager = SessionManager(),
    private val port: Int = 8080
) {
    private var server: EmbeddedServer<CIOApplicationEngine, CIOApplicationEngine.Configuration>? = null
    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
        isLenient = true
    }

    private var _pin: String = generatePin()
    val currentPin: String get() = _pin

    private val _isRunning = MutableStateFlow(false)
    val isRunning: StateFlow<Boolean> = _isRunning.asStateFlow()

    val sessions: SessionManager get() = sessionManager
    val toolCount: Int get() = tools.size

    private val toolMap: Map<String, Tool> = tools.associateBy { it.name }

    fun start() {
        _pin = generatePin()
        server = embeddedServer(CIO, port = port) {
            install(ContentNegotiation) { json(this@OpenModalityServer.json) }
            install(CORS) {
                anyHost()
                allowMethod(HttpMethod.Post)
                allowMethod(HttpMethod.Get)
                allowHeader(HttpHeaders.ContentType)
                allowHeader(HttpHeaders.Accept)
                allowHeader("X-Pin")
            }
            install(WebSockets) {
                pingPeriod = 30.seconds
                timeout = 60.seconds
                maxFrameSize = Long.MAX_VALUE
            }

            routing {
                // WebSocket endpoint — main communication channel
                webSocket("/ws") {
                    val pin = call.request.queryParameters["pin"]
                    if (pin != _pin) {
                        close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "Invalid PIN"))
                        return@webSocket
                    }

                    val clientId = "ws-${++clientCounter}"
                    sessionManager.addClient(ConnectedClient(id = clientId, connectedAt = currentTimeMillis()))

                    try {
                        for (frame in incoming) {
                            if (frame is Frame.Text) {
                                val response = handleMessage(clientId, frame.readText())
                                send(Frame.Text(response))
                            }
                        }
                    } finally {
                        sessionManager.removeClient(clientId)
                    }
                }

                // HTTP one-shot endpoint — for clients that don't support WebSocket
                post("/call") {
                    val pin = call.request.headers["X-Pin"]
                        ?: call.request.queryParameters["pin"]
                    if (pin != _pin) {
                        call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Invalid PIN"))
                        return@post
                    }

                    val body = call.receiveText()
                    val result = handleMessage("http-${++clientCounter}", body)
                    call.respondText(result, ContentType.Application.Json)
                }

                // Discovery endpoint — no auth, returns server info + tool list
                get("/info") {
                    val available = sensors.availableSensors()
                    val sensorInfos = available.map { sensor ->
                        SensorInfo(
                            id = sensor.id,
                            name = sensor.displayName,
                            category = sensor.category.name,
                            permission = sensors.permissionStatus(sensor).name
                        )
                    }
                    val info = ServerInfoResponse(
                        tools = tools.map { it.toInfo() },
                        sensors = sensorInfos
                    )
                    call.respondText(
                        json.encodeToString(ServerInfoResponse.serializer(), info),
                        ContentType.Application.Json
                    )
                }

                // Health check
                get("/health") { call.respondText("ok") }
            }
        }
        server?.start(wait = false)
        _isRunning.value = true
    }

    fun stop() {
        server?.stop(gracePeriodMillis = 1000, timeoutMillis = 3000)
        server = null
        _isRunning.value = false
    }

    // -- Message handling --

    private suspend fun handleMessage(clientId: String, text: String): String {
        val startTime = currentTimeMillis()
        return try {
            val request = json.decodeFromString<Request>(text)
            val result = dispatch(request)
            val response = Response(id = request.id, result = result)

            sessionManager.logRequest(RequestLogEntry(
                clientId = clientId,
                method = request.method,
                toolName = if (toolMap.containsKey(request.method)) request.method else null,
                timestamp = startTime,
                durationMs = currentTimeMillis() - startTime
            ))

            json.encodeToString(Response.serializer(), response)
        } catch (e: Exception) {
            sessionManager.logRequest(RequestLogEntry(
                clientId = clientId,
                method = "unknown",
                timestamp = startTime,
                durationMs = currentTimeMillis() - startTime,
                success = false,
                error = e.message
            ))
            val response = Response(
                id = "0",
                error = ErrorInfo(code = -1, message = e.message ?: "Internal error")
            )
            json.encodeToString(Response.serializer(), response)
        }
    }

    private suspend fun dispatch(request: Request): JsonElement {
        return when (request.method) {
            "list_tools" -> json.encodeToJsonElement(
                ToolListResult(tools = tools.map { it.toInfo() })
            )
            "ping" -> json.encodeToJsonElement(mapOf("status" to "ok"))
            "get_info" -> {
                val available = sensors.availableSensors()
                val sensorInfos = available.map { sensor ->
                    SensorInfo(
                        id = sensor.id,
                        name = sensor.displayName,
                        category = sensor.category.name,
                        permission = sensors.permissionStatus(sensor).name
                    )
                }
                json.encodeToJsonElement(ServerInfoResponse(
                    tools = tools.map { it.toInfo() },
                    sensors = sensorInfos
                ))
            }
            else -> {
                val tool = toolMap[request.method]
                    ?: throw IllegalArgumentException("Unknown method: ${request.method}")
                json.encodeToJsonElement(tool.handler(request.params))
            }
        }
    }

    // -- Utilities --

    private var clientCounter = 0

    private fun generatePin(): String = (100000 + Random.nextInt(900000)).toString()
}

expect fun currentTimeMillis(): Long
