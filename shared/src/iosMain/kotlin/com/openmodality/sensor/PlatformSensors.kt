package com.openmodality.sensor

import com.openmodality.sensor.models.*
import platform.UIKit.UIDevice

actual class PlatformSensors {

    actual fun availableSensors(): Set<SensorType> = setOf(
        SensorType.CAMERA_BACK, SensorType.CAMERA_FRONT,
        SensorType.LIDAR, // Available on Pro models
        SensorType.MICROPHONE,
        SensorType.GPS,
        SensorType.ACCELEROMETER, SensorType.GYROSCOPE,
        SensorType.MAGNETOMETER, SensorType.PEDOMETER,
        SensorType.BAROMETER, SensorType.AMBIENT_LIGHT, SensorType.PROXIMITY,
        SensorType.BLUETOOTH, SensorType.WIFI, SensorType.NFC,
        SensorType.BATTERY
    )

    actual fun permissionStatus(sensor: SensorType): PermissionStatus =
        PermissionStatus.NOT_REQUESTED

    actual suspend fun requestPermission(sensor: SensorType): PermissionStatus =
        PermissionStatus.NOT_REQUESTED

    // -- Vision --
    actual suspend fun takePhoto(camera: CameraType, resolution: Resolution): PhotoResult =
        TODO("iOS camera implementation")

    actual suspend fun scanLidar(): String? =
        TODO("iOS LiDAR implementation")

    // -- Audio --
    actual suspend fun recordAudio(durationSeconds: Int, transcribe: Boolean): AudioResult =
        TODO("iOS audio implementation")

    actual suspend fun getAmbientSoundLevel(): AmbientSoundResult =
        TODO("iOS ambient sound implementation")

    // -- Location --
    actual suspend fun getLocation(accuracy: LocationAccuracy): LocationResult =
        TODO("iOS location implementation")

    actual suspend fun getAddress(): AddressResult =
        TODO("iOS geocoding implementation")

    // -- Motion --
    actual suspend fun readAccelerometer(): MotionResult =
        TODO("iOS accelerometer implementation")

    actual suspend fun readGyroscope(): MotionResult =
        TODO("iOS gyroscope implementation")

    actual suspend fun readMagnetometer(): MotionResult =
        TODO("iOS magnetometer implementation")

    actual suspend fun getDeviceMotion(): DeviceMotionResult =
        TODO("iOS device motion implementation")

    actual suspend fun getPedometer(fromTimestamp: Long?): PedometerResult =
        TODO("iOS pedometer implementation")

    // -- Environment --
    actual suspend fun readBarometer(): BarometerResult =
        TODO("iOS barometer implementation")

    actual suspend fun readAmbientLight(): AmbientLightResult =
        TODO("iOS ambient light implementation")

    actual suspend fun readProximity(): ProximityResult =
        TODO("iOS proximity implementation")

    // -- Connectivity --
    actual suspend fun scanBluetooth(durationSeconds: Int): BluetoothScanResult =
        TODO("iOS bluetooth implementation")

    actual suspend fun scanWifi(): WifiScanResult =
        TODO("iOS wifi implementation")

    actual suspend fun readNfc(): NfcResult =
        TODO("iOS NFC implementation")

    // -- Device --
    actual suspend fun getBattery(): BatteryResult =
        TODO("iOS battery implementation")

    actual fun getDeviceInfo(): DeviceInfoResult {
        val device = UIDevice.currentDevice
        return DeviceInfoResult(
            model = device.model,
            manufacturer = "Apple",
            osName = device.systemName,
            osVersion = device.systemVersion,
            screenWidth = 0,
            screenHeight = 0,
            availableSensors = availableSensors().map { it.id }
        )
    }
}
