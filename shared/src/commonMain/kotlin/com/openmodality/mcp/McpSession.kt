package com.openmodality.mcp

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Tracks connected MCP client sessions.
 */
data class ConnectedClient(
    val id: String,
    val name: String,
    val version: String,
    val connectedAt: Long
)

class McpSessionManager {
    private val _clients = MutableStateFlow<List<ConnectedClient>>(emptyList())
    val clients: StateFlow<List<ConnectedClient>> = _clients.asStateFlow()

    private val _requestLog = MutableStateFlow<List<RequestLogEntry>>(emptyList())
    val requestLog: StateFlow<List<RequestLogEntry>> = _requestLog.asStateFlow()

    fun addClient(client: ConnectedClient) {
        _clients.value = _clients.value + client
    }

    fun removeClient(clientId: String) {
        _clients.value = _clients.value.filter { it.id != clientId }
    }

    fun logRequest(entry: RequestLogEntry) {
        val current = _requestLog.value
        // Keep last 100 entries
        _requestLog.value = (current + entry).takeLast(100)
    }
}

data class RequestLogEntry(
    val clientId: String?,
    val method: String,
    val toolName: String? = null,
    val timestamp: Long,
    val durationMs: Long? = null,
    val success: Boolean = true,
    val error: String? = null
)
