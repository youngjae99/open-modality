package com.openmodality.sensor.models

import kotlinx.serialization.Serializable

@Serializable
data class PhotoResult(
    val base64: String,
    val mimeType: String = "image/jpeg",
    val width: Int,
    val height: Int,
    val camera: String,
    val timestamp: Long
)

@Serializable
data class LocationResult(
    val latitude: Double,
    val longitude: Double,
    val altitude: Double?,
    val accuracy: Float,
    val speed: Float?,
    val heading: Float?,
    val timestamp: Long
)

@Serializable
data class AddressResult(
    val formattedAddress: String,
    val street: String?,
    val city: String?,
    val state: String?,
    val country: String?,
    val postalCode: String?
)

@Serializable
data class MotionResult(
    val x: Double,
    val y: Double,
    val z: Double,
    val timestamp: Long
)

@Serializable
data class DeviceMotionResult(
    val attitude: Attitude,
    val rotationRate: MotionResult,
    val gravity: MotionResult,
    val userAcceleration: MotionResult,
    val timestamp: Long
)

@Serializable
data class Attitude(
    val pitch: Double,
    val roll: Double,
    val yaw: Double
)

@Serializable
data class PedometerResult(
    val steps: Long,
    val distanceMeters: Double?,
    val floorsAscended: Int?,
    val floorsDescended: Int?,
    val startTime: Long,
    val endTime: Long
)

@Serializable
data class BarometerResult(
    val pressureHPa: Double,
    val relativeAltitudeMeters: Double?,
    val timestamp: Long
)

@Serializable
data class AmbientLightResult(
    val lux: Float,
    val timestamp: Long
)

@Serializable
data class ProximityResult(
    val isNear: Boolean,
    val distanceCm: Float?,
    val timestamp: Long
)

@Serializable
data class AudioResult(
    val transcription: String?,
    val durationSeconds: Double,
    val averageDecibels: Float?,
    val timestamp: Long
)

@Serializable
data class AmbientSoundResult(
    val decibels: Float,
    val timestamp: Long
)

@Serializable
data class BluetoothDevice(
    val name: String?,
    val identifier: String,
    val rssi: Int,
    val type: String?
)

@Serializable
data class BluetoothScanResult(
    val devices: List<BluetoothDevice>,
    val scanDurationSeconds: Double,
    val timestamp: Long
)

@Serializable
data class WifiNetwork(
    val ssid: String?,
    val bssid: String?,
    val rssi: Int,
    val frequency: Int?,
    val security: String?
)

@Serializable
data class WifiScanResult(
    val networks: List<WifiNetwork>,
    val timestamp: Long
)

@Serializable
data class NfcResult(
    val tagId: String?,
    val payload: String?,
    val type: String?,
    val timestamp: Long
)

@Serializable
data class BatteryResult(
    val level: Float,
    val isCharging: Boolean,
    val thermalState: String?,
    val timestamp: Long
)

@Serializable
data class DeviceInfoResult(
    val model: String,
    val manufacturer: String,
    val osName: String,
    val osVersion: String,
    val screenWidth: Int,
    val screenHeight: Int,
    val availableSensors: List<String>
)
