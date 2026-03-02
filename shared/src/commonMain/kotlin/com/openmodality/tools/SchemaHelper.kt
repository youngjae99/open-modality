package com.openmodality.tools

import kotlinx.serialization.json.*

/**
 * Helpers for building JSON Schema objects for MCP tool inputSchema.
 */
fun emptySchema(): JsonObject = buildJsonObject {
    put("type", "object")
    put("properties", buildJsonObject {})
}

fun buildSchema(block: SchemaBuilder.() -> Unit): JsonObject {
    val builder = SchemaBuilder()
    builder.block()
    return builder.build()
}

class SchemaBuilder {
    private val properties = mutableMapOf<String, JsonObject>()
    private val required = mutableListOf<String>()

    fun string(name: String, description: String, enum: List<String>? = null, required: Boolean = false) {
        properties[name] = buildJsonObject {
            put("type", "string")
            put("description", description)
            if (enum != null) {
                put("enum", buildJsonArray { enum.forEach { add(it) } })
            }
        }
        if (required) this.required.add(name)
    }

    fun integer(name: String, description: String, default: Int? = null, required: Boolean = false) {
        properties[name] = buildJsonObject {
            put("type", "integer")
            put("description", description)
            if (default != null) put("default", default)
        }
        if (required) this.required.add(name)
    }

    fun boolean(name: String, description: String, default: Boolean? = null, required: Boolean = false) {
        properties[name] = buildJsonObject {
            put("type", "boolean")
            put("description", description)
            if (default != null) put("default", default)
        }
        if (required) this.required.add(name)
    }

    fun build(): JsonObject = buildJsonObject {
        put("type", "object")
        put("properties", buildJsonObject {
            properties.forEach { (k, v) -> put(k, v) }
        })
        if (required.isNotEmpty()) {
            put("required", buildJsonArray { required.forEach { add(it) } })
        }
    }
}
