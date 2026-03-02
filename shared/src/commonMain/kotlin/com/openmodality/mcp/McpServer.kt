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
import kotlin.random.Random
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.json.*

/**
 * MCP Server running on the phone.
 * Implements MCP Streamable HTTP transport with OAuth 2.1 PIN-based auth.
 *
 * Clients connect via:
 *   POST http://<phone-ip>:<port>/mcp  (JSON-RPC requests, requires Bearer token)
 *   GET  http://<phone-ip>:<port>/mcp  (SSE stream for server-initiated messages)
 *
 * Auth flow:
 *   1. Claude Code opens browser → GET /oauth/authorize
 *   2. User enters 6-digit PIN shown in the app
 *   3. Server issues authorization code → Claude Code exchanges for Bearer token
 *   4. Subsequent MCP requests carry Authorization: Bearer <token>
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
    private val auth = McpAuth()

    val currentPin: String get() = auth.pin

    private val _isRunning = MutableStateFlow(false)
    val isRunning: StateFlow<Boolean> = _isRunning.asStateFlow()

    val sessions: McpSessionManager get() = sessionManager
    val toolCount: Int get() = tools.size

    private val toolMap: Map<String, McpTool> = tools.associateBy { it.name }
    private val resourceMap: Map<String, McpResource> = resources.associateBy { it.uri }

    fun start() {
        server = embeddedServer(CIO, port = port) {
            install(ContentNegotiation) { json(this@McpServer.json) }
            install(CORS) {
                anyHost()
                allowMethod(HttpMethod.Post)
                allowMethod(HttpMethod.Get)
                allowMethod(HttpMethod.Delete)
                allowHeader(HttpHeaders.ContentType)
                allowHeader(HttpHeaders.Accept)
                allowHeader(HttpHeaders.Authorization)
                allowHeader("Mcp-Session-Id")
                exposeHeader("Mcp-Session-Id")
            }
            install(SSE)

            routing {
                // ── MCP endpoints ────────────────────────────────────────────
                post("/mcp") { handleMcpRequest(call) }

                sse("/mcp") {
                    // Keep connection alive — Claude Code marks the server as failed if this closes
                    while (true) {
                        send(io.ktor.sse.ServerSentEvent(data = """{"jsonrpc":"2.0","method":"ping"}"""))
                        delay(30_000)
                    }
                }

                // ── OAuth 2.1 discovery ──────────────────────────────────────
                // RFC 9728: Protected Resource Metadata
                // authorization_servers points to our own server as the OAuth AS
                get("/.well-known/oauth-protected-resource") {
                    val base = "http://${call.request.host()}:$port"
                    call.respondText(
                        """{"resource":"$base/mcp","authorization_servers":["$base"]}""",
                        ContentType.Application.Json
                    )
                }

                // RFC 8414: Authorization Server Metadata
                get("/.well-known/oauth-authorization-server") {
                    val base = "http://${call.request.host()}:$port"
                    call.respondText(
                        """{"issuer":"$base","authorization_endpoint":"$base/oauth/authorize","token_endpoint":"$base/oauth/token","registration_endpoint":"$base/oauth/register","response_types_supported":["code"],"grant_types_supported":["authorization_code"],"code_challenge_methods_supported":["S256","plain"]}""",
                        ContentType.Application.Json
                    )
                }

                // RFC 7591: Dynamic Client Registration
                post("/oauth/register") {
                    val body = json.parseToJsonElement(call.receiveText()).jsonObject
                    val redirectUris = body["redirect_uris"]?.jsonArray
                        ?.map { it.jsonPrimitive.content } ?: emptyList()
                    val clientId = auth.registerClient(redirectUris)
                    val urisJson = redirectUris.joinToString(",") { "\"$it\"" }
                    call.respondText(
                        """{"client_id":"$clientId","redirect_uris":[$urisJson],"grant_types":["authorization_code"],"response_types":["code"],"token_endpoint_auth_method":"none"}""",
                        ContentType.Application.Json,
                        HttpStatusCode.Created
                    )
                }

                // ── OAuth 2.1 authorization endpoints ───────────────────────
                // GET: show PIN entry form in browser
                get("/oauth/authorize") {
                    val p = call.request.queryParameters
                    val authId = auth.createAuthRequest(
                        clientId = p["client_id"] ?: "",
                        redirectUri = p["redirect_uri"] ?: "",
                        state = p["state"],
                        now = currentTimeMillis()
                    )
                    call.respondText(authorizeHtml(authId), ContentType.Text.Html)
                }

                // POST: validate PIN, redirect with authorization code
                post("/oauth/authorize") {
                    val params = call.receiveParameters()
                    val authId = params["auth_id"] ?: ""
                    val pin = params["pin"] ?: ""
                    val result = auth.validatePinAndCreateCode(authId, pin, currentTimeMillis())
                    if (result == null) {
                        call.respondText(authorizeHtml(authId, error = true), ContentType.Text.Html)
                        return@post
                    }
                    val sep = if ('?' in result.redirectUri) '&' else '?'
                    val stateParam = result.state?.let { "&state=$it" } ?: ""
                    call.respondRedirect("${result.redirectUri}${sep}code=${result.code}$stateParam")
                }

                // POST: exchange authorization code for access token
                post("/oauth/token") {
                    val params = call.receiveParameters()
                    if (params["grant_type"] != "authorization_code") {
                        call.respondText(
                            """{"error":"unsupported_grant_type"}""",
                            ContentType.Application.Json,
                            HttpStatusCode.BadRequest
                        )
                        return@post
                    }
                    val token = auth.exchangeCode(params["code"] ?: "", currentTimeMillis())
                    if (token == null) {
                        call.respondText(
                            """{"error":"invalid_grant"}""",
                            ContentType.Application.Json,
                            HttpStatusCode.BadRequest
                        )
                        return@post
                    }
                    call.respondText(
                        """{"access_token":"$token","token_type":"bearer","expires_in":86400}""",
                        ContentType.Application.Json
                    )
                }

                // ── Health check ─────────────────────────────────────────────
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

    // ── MCP request handler ───────────────────────────────────────────────────

    private suspend fun handleMcpRequest(call: ApplicationCall) {
        val startTime = currentTimeMillis()
        try {
            // Validate Bearer token
            val authHeader = call.request.headers[HttpHeaders.Authorization]
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                call.response.headers.append(
                    HttpHeaders.WWWAuthenticate,
                    """Bearer resource_metadata="http://${call.request.host()}:$port/.well-known/oauth-protected-resource""""
                )
                call.respondText("", status = HttpStatusCode.Unauthorized)
                return
            }
            val token = authHeader.removePrefix("Bearer ")
            if (!auth.validateToken(token)) {
                call.response.headers.append(
                    HttpHeaders.WWWAuthenticate,
                    """Bearer error="invalid_token", resource_metadata="http://${call.request.host()}:$port/.well-known/oauth-protected-resource""""
                )
                call.respondText("", status = HttpStatusCode.Unauthorized)
                return
            }

            val body = call.receiveText()
            val request = json.decodeFromString<JsonRpcRequest>(body)

            // Notifications (no id) → 202 Accepted, no body
            if (request.id == null) {
                sessionManager.logRequest(
                    RequestLogEntry(
                        clientId = null,
                        method = request.method,
                        timestamp = startTime,
                        durationMs = currentTimeMillis() - startTime
                    )
                )
                call.respond(HttpStatusCode.Accepted)
                return
            }

            val result = processRequest(request)
            // MCP Streamable HTTP spec: include Mcp-Session-Id on initialize response
            if (request.method == "initialize") {
                call.response.headers.append("Mcp-Session-Id", randomHex(16))
            }
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
            call.respond(
                HttpStatusCode.OK,
                JsonRpcResponse(error = JsonRpcError(code = -32603, message = e.message ?: "Internal error"))
            )
        }
    }

    // ── JSON-RPC dispatch ─────────────────────────────────────────────────────

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
            sessionManager.addClient(
                ConnectedClient(
                    id = generateClientId(),
                    name = initParams.clientInfo.name,
                    version = initParams.clientInfo.version,
                    connectedAt = currentTimeMillis()
                )
            )
        }
        return json.encodeToJsonElement(InitializeResult())
    }

    private fun handleToolsList(): JsonElement =
        json.encodeToJsonElement(ToolsListResult(tools = tools.map { it.toDefinition() }))

    private suspend fun handleToolCall(params: JsonObject?): JsonElement {
        requireNotNull(params) { "tools/call requires params" }
        val callParams = json.decodeFromJsonElement<ToolCallParams>(params)
        val tool = toolMap[callParams.name]
            ?: return json.encodeToJsonElement(
                ToolCallResult(content = listOf(ContentBlock.Text("Unknown tool: ${callParams.name}")), isError = true)
            )
        return try {
            json.encodeToJsonElement(tool.handler(callParams.arguments))
        } catch (e: Exception) {
            json.encodeToJsonElement(ToolCallResult(content = listOf(ContentBlock.Text("Error: ${e.message}")), isError = true))
        }
    }

    private fun handleResourcesList(): JsonElement =
        json.encodeToJsonElement(ResourcesListResult(resources = resources.map { it.toDefinition() }))

    private suspend fun handleResourceRead(params: JsonObject?): JsonElement {
        requireNotNull(params) { "resources/read requires params" }
        val readParams = json.decodeFromJsonElement<ResourceReadParams>(params)
        val resource = resourceMap[readParams.uri]
            ?: throw IllegalArgumentException("Unknown resource: ${readParams.uri}")
        return json.encodeToJsonElement(
            ResourceReadResult(
                contents = listOf(
                    ResourceContent(uri = readParams.uri, mimeType = resource.mimeType, text = resource.handler())
                )
            )
        )
    }

    // ── OAuth HTML ────────────────────────────────────────────────────────────

    private fun authorizeHtml(authId: String, error: Boolean = false): String {
        val errorHtml = if (error)
            """<p class="error">❌ Wrong PIN. Please try again.</p>"""
        else ""
        return """<!DOCTYPE html>
<html><head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width,initial-scale=1">
<title>Open Modality – Authorize</title>
<style>
*{box-sizing:border-box;margin:0;padding:0}
body{font-family:-apple-system,BlinkMacSystemFont,'Segoe UI',sans-serif;background:#f5f5f7;display:flex;align-items:center;justify-content:center;min-height:100vh}
.card{background:#fff;border-radius:16px;padding:40px;width:360px;box-shadow:0 4px 24px rgba(0,0,0,.12);text-align:center}
.icon{font-size:48px;margin-bottom:16px}
h1{font-size:20px;font-weight:600;margin-bottom:8px;color:#1d1d1f}
p{font-size:14px;color:#6e6e73;margin-bottom:24px;line-height:1.5}
input[type=text]{width:100%;padding:14px;font-size:32px;letter-spacing:14px;text-align:center;border:2px solid #d2d2d7;border-radius:10px;margin-bottom:16px;outline:none;font-family:monospace}
input[type=text]:focus{border-color:#007AFF}
button{width:100%;padding:14px;background:#007AFF;color:#fff;border:none;border-radius:10px;font-size:16px;font-weight:600;cursor:pointer}
button:hover{background:#0056b3}
.error{color:#FF3B30;font-size:14px;margin-bottom:16px}
</style>
</head>
<body><div class="card">
<div class="icon">📱</div>
<h1>Open Modality</h1>
<p>Open the app on your phone and enter the 6-digit PIN shown on screen.</p>
$errorHtml
<form method="POST" action="/oauth/authorize">
<input type="hidden" name="auth_id" value="$authId">
<input type="text" name="pin" maxlength="6" pattern="[0-9]{6}" inputmode="numeric" autocomplete="off" autofocus placeholder="000000">
<button type="submit">Authorize</button>
</form>
</div></body></html>"""
    }

    // ── Utilities ─────────────────────────────────────────────────────────────

    private var clientCounter = 0
    private fun generateClientId(): String = "client-${++clientCounter}"

    private fun randomHex(bytes: Int): String =
        (1..bytes).joinToString("") { Random.nextInt(256).toString(16).padStart(2, '0') }
}

expect fun currentTimeMillis(): Long
