package com.openmodality.tools

import com.openmodality.server.*
import com.openmodality.sensor.*
import com.openmodality.sensor.models.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.*

/**
 * Registers all sensor capabilities as callable tools.
 */
class SensorToolRegistry(
    private val sensors: PlatformSensors,
    private val json: Json = Json { encodeDefaults = true }
) {
    fun registerAll(): List<Tool> = buildList {
        addAll(visionTools())
        addAll(audioTools())
        addAll(locationTools())
        addAll(motionTools())
        addAll(environmentTools())
        addAll(connectivityTools())
        addAll(deviceTools())
    }

    // -- Vision Tools --

    private fun visionTools(): List<Tool> = buildList {
        add(Tool(
            name = "take_photo",
            description = "Take a photo using the device camera. Returns the image as base64-encoded JPEG.",
            parameters = buildSchema {
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
            ToolResult(content = listOf(
                ContentItem.Image(data = result.base64, mimeType = result.mimeType),
                ContentItem.Text("Photo taken: ${result.width}x${result.height} (${result.camera})")
            ))
        })

        if (sensors.availableSensors().contains(SensorType.LIDAR)) {
            add(Tool(
                name = "scan_lidar",
                description = "Scan the environment using LiDAR sensor. Returns 3D depth data. (iOS only)",
                parameters = emptySchema()
            ) { _ ->
                val result = sensors.scanLidar()
                ToolResult(content = listOf(
                    ContentItem.Text(result ?: "LiDAR not available on this device")
                ))
            })
        }
    }

    // -- Audio Tools --

    private fun audioTools(): List<Tool> = listOf(
        Tool(
            name = "record_audio",
            description = "Record audio from the microphone. Optionally transcribes speech to text.",
            parameters = buildSchema {
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
            ToolResult(content = listOf(ContentItem.Text(text)))
        },
        Tool(
            name = "get_ambient_sound_level",
            description = "Get the current ambient sound level in decibels.",
            parameters = emptySchema()
        ) { _ ->
            val result = sensors.getAmbientSoundLevel()
            ToolResult(content = listOf(ContentItem.Text(encode(result))))
        }
    )

    // -- Location Tools --

    private fun locationTools(): List<Tool> = listOf(
        Tool(
            name = "get_location",
            description = "Get the current GPS location with coordinates, altitude, speed, and heading.",
            parameters = buildSchema {
                string("accuracy", "Location accuracy level", enum = listOf("best", "balanced", "low_power"))
            }
        ) { params ->
            val accuracy = when (params?.get("accuracy")?.jsonPrimitive?.contentOrNull) {
                "best" -> LocationAccuracy.BEST
                "low_power" -> LocationAccuracy.LOW_POWER
                else -> LocationAccuracy.BALANCED
            }
            val result = sensors.getLocation(accuracy)
            ToolResult(content = listOf(ContentItem.Text(encode(result))))
        },
        Tool(
            name = "get_address",
            description = "Get the current address via reverse geocoding.",
            parameters = emptySchema()
        ) { _ ->
            val result = sensors.getAddress()
            ToolResult(content = listOf(ContentItem.Text(encode(result))))
        }
    )

    // -- Motion Tools --

    private fun motionTools(): List<Tool> = listOf(
        Tool(
            name = "read_accelerometer",
            description = "Read current accelerometer values (x, y, z in g-force).",
            parameters = emptySchema()
        ) { _ ->
            val result = sensors.readAccelerometer()
            ToolResult(content = listOf(ContentItem.Text(encode(result))))
        },
        Tool(
            name = "read_gyroscope",
            description = "Read current gyroscope rotation rate (x, y, z in rad/s).",
            parameters = emptySchema()
        ) { _ ->
            val result = sensors.readGyroscope()
            ToolResult(content = listOf(ContentItem.Text(encode(result))))
        },
        Tool(
            name = "read_magnetometer",
            description = "Read current magnetometer / compass values (x, y, z in microtesla).",
            parameters = emptySchema()
        ) { _ ->
            val result = sensors.readMagnetometer()
            ToolResult(content = listOf(ContentItem.Text(encode(result))))
        },
        Tool(
            name = "get_device_motion",
            description = "Get combined device motion data: attitude (pitch/roll/yaw), rotation rate, gravity, and user acceleration.",
            parameters = emptySchema()
        ) { _ ->
            val result = sensors.getDeviceMotion()
            ToolResult(content = listOf(ContentItem.Text(encode(result))))
        },
        Tool(
            name = "get_pedometer",
            description = "Get step count and distance data from the pedometer.",
            parameters = buildSchema {
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
            ToolResult(content = listOf(ContentItem.Text(encode(result))))
        }
    )

    // -- Environment Tools --

    private fun environmentTools(): List<Tool> = listOf(
        Tool(
            name = "read_barometer",
            description = "Read barometric pressure (hPa) and relative altitude change (meters).",
            parameters = emptySchema()
        ) { _ ->
            val result = sensors.readBarometer()
            ToolResult(content = listOf(ContentItem.Text(encode(result))))
        },
        Tool(
            name = "read_ambient_light",
            description = "Read ambient light level in lux.",
            parameters = emptySchema()
        ) { _ ->
            val result = sensors.readAmbientLight()
            ToolResult(content = listOf(ContentItem.Text(encode(result))))
        },
        Tool(
            name = "read_proximity",
            description = "Read proximity sensor. Detects if something is near the device screen.",
            parameters = emptySchema()
        ) { _ ->
            val result = sensors.readProximity()
            ToolResult(content = listOf(ContentItem.Text(encode(result))))
        }
    )

    // -- Connectivity Tools --

    private fun connectivityTools(): List<Tool> = listOf(
        Tool(
            name = "scan_bluetooth",
            description = "Scan for nearby Bluetooth Low Energy (BLE) devices.",
            parameters = buildSchema {
                integer("duration_seconds", "Scan duration in seconds (1-30)", default = 5)
            }
        ) { params ->
            val duration = params?.get("duration_seconds")?.jsonPrimitive?.intOrNull ?: 5
            val result = sensors.scanBluetooth(duration.coerceIn(1, 30))
            ToolResult(content = listOf(ContentItem.Text(encode(result))))
        },
        Tool(
            name = "scan_wifi",
            description = "Scan for nearby WiFi networks.",
            parameters = emptySchema()
        ) { _ ->
            val result = sensors.scanWifi()
            ToolResult(content = listOf(ContentItem.Text(encode(result))))
        },
        Tool(
            name = "read_nfc",
            description = "Read an NFC tag. The user will need to hold the device near the tag.",
            parameters = emptySchema()
        ) { _ ->
            val result = sensors.readNfc()
            ToolResult(content = listOf(ContentItem.Text(encode(result))))
        }
    )

    // -- Device Tools --

    private fun deviceTools(): List<Tool> = listOf(
        Tool(
            name = "get_battery",
            description = "Get battery level, charging status, and thermal state.",
            parameters = emptySchema()
        ) { _ ->
            val result = sensors.getBattery()
            ToolResult(content = listOf(ContentItem.Text(encode(result))))
        },
        Tool(
            name = "get_device_info",
            description = "Get device model, OS version, screen size, and list of available sensors.",
            parameters = emptySchema()
        ) { _ ->
            val result = sensors.getDeviceInfo()
            ToolResult(content = listOf(ContentItem.Text(encode(result))))
        }
    )

    private inline fun <reified T> encode(value: T): String =
        json.encodeToString(value)

    private fun todayStartMillis(): Long {
        val now = currentTimeMillis()
        return now - (now % 86_400_000)
    }
}
