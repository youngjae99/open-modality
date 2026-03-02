package com.openmodality.mcp

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
import io.ktor.server.sse.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.json.*

/**
 * MCP Server running on the phone.
 * Implements MCP Streamable HTTP transport.
 *
 * Clients connect via:
 *   POST http://<phone-ip>:<port>/mcp  (JSON-RPC requests)
 *   GET  http://<phone-ip>:<port>/mcp  (SSE stream for server-initiated messages)
 */
class McpServer(
    private val tools: List<McpTool>,
    private val resources: List<McpResource>,
    private val sessionManager: McpSessionManager = McpSessionManager(),
    private val port: Int = 8080
) {
    private var server: EmbeddedServer<CIOApplicationEngine, CIOApplicationEngine.Configuration>? = null
    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
        isLenient = true
    }

    private val _isRunning = MutableStateFlow(false)
    val isRunning: StateFlow<Boolean> = _isRunning.asStateFlow()

    val sessions: McpSessionManager get() = sessionManager

    private val toolMap: Map<String, McpTool> = tools.associateBy { it.name }
    private val resourceMap: Map<String, McpResource> = resources.associateBy { it.uri }

    fun start() {
        server = embeddedServer(CIO, port = port) {
            install(ContentNegotiation) {
                json(this@McpServer.json)
            }
            install(CORS) {
                anyHost()
                allowMethod(HttpMethod.Post)
                allowMethod(HttpMethod.Get)
                allowHeader(HttpHeaders.ContentType)
                allowHeader(HttpHeaders.Accept)
            }
            install(SSE)

            routing {
                // MCP Streamable HTTP: POST for requests
                post("/mcp") {
                    handleMcpRequest(call)
                }

                // MCP Streamable HTTP: SSE for server-initiated messages
                sse("/mcp/sse") {
                    // Keep connection open for server notifications
                    send(io.ktor.sse.ServerSentEvent(data = """{"jsonrpc":"2.0","method":"ping"}"""))
                }

                // Health check
                get("/health") {
                    call.respondText("ok")
                }
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

    private suspend fun handleMcpRequest(call: ApplicationCall) {
        val startTime = currentTimeMillis()

        try {
            val body = call.receiveText()
            val request = json.decodeFromString<JsonRpcRequest>(body)

            val result = processRequest(request)
            val response = JsonRpcResponse(
                id = request.id,
                result = json.encodeToJsonElement(JsonElement.serializer(), result)
            )

            sessionManager.logRequest(
                RequestLogEntry(
                    clientId = null,
                    method = request.method,
                    toolName = if (request.method == "tools/call") {
                        request.params?.get("name")?.let { json.decodeFromJsonElement<String>(it) }
                    } else null,
                    timestamp = startTime,
                    durationMs = currentTimeMillis() - startTime
                )
            )

            call.respond(HttpStatusCode.OK, response)
        } catch (e: Exception) {
            val errorResponse = JsonRpcResponse(
                error = JsonRpcError(
                    code = -32603,
                    message = e.message ?: "Internal error"
                )
            )

            sessionManager.logRequest(
                RequestLogEntry(
                    clientId = null,
                    method = "unknown",
                    timestamp = startTime,
                    durationMs = currentTimeMillis() - startTime,
                    success = false,
                    error = e.message
                )
            )

            call.respond(HttpStatusCode.OK, errorResponse)
        }
    }

    private suspend fun processRequest(request: JsonRpcRequest): JsonElement {
        return when (request.method) {
            "initialize" -> handleInitialize(request.params)
            "notifications/initialized" -> JsonNull
            "tools/list" -> handleToolsList()
            "tools/call" -> handleToolCall(request.params)
            "resources/list" -> handleResourcesList()
            "resources/read" -> handleResourceRead(request.params)
            "ping" -> json.encodeToJsonElement(mapOf("status" to "ok"))
            else -> throw IllegalArgumentException("Unknown method: ${request.method}")
        }
    }

    private fun handleInitialize(params: JsonObject?): JsonElement {
        if (params != null) {
            val initParams = json.decodeFromJsonElement<InitializeParams>(params)
            val client = ConnectedClient(
                id = generateClientId(),
                name = initParams.clientInfo.name,
                version = initParams.clientInfo.version,
                connectedAt = currentTimeMillis()
            )
            sessionManager.addClient(client)
        }

        val result = InitializeResult()
        return json.encodeToJsonElement(result)
    }

    private fun handleToolsList(): JsonElement {
        val result = ToolsListResult(tools = tools.map { it.toDefinition() })
        return json.encodeToJsonElement(result)
    }

    private suspend fun handleToolCall(params: JsonObject?): JsonElement {
        requireNotNull(params) { "tools/call requires params" }
        val callParams = json.decodeFromJsonElement<ToolCallParams>(params)
        val tool = toolMap[callParams.name]
            ?: return json.encodeToJsonElement(
                ToolCallResult(
                    content = listOf(ContentBlock.Text("Unknown tool: ${callParams.name}")),
                    isError = true
                )
            )

        return try {
            val result = tool.handler(callParams.arguments)
            json.encodeToJsonElement(result)
        } catch (e: Exception) {
            json.encodeToJsonElement(
                ToolCallResult(
                    content = listOf(ContentBlock.Text("Error: ${e.message}")),
                    isError = true
                )
            )
        }
    }

    private fun handleResourcesList(): JsonElement {
        val result = ResourcesListResult(resources = resources.map { it.toDefinition() })
        return json.encodeToJsonElement(result)
    }

    private suspend fun handleResourceRead(params: JsonObject?): JsonElement {
        requireNotNull(params) { "resources/read requires params" }
        val readParams = json.decodeFromJsonElement<ResourceReadParams>(params)
        val resource = resourceMap[readParams.uri]
            ?: throw IllegalArgumentException("Unknown resource: ${readParams.uri}")

        val content = resource.handler()
        val result = ResourceReadResult(
            contents = listOf(
                ResourceContent(
                    uri = readParams.uri,
                    mimeType = resource.mimeType,
                    text = content
                )
            )
        )
        return json.encodeToJsonElement(result)
    }

    private var clientCounter = 0
    private fun generateClientId(): String = "client-${++clientCounter}"
}

expect fun currentTimeMillis(): Long
