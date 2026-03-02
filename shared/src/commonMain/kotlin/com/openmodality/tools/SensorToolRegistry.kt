package com.openmodality.tools

import com.openmodality.mcp.*
import com.openmodality.sensor.*
import com.openmodality.sensor.models.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.*

/**
 * Registers all sensor tools as MCP tools.
 * Each sensor capability maps to one or more MCP tools.
 */
class SensorToolRegistry(
    private val sensors: PlatformSensors,
    private val json: Json = Json { encodeDefaults = true }
) {
    fun registerAll(): List<McpTool> = buildList {
        addAll(visionTools())
        addAll(audioTools())
        addAll(locationTools())
        addAll(motionTools())
        addAll(environmentTools())
        addAll(connectivityTools())
        addAll(deviceTools())
    }

    fun registerResources(): List<McpResource> = listOf(
        McpResource(
            uri = "sensor://status",
            name = "Sensor Status",
            description = "Available sensors and their permission status"
        ) {
            val available = sensors.availableSensors()
            val statuses = available.map { sensor ->
                buildJsonObject {
                    put("sensor", sensor.id)
                    put("name", sensor.displayName)
                    put("category", sensor.category.name)
                    put("permission", sensors.permissionStatus(sensor).name)
                }
            }
            buildJsonArray { statuses.forEach { add(it) } }.toString()
        },
        McpResource(
            uri = "sensor://capabilities",
            name = "Device Capabilities",
            description = "Device model, platform, and available sensor specifications"
        ) {
            json.encodeToString(DeviceInfoResult.serializer(), sensors.getDeviceInfo())
        }
    )

    // -- Vision Tools --

    private fun visionTools(): List<McpTool> = buildList {
        add(McpTool(
            name = "take_photo",
            description = "Take a photo using the device camera. Returns the image as base64-encoded JPEG.",
            inputSchema = buildSchema {
                string("camera", "Which camera to use", enum = listOf("front", "back"))
                string("resolution", "Image resolution", enum = listOf("low", "medium", "high"))
            }
        ) { params ->
            val camera = when (params?.get("camera")?.jsonPrimitive?.contentOrNull) {
                "front" -> CameraType.FRONT
                else -> CameraType.BACK
            }
            val resolution = when (params?.get("resolution")?.jsonPrimitive?.contentOrNull) {
                "low" -> Resolution.LOW
                "high" -> Resolution.HIGH
                else -> Resolution.MEDIUM
            }
            val result = sensors.takePhoto(camera, resolution)
            ToolCallResult(content = listOf(
                ContentBlock.Image(data = result.base64, mimeType = result.mimeType),
                ContentBlock.Text("Photo taken: ${result.width}x${result.height} (${result.camera})")
            ))
        })

        if (sensors.availableSensors().contains(SensorType.LIDAR)) {
            add(McpTool(
                name = "scan_lidar",
                description = "Scan the environment using LiDAR sensor. Returns 3D depth data. (iOS only)",
                inputSchema = emptySchema()
            ) { _ ->
                val result = sensors.scanLidar()
                ToolCallResult(content = listOf(
                    ContentBlock.Text(result ?: "LiDAR not available on this device")
                ))
            })
        }
    }

    // -- Audio Tools --

    private fun audioTools(): List<McpTool> = listOf(
        McpTool(
            name = "record_audio",
            description = "Record audio from the microphone. Optionally transcribes speech to text.",
            inputSchema = buildSchema {
                integer("duration_seconds", "Recording duration in seconds (1-30)", default = 5, required = true)
                boolean("transcribe", "Whether to transcribe the audio to text", default = true)
            }
        ) { params ->
            val duration = params?.get("duration_seconds")?.jsonPrimitive?.intOrNull ?: 5
            val transcribe = params?.get("transcribe")?.jsonPrimitive?.booleanOrNull ?: true
            val result = sensors.recordAudio(duration.coerceIn(1, 30), transcribe)
            val text = buildString {
                if (result.transcription != null) appendLine("Transcription: ${result.transcription}")
                appendLine("Duration: ${result.durationSeconds}s")
                if (result.averageDecibels != null) appendLine("Average volume: ${result.averageDecibels} dB")
            }
            ToolCallResult(content = listOf(ContentBlock.Text(text)))
        },
        McpTool(
            name = "get_ambient_sound_level",
            description = "Get the current ambient sound level in decibels.",
            inputSchema = emptySchema()
        ) { _ ->
            val result = sensors.getAmbientSoundLevel()
            ToolCallResult(content = listOf(
                ContentBlock.Text(encode(result))
            ))
        }
    )

    // -- Location Tools --

    private fun locationTools(): List<McpTool> = listOf(
        McpTool(
            name = "get_location",
            description = "Get the current GPS location with coordinates, altitude, speed, and heading.",
            inputSchema = buildSchema {
                string("accuracy", "Location accuracy level", enum = listOf("best", "balanced", "low_power"))
            }
        ) { params ->
            val accuracy = when (params?.get("accuracy")?.jsonPrimitive?.contentOrNull) {
                "best" -> LocationAccuracy.BEST
                "low_power" -> LocationAccuracy.LOW_POWER
                else -> LocationAccuracy.BALANCED
            }
            val result = sensors.getLocation(accuracy)
            ToolCallResult(content = listOf(
                ContentBlock.Text(encode(result))
            ))
        },
        McpTool(
            name = "get_address",
            description = "Get the current address via reverse geocoding.",
            inputSchema = emptySchema()
        ) { _ ->
            val result = sensors.getAddress()
            ToolCallResult(content = listOf(
                ContentBlock.Text(encode(result))
            ))
        }
    )

    // -- Motion Tools --

    private fun motionTools(): List<McpTool> = listOf(
        McpTool(
            name = "read_accelerometer",
            description = "Read current accelerometer values (x, y, z in g-force).",
            inputSchema = emptySchema()
        ) { _ ->
            val result = sensors.readAccelerometer()
            ToolCallResult(content = listOf(ContentBlock.Text(encode(result))))
        },
        McpTool(
            name = "read_gyroscope",
            description = "Read current gyroscope rotation rate (x, y, z in rad/s).",
            inputSchema = emptySchema()
        ) { _ ->
            val result = sensors.readGyroscope()
            ToolCallResult(content = listOf(ContentBlock.Text(encode(result))))
        },
        McpTool(
            name = "read_magnetometer",
            description = "Read current magnetometer / compass values (x, y, z in microtesla).",
            inputSchema = emptySchema()
        ) { _ ->
            val result = sensors.readMagnetometer()
            ToolCallResult(content = listOf(ContentBlock.Text(encode(result))))
        },
        McpTool(
            name = "get_device_motion",
            description = "Get combined device motion data: attitude (pitch/roll/yaw), rotation rate, gravity, and user acceleration.",
            inputSchema = emptySchema()
        ) { _ ->
            val result = sensors.getDeviceMotion()
            ToolCallResult(content = listOf(ContentBlock.Text(encode(result))))
        },
        McpTool(
            name = "get_pedometer",
            description = "Get step count and distance data from the pedometer.",
            inputSchema = buildSchema {
                string("from", "Time range: 'today', '1h', or ISO 8601 timestamp")
            }
        ) { params ->
            val from = params?.get("from")?.jsonPrimitive?.contentOrNull
            val timestamp = when (from) {
                "today" -> todayStartMillis()
                "1h" -> currentTimeMillis() - 3600_000
                null -> todayStartMillis()
                else -> from.toLongOrNull() ?: todayStartMillis()
            }
            val result = sensors.getPedometer(timestamp)
            ToolCallResult(content = listOf(ContentBlock.Text(encode(result))))
        }
    )

    // -- Environment Tools --

    private fun environmentTools(): List<McpTool> = listOf(
        McpTool(
            name = "read_barometer",
            description = "Read barometric pressure (hPa) and relative altitude change (meters).",
            inputSchema = emptySchema()
        ) { _ ->
            val result = sensors.readBarometer()
            ToolCallResult(content = listOf(ContentBlock.Text(encode(result))))
        },
        McpTool(
            name = "read_ambient_light",
            description = "Read ambient light level in lux.",
            inputSchema = emptySchema()
        ) { _ ->
            val result = sensors.readAmbientLight()
            ToolCallResult(content = listOf(ContentBlock.Text(encode(result))))
        },
        McpTool(
            name = "read_proximity",
            description = "Read proximity sensor. Detects if something is near the device screen.",
            inputSchema = emptySchema()
        ) { _ ->
            val result = sensors.readProximity()
            ToolCallResult(content = listOf(ContentBlock.Text(encode(result))))
        }
    )

    // -- Connectivity Tools --

    private fun connectivityTools(): List<McpTool> = listOf(
        McpTool(
            name = "scan_bluetooth",
            description = "Scan for nearby Bluetooth Low Energy (BLE) devices.",
            inputSchema = buildSchema {
                integer("duration_seconds", "Scan duration in seconds (1-30)", default = 5)
            }
        ) { params ->
            val duration = params?.get("duration_seconds")?.jsonPrimitive?.intOrNull ?: 5
            val result = sensors.scanBluetooth(duration.coerceIn(1, 30))
            ToolCallResult(content = listOf(ContentBlock.Text(encode(result))))
        },
        McpTool(
            name = "scan_wifi",
            description = "Scan for nearby WiFi networks.",
            inputSchema = emptySchema()
        ) { _ ->
            val result = sensors.scanWifi()
            ToolCallResult(content = listOf(ContentBlock.Text(encode(result))))
        },
        McpTool(
            name = "read_nfc",
            description = "Read an NFC tag. The user will need to hold the device near the tag.",
            inputSchema = emptySchema()
        ) { _ ->
            val result = sensors.readNfc()
            ToolCallResult(content = listOf(ContentBlock.Text(encode(result))))
        }
    )

    // -- Device Tools --

    private fun deviceTools(): List<McpTool> = listOf(
        McpTool(
            name = "get_battery",
            description = "Get battery level, charging status, and thermal state.",
            inputSchema = emptySchema()
        ) { _ ->
            val result = sensors.getBattery()
            ToolCallResult(content = listOf(ContentBlock.Text(encode(result))))
        },
        McpTool(
            name = "get_device_info",
            description = "Get device model, OS version, screen size, and list of available sensors.",
            inputSchema = emptySchema()
        ) { _ ->
            val result = sensors.getDeviceInfo()
            ToolCallResult(content = listOf(ContentBlock.Text(encode(result))))
        }
    )

    private inline fun <reified T> encode(value: T): String =
        json.encodeToString(value)

    private fun todayStartMillis(): Long {
        val now = currentTimeMillis()
        return now - (now % 86_400_000)
    }
}
