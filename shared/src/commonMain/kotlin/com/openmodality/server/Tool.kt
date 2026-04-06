package com.openmodality.server

import kotlinx.serialization.json.JsonObject

/**
 * Represents a callable tool exposed over WebSocket.
 */
data class Tool(
    val name: String,
    val description: String,
    val parameters: JsonObject,
    val handler: suspend (params: JsonObject?) -> ToolResult
) {
    fun toInfo(): ToolInfo = ToolInfo(
        name = name,
        description = description,
        parameters = parameters
    )
}
