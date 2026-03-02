package com.openmodality.sensor

import kotlinx.serialization.Serializable

@Serializable
enum class SensorType(val id: String, val displayName: String, val category: SensorCategory) {
    // Vision
    CAMERA_BACK("camera_back", "Back Camera", SensorCategory.VISION),
    CAMERA_FRONT("camera_front", "Front Camera", SensorCategory.VISION),
    LIDAR("lidar", "LiDAR Scanner", SensorCategory.VISION),

    // Audio
    MICROPHONE("microphone", "Microphone", SensorCategory.AUDIO),

    // Location
    GPS("gps", "GPS / Location", SensorCategory.LOCATION),

    // Motion
    ACCELEROMETER("accelerometer", "Accelerometer", SensorCategory.MOTION),
    GYROSCOPE("gyroscope", "Gyroscope", SensorCategory.MOTION),
    MAGNETOMETER("magnetometer", "Magnetometer", SensorCategory.MOTION),
    PEDOMETER("pedometer", "Pedometer", SensorCategory.MOTION),

    // Environment
    BAROMETER("barometer", "Barometer", SensorCategory.ENVIRONMENT),
    AMBIENT_LIGHT("ambient_light", "Ambient Light", SensorCategory.ENVIRONMENT),
    PROXIMITY("proximity", "Proximity Sensor", SensorCategory.ENVIRONMENT),

    // Connectivity
    BLUETOOTH("bluetooth", "Bluetooth LE", SensorCategory.CONNECTIVITY),
    WIFI("wifi", "WiFi Scanner", SensorCategory.CONNECTIVITY),
    NFC("nfc", "NFC", SensorCategory.CONNECTIVITY),

    // Device
    BATTERY("battery", "Battery", SensorCategory.DEVICE),
}

@Serializable
enum class SensorCategory {
    VISION, AUDIO, LOCATION, MOTION, ENVIRONMENT, CONNECTIVITY, DEVICE
}

@Serializable
enum class PermissionStatus {
    GRANTED, DENIED, NOT_REQUESTED, RESTRICTED
}
