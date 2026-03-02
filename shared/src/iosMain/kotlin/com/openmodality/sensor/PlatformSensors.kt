@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class, kotlinx.cinterop.BetaInteropApi::class)

package com.openmodality.sensor

import com.openmodality.sensor.models.*
import kotlinx.cinterop.*
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import platform.CoreLocation.*
import platform.CoreMotion.*
import platform.Foundation.*
import platform.UIKit.UIDevice
import platform.UIKit.UIDeviceBatteryState
import platform.UIKit.UIScreen
import platform.darwin.NSObject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

actual class PlatformSensors {

    private val motionManager: CMMotionManager by lazy {
        CMMotionManager()
    }

    private val altimeter: CMAltimeter by lazy {
        CMAltimeter()
    }

    private val pedometer: CMPedometer by lazy {
        CMPedometer()
    }

    actual fun availableSensors(): Set<SensorType> = buildSet {
        add(SensorType.BATTERY)

        if (motionManager.accelerometerAvailable) add(SensorType.ACCELEROMETER)
        if (motionManager.gyroAvailable) add(SensorType.GYROSCOPE)
        if (motionManager.magnetometerAvailable) add(SensorType.MAGNETOMETER)
        if (CMAltimeter.isRelativeAltitudeAvailable()) add(SensorType.BAROMETER)
        if (CMPedometer.isStepCountingAvailable()) add(SensorType.PEDOMETER)

        add(SensorType.GPS)
        add(SensorType.CAMERA_BACK)
        add(SensorType.CAMERA_FRONT)
        add(SensorType.MICROPHONE)
        add(SensorType.PROXIMITY)
        add(SensorType.BLUETOOTH)
        add(SensorType.WIFI)
        add(SensorType.NFC)
    }

    actual fun permissionStatus(sensor: SensorType): PermissionStatus =
        PermissionStatus.NOT_REQUESTED

    actual suspend fun requestPermission(sensor: SensorType): PermissionStatus =
        PermissionStatus.NOT_REQUESTED

    // -- Vision --

    actual suspend fun takePhoto(camera: CameraType, resolution: Resolution): PhotoResult {
        throw UnsupportedOperationException("Camera not yet implemented - coming in Phase 2")
    }

    actual suspend fun scanLidar(): String? = null

    // -- Audio --

    actual suspend fun recordAudio(durationSeconds: Int, transcribe: Boolean): AudioResult {
        throw UnsupportedOperationException("Audio recording not yet implemented - coming in Phase 2")
    }

    actual suspend fun getAmbientSoundLevel(): AmbientSoundResult {
        return AmbientSoundResult(
            decibels = -1f,
            timestamp = currentTimeMs()
        )
    }

    // -- Location --

    actual suspend fun getLocation(accuracy: LocationAccuracy): LocationResult {
        return suspendCancellableCoroutine { cont ->
            val locationManager = CLLocationManager()
            locationManager.desiredAccuracy = when (accuracy) {
                LocationAccuracy.BEST -> kCLLocationAccuracyBest
                LocationAccuracy.BALANCED -> kCLLocationAccuracyHundredMeters
                LocationAccuracy.LOW_POWER -> kCLLocationAccuracyKilometer
            }

            val delegate = object : NSObject(), CLLocationManagerDelegateProtocol {
                override fun locationManager(manager: CLLocationManager, didUpdateLocations: List<*>) {
                    val location = didUpdateLocations.lastOrNull() as? CLLocation
                    if (location != null) {
                        manager.stopUpdatingLocation()
                        val coord = location.coordinate
                        cont.resume(
                            LocationResult(
                                latitude = coord.useContents { latitude },
                                longitude = coord.useContents { longitude },
                                altitude = location.altitude,
                                accuracy = location.horizontalAccuracy.toFloat(),
                                speed = location.speed.toFloat(),
                                heading = location.course.toFloat(),
                                timestamp = (location.timestamp.timeIntervalSince1970 * 1000).toLong()
                            )
                        )
                    }
                }

                override fun locationManager(manager: CLLocationManager, didFailWithError: NSError) {
                    cont.resumeWithException(
                        RuntimeException("Location error: ${didFailWithError.localizedDescription}")
                    )
                }
            }

            locationManager.delegate = delegate
            locationManager.requestWhenInUseAuthorization()
            locationManager.startUpdatingLocation()

            cont.invokeOnCancellation {
                locationManager.stopUpdatingLocation()
            }
        }
    }

    actual suspend fun getAddress(): AddressResult {
        val location = getLocation(LocationAccuracy.BALANCED)
        return suspendCancellableCoroutine { cont ->
            val geocoder = CLGeocoder()
            val clLocation = CLLocation(
                latitude = location.latitude,
                longitude = location.longitude
            )
            geocoder.reverseGeocodeLocation(clLocation) { placemarks, error ->
                if (error != null) {
                    cont.resume(
                        AddressResult(
                            formattedAddress = "Unknown",
                            street = null, city = null, state = null,
                            country = null, postalCode = null
                        )
                    )
                    return@reverseGeocodeLocation
                }
                val placemark = placemarks?.firstOrNull() as? CLPlacemark
                cont.resume(
                    AddressResult(
                        formattedAddress = buildString {
                            placemark?.thoroughfare?.let { append("$it, ") }
                            placemark?.locality?.let { append("$it, ") }
                            placemark?.administrativeArea?.let { append("$it, ") }
                            placemark?.country?.let { append(it) }
                        }.ifEmpty { "Unknown" },
                        street = placemark?.thoroughfare,
                        city = placemark?.locality,
                        state = placemark?.administrativeArea,
                        country = placemark?.country,
                        postalCode = placemark?.postalCode
                    )
                )
            }
        }
    }

    // -- Motion sensors --

    actual suspend fun readAccelerometer(): MotionResult {
        return withTimeoutOrNull(2000L) {
            suspendCancellableCoroutine { cont ->
                motionManager.startAccelerometerUpdatesToQueue(
                    NSOperationQueue.mainQueue
                ) { data, error ->
                    motionManager.stopAccelerometerUpdates()
                    if (data != null) {
                        val accel = data.acceleration
                        cont.resume(
                            MotionResult(
                                x = accel.useContents { x },
                                y = accel.useContents { y },
                                z = accel.useContents { z },
                                timestamp = currentTimeMs()
                            )
                        )
                    } else {
                        cont.resumeWithException(
                            RuntimeException("Accelerometer error: ${error?.localizedDescription}")
                        )
                    }
                }
                cont.invokeOnCancellation {
                    motionManager.stopAccelerometerUpdates()
                }
            }
        } ?: throw RuntimeException("Accelerometer read timed out")
    }

    actual suspend fun readGyroscope(): MotionResult {
        return withTimeoutOrNull(2000L) {
            suspendCancellableCoroutine { cont ->
                motionManager.startGyroUpdatesToQueue(
                    NSOperationQueue.mainQueue
                ) { data, error ->
                    motionManager.stopGyroUpdates()
                    if (data != null) {
                        val rate = data.rotationRate
                        cont.resume(
                            MotionResult(
                                x = rate.useContents { x },
                                y = rate.useContents { y },
                                z = rate.useContents { z },
                                timestamp = currentTimeMs()
                            )
                        )
                    } else {
                        cont.resumeWithException(
                            RuntimeException("Gyroscope error: ${error?.localizedDescription}")
                        )
                    }
                }
                cont.invokeOnCancellation {
                    motionManager.stopGyroUpdates()
                }
            }
        } ?: throw RuntimeException("Gyroscope read timed out")
    }

    actual suspend fun readMagnetometer(): MotionResult {
        return withTimeoutOrNull(2000L) {
            suspendCancellableCoroutine { cont ->
                motionManager.startMagnetometerUpdatesToQueue(
                    NSOperationQueue.mainQueue
                ) { data, error ->
                    motionManager.stopMagnetometerUpdates()
                    if (data != null) {
                        val field = data.magneticField
                        cont.resume(
                            MotionResult(
                                x = field.useContents { x },
                                y = field.useContents { y },
                                z = field.useContents { z },
                                timestamp = currentTimeMs()
                            )
                        )
                    } else {
                        cont.resumeWithException(
                            RuntimeException("Magnetometer error: ${error?.localizedDescription}")
                        )
                    }
                }
                cont.invokeOnCancellation {
                    motionManager.stopMagnetometerUpdates()
                }
            }
        } ?: throw RuntimeException("Magnetometer read timed out")
    }

    actual suspend fun getDeviceMotion(): DeviceMotionResult {
        return withTimeoutOrNull(2000L) {
            suspendCancellableCoroutine { cont ->
                motionManager.startDeviceMotionUpdatesToQueue(
                    NSOperationQueue.mainQueue
                ) { motion, error ->
                    motionManager.stopDeviceMotionUpdates()
                    if (motion != null) {
                        val now = currentTimeMs()

                        cont.resume(
                            DeviceMotionResult(
                                attitude = Attitude(
                                    pitch = motion.attitude.pitch,
                                    roll = motion.attitude.roll,
                                    yaw = motion.attitude.yaw
                                ),
                                rotationRate = MotionResult(
                                    x = motion.rotationRate.useContents { x },
                                    y = motion.rotationRate.useContents { y },
                                    z = motion.rotationRate.useContents { z },
                                    timestamp = now
                                ),
                                gravity = MotionResult(
                                    x = motion.gravity.useContents { x },
                                    y = motion.gravity.useContents { y },
                                    z = motion.gravity.useContents { z },
                                    timestamp = now
                                ),
                                userAcceleration = MotionResult(
                                    x = motion.userAcceleration.useContents { x },
                                    y = motion.userAcceleration.useContents { y },
                                    z = motion.userAcceleration.useContents { z },
                                    timestamp = now
                                ),
                                timestamp = now
                            )
                        )
                    } else {
                        cont.resumeWithException(
                            RuntimeException("DeviceMotion error: ${error?.localizedDescription}")
                        )
                    }
                }
                cont.invokeOnCancellation {
                    motionManager.stopDeviceMotionUpdates()
                }
            }
        } ?: throw RuntimeException("DeviceMotion read timed out")
    }

    actual suspend fun getPedometer(fromTimestamp: Long?): PedometerResult {
        val now = NSDate()
        val fromDate = if (fromTimestamp != null) {
            NSDate.dateWithTimeIntervalSince1970(fromTimestamp.toDouble() / 1000.0)
        } else {
            val calendar = NSCalendar.currentCalendar
            calendar.startOfDayForDate(now)
        }

        return suspendCancellableCoroutine { cont ->
            pedometer.queryPedometerDataFromDate(fromDate, toDate = now) { data, error ->
                if (data != null) {
                    cont.resume(
                        PedometerResult(
                            steps = data.numberOfSteps.longValue,
                            distanceMeters = data.distance?.doubleValue,
                            floorsAscended = data.floorsAscended?.intValue,
                            floorsDescended = data.floorsDescended?.intValue,
                            startTime = (fromDate.timeIntervalSince1970 * 1000).toLong(),
                            endTime = (now.timeIntervalSince1970 * 1000).toLong()
                        )
                    )
                } else {
                    cont.resumeWithException(
                        RuntimeException("Pedometer error: ${error?.localizedDescription}")
                    )
                }
            }
        }
    }

    // -- Environment --

    actual suspend fun readBarometer(): BarometerResult {
        if (!CMAltimeter.isRelativeAltitudeAvailable()) {
            throw UnsupportedOperationException("Barometer not available on this device")
        }

        return withTimeoutOrNull(2000L) {
            suspendCancellableCoroutine { cont ->
                altimeter.startRelativeAltitudeUpdatesToQueue(
                    NSOperationQueue.mainQueue
                ) { data, error ->
                    altimeter.stopRelativeAltitudeUpdates()
                    if (data != null) {
                        cont.resume(
                            BarometerResult(
                                pressureHPa = data.pressure.doubleValue * 10.0, // kPa → hPa
                                relativeAltitudeMeters = data.relativeAltitude.doubleValue,
                                timestamp = currentTimeMs()
                            )
                        )
                    } else {
                        cont.resumeWithException(
                            RuntimeException("Barometer error: ${error?.localizedDescription}")
                        )
                    }
                }
                cont.invokeOnCancellation {
                    altimeter.stopRelativeAltitudeUpdates()
                }
            }
        } ?: throw RuntimeException("Barometer read timed out")
    }

    actual suspend fun readAmbientLight(): AmbientLightResult {
        val brightness = UIScreen.mainScreen.brightness
        return AmbientLightResult(
            lux = (brightness * 1000).toFloat(),
            timestamp = currentTimeMs()
        )
    }

    actual suspend fun readProximity(): ProximityResult {
        val device = UIDevice.currentDevice
        val wasEnabled = device.proximityMonitoringEnabled
        device.proximityMonitoringEnabled = true

        val isNear = device.proximityState
        if (!wasEnabled) {
            device.proximityMonitoringEnabled = false
        }

        return ProximityResult(
            isNear = isNear,
            distanceCm = if (isNear) 0f else 10f,
            timestamp = currentTimeMs()
        )
    }

    // -- Connectivity --

    actual suspend fun scanBluetooth(durationSeconds: Int): BluetoothScanResult {
        throw UnsupportedOperationException("Bluetooth scanning not yet implemented - coming in Phase 2")
    }

    actual suspend fun scanWifi(): WifiScanResult {
        throw UnsupportedOperationException("WiFi scanning not yet implemented - coming in Phase 2")
    }

    actual suspend fun readNfc(): NfcResult {
        throw UnsupportedOperationException("NFC reading not yet implemented - coming in Phase 2")
    }

    // -- Device --

    actual suspend fun getBattery(): BatteryResult {
        val device = UIDevice.currentDevice
        device.batteryMonitoringEnabled = true
        val level = device.batteryLevel
        val state = device.batteryState

        return BatteryResult(
            level = if (level < 0f) -1f else level,
            isCharging = state == UIDeviceBatteryState.UIDeviceBatteryStateCharging
                    || state == UIDeviceBatteryState.UIDeviceBatteryStateFull,
            thermalState = NSProcessInfo.processInfo.thermalState.let { ts ->
                when (ts) {
                    NSProcessInfoThermalState.NSProcessInfoThermalStateNominal -> "nominal"
                    NSProcessInfoThermalState.NSProcessInfoThermalStateFair -> "fair"
                    NSProcessInfoThermalState.NSProcessInfoThermalStateSerious -> "serious"
                    NSProcessInfoThermalState.NSProcessInfoThermalStateCritical -> "critical"
                    else -> "unknown"
                }
            },
            timestamp = currentTimeMs()
        )
    }

    actual fun getDeviceInfo(): DeviceInfoResult {
        val device = UIDevice.currentDevice
        val screen = UIScreen.mainScreen
        val scale = screen.scale
        val boundsWidth = screen.bounds.useContents { size.width }
        val boundsHeight = screen.bounds.useContents { size.height }

        return DeviceInfoResult(
            model = device.model,
            manufacturer = "Apple",
            osName = device.systemName,
            osVersion = device.systemVersion,
            screenWidth = (boundsWidth * scale).toInt(),
            screenHeight = (boundsHeight * scale).toInt(),
            availableSensors = availableSensors().map { it.id }
        )
    }

    private fun currentTimeMs(): Long =
        (NSDate().timeIntervalSince1970 * 1000).toLong()
}
