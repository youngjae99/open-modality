@file:OptIn(kotlinx.serialization.ExperimentalSerializationApi::class)

package com.openmodality.server

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

/**
 * WebSocket message protocol for Open Modality.
 *
 * Request-response over WebSocket:
 *   → {"id":"1","method":"take_photo","params":{"camera":"back"}}
 *   ← {"id":"1","result":{...}}
 *
 * Built-in methods:
 *   - list_tools: returns available tools with descriptions and parameter schemas
 *   - get_info: returns device info and available sensors
 *   - ping: health check
 *   - Any registered tool name (e.g. "take_photo", "read_accelerometer")
 */

// -- Request / Response --

@Serializable
data class Request(
    val id: String,
    val method: String,
    @EncodeDefault(EncodeDefault.Mode.NEVER)
    val params: JsonObject? = null
)

@Serializable
data class Response(
    val id: String,
    @EncodeDefault(EncodeDefault.Mode.NEVER)
    val result: JsonElement? = null,
    @EncodeDefault(EncodeDefault.Mode.NEVER)
    val error: ErrorInfo? = null
)

@Serializable
data class ErrorInfo(
    val code: Int,
    val message: String
)

// -- Tool definitions --

@Serializable
data class ToolInfo(
    val name: String,
    val description: String,
    val parameters: JsonObject
)

@Serializable
data class ToolListResult(
    val tools: List<ToolInfo>
)

// -- Tool call results --

@Serializable
data class ToolResult(
    val content: List<ContentItem>,
    @SerialName("isError")
    val isError: Boolean = false
)

@Serializable
sealed class ContentItem {
    @Serializable
    @SerialName("text")
    data class Text(val text: String) : ContentItem()

    @Serializable
    @SerialName("image")
    data class Image(val data: String, val mimeType: String) : ContentItem()
}

// -- Server info (for GET /info and get_info method) --

@Serializable
data class ServerInfoResponse(
    val name: String = "open-modality",
    val version: String = "0.2.0",
    val protocol: String = "websocket",
    val tools: List<ToolInfo>,
    val sensors: List<SensorInfo>
)

@Serializable
data class SensorInfo(
    val id: String,
    val name: String,
    val category: String,
    val permission: String
)
