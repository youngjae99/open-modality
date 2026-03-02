package com.openmodality.sensor

import com.openmodality.sensor.models.*

actual class PlatformSensors {

    actual fun availableSensors(): Set<SensorType> = setOf(
        SensorType.CAMERA_BACK, SensorType.CAMERA_FRONT,
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
        TODO("Android camera implementation")

    actual suspend fun scanLidar(): String? = null // Not available on Android

    // -- Audio --
    actual suspend fun recordAudio(durationSeconds: Int, transcribe: Boolean): AudioResult =
        TODO("Android audio implementation")

    actual suspend fun getAmbientSoundLevel(): AmbientSoundResult =
        TODO("Android ambient sound implementation")

    // -- Location --
    actual suspend fun getLocation(accuracy: LocationAccuracy): LocationResult =
        TODO("Android location implementation")

    actual suspend fun getAddress(): AddressResult =
        TODO("Android geocoding implementation")

    // -- Motion --
    actual suspend fun readAccelerometer(): MotionResult =
        TODO("Android accelerometer implementation")

    actual suspend fun readGyroscope(): MotionResult =
        TODO("Android gyroscope implementation")

    actual suspend fun readMagnetometer(): MotionResult =
        TODO("Android magnetometer implementation")

    actual suspend fun getDeviceMotion(): DeviceMotionResult =
        TODO("Android device motion implementation")

    actual suspend fun getPedometer(fromTimestamp: Long?): PedometerResult =
        TODO("Android pedometer implementation")

    // -- Environment --
    actual suspend fun readBarometer(): BarometerResult =
        TODO("Android barometer implementation")

    actual suspend fun readAmbientLight(): AmbientLightResult =
        TODO("Android ambient light implementation")

    actual suspend fun readProximity(): ProximityResult =
        TODO("Android proximity implementation")

    // -- Connectivity --
    actual suspend fun scanBluetooth(durationSeconds: Int): BluetoothScanResult =
        TODO("Android bluetooth implementation")

    actual suspend fun scanWifi(): WifiScanResult =
        TODO("Android wifi implementation")

    actual suspend fun readNfc(): NfcResult =
        TODO("Android NFC implementation")

    // -- Device --
    actual suspend fun getBattery(): BatteryResult =
        TODO("Android battery implementation")

    actual fun getDeviceInfo(): DeviceInfoResult = DeviceInfoResult(
        model = android.os.Build.MODEL,
        manufacturer = android.os.Build.MANUFACTURER,
        osName = "Android",
        osVersion = android.os.Build.VERSION.RELEASE,
        screenWidth = 0,
        screenHeight = 0,
        availableSensors = availableSensors().map { it.id }
    )
}
