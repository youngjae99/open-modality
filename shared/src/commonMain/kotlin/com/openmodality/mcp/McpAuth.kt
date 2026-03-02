package com.openmodality.mcp

import kotlin.random.Random

/**
 * Minimal OAuth 2.1 Authorization Code state manager.
 * Security is provided by the 6-digit PIN shown in the app.
 */
class McpAuth {
    private var _pin: String = generatePin()
    val pin: String get() = _pin

    // authId → pending request (TTL: 5 min)
    private val pendingRequests = mutableMapOf<String, PendingAuthRequest>()
    // code → auth code entry (TTL: 1 min)
    private val pendingCodes = mutableMapOf<String, AuthCodeEntry>()
    // token → entry (no expiry for local server)
    private val accessTokens = mutableMapOf<String, AccessTokenEntry>()

    fun createAuthRequest(clientId: String, redirectUri: String, state: String?, now: Long): String {
        val authId = randomHex(16)
        pendingRequests[authId] = PendingAuthRequest(
            authId = authId,
            clientId = clientId,
            redirectUri = redirectUri,
            state = state,
            createdAt = now
        )
        return authId
    }

    /** Returns AuthCodeResult on success, null if PIN wrong or authId expired/unknown. */
    fun validatePinAndCreateCode(authId: String, pin: String, now: Long): AuthCodeResult? {
        val request = pendingRequests[authId] ?: return null
        if (now - request.createdAt > 5 * 60 * 1000L) {
            pendingRequests.remove(authId)
            return null
        }
        if (pin != _pin) return null // wrong PIN — keep request alive for retry

        pendingRequests.remove(authId)
        val code = randomHex(32)
        pendingCodes[code] = AuthCodeEntry(
            clientId = request.clientId,
            redirectUri = request.redirectUri,
            createdAt = now
        )
        return AuthCodeResult(code = code, state = request.state, redirectUri = request.redirectUri)
    }

    /** Exchanges authorization code for access token. Returns token or null if invalid/expired. */
    fun exchangeCode(code: String, now: Long): String? {
        val entry = pendingCodes.remove(code) ?: return null
        if (now - entry.createdAt > 60 * 1000L) return null // 1 min TTL
        val token = randomHex(32)
        accessTokens[token] = AccessTokenEntry(clientId = entry.clientId)
        return token
    }

    fun validateToken(token: String): Boolean = accessTokens.containsKey(token)

    // Dynamic Client Registration (RFC 7591)
    private val registeredClients = mutableMapOf<String, List<String>>() // clientId → redirectUris

    fun registerClient(redirectUris: List<String>): String {
        val clientId = randomHex(16)
        registeredClients[clientId] = redirectUris
        return clientId
    }

    private fun generatePin(): String = (100000 + Random.nextInt(900000)).toString()

    private fun randomHex(bytes: Int): String =
        (1..bytes).map { Random.nextInt(256).toString(16).padStart(2, '0') }.joinToString("")
}

internal data class PendingAuthRequest(
    val authId: String,
    val clientId: String,
    val redirectUri: String,
    val state: String?,
    val createdAt: Long
)

internal data class AuthCodeEntry(
    val clientId: String,
    val redirectUri: String,
    val createdAt: Long
)

data class AuthCodeResult(
    val code: String,
    val state: String?,
    val redirectUri: String
)

internal data class AccessTokenEntry(val clientId: String)
