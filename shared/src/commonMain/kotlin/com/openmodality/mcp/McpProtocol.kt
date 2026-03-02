@file:OptIn(kotlinx.serialization.ExperimentalSerializationApi::class)

package com.openmodality.mcp

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

/**
 * MCP (Model Context Protocol) JSON-RPC 2.0 types.
 * Implements the MCP specification for tool/resource serving.
 */

const val MCP_PROTOCOL_VERSION = "2025-03-26"
const val JSONRPC_VERSION = "2.0"

// -- JSON-RPC base types --

@Serializable
data class JsonRpcRequest(
    val jsonrpc: String = JSONRPC_VERSION,
    val id: JsonElement? = null,
    val method: String,
    val params: JsonObject? = null
)

@Serializable
data class JsonRpcResponse(
    val jsonrpc: String = JSONRPC_VERSION,
    @EncodeDefault(EncodeDefault.Mode.NEVER) val id: JsonElement? = null,
    @EncodeDefault(EncodeDefault.Mode.NEVER) val result: JsonElement? = null,
    @EncodeDefault(EncodeDefault.Mode.NEVER) val error: JsonRpcError? = null
)

@Serializable
data class JsonRpcError(
    val code: Int,
    val message: String,
    @EncodeDefault(EncodeDefault.Mode.NEVER) val data: JsonElement? = null
)

// -- MCP Initialize --

@Serializable
data class InitializeParams(
    val protocolVersion: String,
    val capabilities: ClientCapabilities,
    val clientInfo: ClientInfo
)

@Serializable
data class ClientCapabilities(
    val roots: JsonObject? = null,
    val sampling: JsonObject? = null
)

@Serializable
data class ClientInfo(
    val name: String,
    val version: String
)

@Serializable
data class InitializeResult(
    val protocolVersion: String = MCP_PROTOCOL_VERSION,
    val capabilities: ServerCapabilities = ServerCapabilities(),
    val serverInfo: ServerInfo = ServerInfo()
)

@Serializable
data class ServerCapabilities(
    val tools: ToolsCapability? = ToolsCapability(),
    val resources: ResourcesCapability? = ResourcesCapability()
)

@Serializable
data class ToolsCapability(
    val listChanged: Boolean = false
)

@Serializable
data class ResourcesCapability(
    val subscribe: Boolean = false,
    val listChanged: Boolean = false
)

@Serializable
data class ServerInfo(
    val name: String = "open-modality",
    val version: String = "0.1.0"
)

// -- MCP Tools --

@Serializable
data class ToolDefinition(
    val name: String,
    val description: String,
    val inputSchema: JsonObject
)

@Serializable
data class ToolsListResult(
    val tools: List<ToolDefinition>
)

@Serializable
data class ToolCallParams(
    val name: String,
    val arguments: JsonObject? = null
)

@Serializable
data class ToolCallResult(
    val content: List<ContentBlock>,
    @SerialName("isError")
    val isError: Boolean = false
)

@Serializable
sealed class ContentBlock {
    @Serializable
    @SerialName("text")
    data class Text(
        val text: String,
        val type: String = "text"
    ) : ContentBlock()

    @Serializable
    @SerialName("image")
    data class Image(
        val data: String,
        val mimeType: String,
        val type: String = "image"
    ) : ContentBlock()
}

// -- MCP Resources --

@Serializable
data class ResourceDefinition(
    val uri: String,
    val name: String,
    val description: String? = null,
    val mimeType: String? = "application/json"
)

@Serializable
data class ResourcesListResult(
    val resources: List<ResourceDefinition>
)

@Serializable
data class ResourceReadParams(
    val uri: String
)

@Serializable
data class ResourceReadResult(
    val contents: List<ResourceContent>
)

@Serializable
data class ResourceContent(
    val uri: String,
    val mimeType: String? = "application/json",
    val text: String? = null,
    val blob: String? = null
)
