package com.openmodality.sensor

import com.openmodality.sensor.models.*

/**
 * Platform-specific sensor access.
 * Each platform (iOS/Android) provides an `actual` implementation.
 */
expect class PlatformSensors {

    // -- Capabilities --
    fun availableSensors(): Set<SensorType>
    fun permissionStatus(sensor: SensorType): PermissionStatus
    suspend fun requestPermission(sensor: SensorType): PermissionStatus

    // -- Vision --
    suspend fun takePhoto(camera: CameraType, resolution: Resolution): PhotoResult
    // LiDAR: iOS only, returns null on Android
    suspend fun scanLidar(): String?

    // -- Audio --
    suspend fun recordAudio(durationSeconds: Int, transcribe: Boolean): AudioResult
    suspend fun getAmbientSoundLevel(): AmbientSoundResult

    // -- Location --
    suspend fun getLocation(accuracy: LocationAccuracy): LocationResult
    suspend fun getAddress(): AddressResult

    // -- Motion --
    suspend fun readAccelerometer(): MotionResult
    suspend fun readGyroscope(): MotionResult
    suspend fun readMagnetometer(): MotionResult
    suspend fun getDeviceMotion(): DeviceMotionResult
    suspend fun getPedometer(fromTimestamp: Long?): PedometerResult

    // -- Environment --
    suspend fun readBarometer(): BarometerResult
    suspend fun readAmbientLight(): AmbientLightResult
    suspend fun readProximity(): ProximityResult

    // -- Connectivity --
    suspend fun scanBluetooth(durationSeconds: Int): BluetoothScanResult
    suspend fun scanWifi(): WifiScanResult
    suspend fun readNfc(): NfcResult

    // -- Device --
    suspend fun getBattery(): BatteryResult
    fun getDeviceInfo(): DeviceInfoResult
}

enum class CameraType { FRONT, BACK }
enum class Resolution { LOW, MEDIUM, HIGH }
enum class LocationAccuracy { BEST, BALANCED, LOW_POWER }
