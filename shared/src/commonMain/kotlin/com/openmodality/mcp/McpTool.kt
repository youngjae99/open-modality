package com.openmodality.mcp

import kotlinx.serialization.json.JsonObject

/**
 * Represents an MCP tool that can be called by clients.
 */
data class McpTool(
    val name: String,
    val description: String,
    val inputSchema: JsonObject,
    val handler: suspend (params: JsonObject?) -> ToolCallResult
) {
    fun toDefinition(): ToolDefinition = ToolDefinition(
        name = name,
        description = description,
        inputSchema = inputSchema
    )
}
