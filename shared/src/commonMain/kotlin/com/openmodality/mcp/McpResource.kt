package com.openmodality.mcp

/**
 * Represents an MCP resource that can be read by clients.
 */
data class McpResource(
    val uri: String,
    val name: String,
    val description: String,
    val mimeType: String = "application/json",
    val handler: suspend () -> String
) {
    fun toDefinition(): ResourceDefinition = ResourceDefinition(
        uri = uri,
        name = name,
        description = description,
        mimeType = mimeType
    )
}
