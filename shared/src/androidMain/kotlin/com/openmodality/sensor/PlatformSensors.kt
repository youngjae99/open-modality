package com.openmodality.sensor

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.ImageFormat
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.hardware.camera2.CaptureRequest
import android.location.Geocoder
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.ImageReader
import android.media.MediaRecorder
import android.os.BatteryManager
import android.os.Build
import android.os.Handler
import android.os.HandlerThread
import android.util.Base64
import android.util.DisplayMetrics
import android.view.WindowManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.openmodality.sensor.models.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.math.log10
import kotlin.math.sqrt

actual class PlatformSensors {

    companion object {
        lateinit var appContext: Context
    }

    private val sensorManager: SensorManager by lazy {
        appContext.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }

    private val fusedLocationClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(appContext)
    }

    actual fun availableSensors(): Set<SensorType> = buildSet {
        add(SensorType.BATTERY)

        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null)
            add(SensorType.ACCELEROMETER)
        if (sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE) != null)
            add(SensorType.GYROSCOPE)
        if (sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) != null)
            add(SensorType.MAGNETOMETER)
        if (sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE) != null)
            add(SensorType.BAROMETER)
        if (sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT) != null)
            add(SensorType.AMBIENT_LIGHT)
        if (sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY) != null)
            add(SensorType.PROXIMITY)
        if (sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) != null)
            add(SensorType.PEDOMETER)

        // These require runtime permission checks but are generally available
        add(SensorType.GPS)
        add(SensorType.CAMERA_BACK)
        add(SensorType.CAMERA_FRONT)
        add(SensorType.MICROPHONE)
        add(SensorType.BLUETOOTH)
        add(SensorType.WIFI)
        add(SensorType.NFC)
    }

    actual fun permissionStatus(sensor: SensorType): PermissionStatus =
        PermissionStatus.NOT_REQUESTED

    actual suspend fun requestPermission(sensor: SensorType): PermissionStatus =
        PermissionStatus.NOT_REQUESTED

    // -- Vision --

    @SuppressLint("MissingPermission")
    actual suspend fun takePhoto(camera: CameraType, resolution: Resolution): PhotoResult {
        return withTimeoutOrNull(5000L) {
            suspendCancellableCoroutine { cont ->
                val cameraManager = appContext.getSystemService(Context.CAMERA_SERVICE) as CameraManager
                val handlerThread = HandlerThread("CameraThread").also { it.start() }
                val handler = Handler(handlerThread.looper)

                // Find camera ID matching requested facing
                val targetFacing = when (camera) {
                    CameraType.FRONT -> CameraCharacteristics.LENS_FACING_FRONT
                    CameraType.BACK -> CameraCharacteristics.LENS_FACING_BACK
                }
                val cameraId = cameraManager.cameraIdList.firstOrNull { id ->
                    cameraManager.getCameraCharacteristics(id)
                        .get(CameraCharacteristics.LENS_FACING) == targetFacing
                } ?: cameraManager.cameraIdList.first()

                val characteristics = cameraManager.getCameraCharacteristics(cameraId)
                val map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)!!
                val jpegSizes = map.getOutputSizes(ImageFormat.JPEG)
                val size = when (resolution) {
                    Resolution.LOW -> jpegSizes.minByOrNull { it.width * it.height }!!
                    Resolution.HIGH -> jpegSizes.maxByOrNull { it.width * it.height }!!
                    Resolution.MEDIUM -> jpegSizes.sortedBy { it.width * it.height }
                        .let { it[it.size / 2] }
                }

                val imageReader = ImageReader.newInstance(size.width, size.height, ImageFormat.JPEG, 1)
                var cameraDevice: CameraDevice? = null

                fun cleanup() {
                    try { cameraDevice?.close() } catch (_: Exception) {}
                    try { imageReader.close() } catch (_: Exception) {}
                    handlerThread.quitSafely()
                }

                imageReader.setOnImageAvailableListener({ reader ->
                    val image = reader.acquireLatestImage() ?: return@setOnImageAvailableListener
                    try {
                        val buffer = image.planes[0].buffer
                        val bytes = ByteArray(buffer.remaining())
                        buffer.get(bytes)
                        val base64 = Base64.encodeToString(bytes, Base64.NO_WRAP)
                        cleanup()
                        cont.resume(
                            PhotoResult(
                                base64 = base64,
                                mimeType = "image/jpeg",
                                width = size.width,
                                height = size.height,
                                camera = camera.name.lowercase(),
                                timestamp = System.currentTimeMillis()
                            )
                        )
                    } catch (e: Exception) {
                        cleanup()
                        cont.resumeWithException(e)
                    } finally {
                        image.close()
                    }
                }, handler)

                cameraManager.openCamera(cameraId, object : CameraDevice.StateCallback() {
                    override fun onOpened(device: CameraDevice) {
                        cameraDevice = device
                        val surface = imageReader.surface
                        device.createCaptureSession(
                            listOf(surface),
                            object : android.hardware.camera2.CameraCaptureSession.StateCallback() {
                                override fun onConfigured(session: android.hardware.camera2.CameraCaptureSession) {
                                    val request = device.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE).apply {
                                        addTarget(surface)
                                    }.build()
                                    session.capture(request, null, handler)
                                }
                                override fun onConfigureFailed(session: android.hardware.camera2.CameraCaptureSession) {
                                    cleanup()
                                    cont.resumeWithException(RuntimeException("Camera session configuration failed"))
                                }
                            },
                            handler
                        )
                    }
                    override fun onDisconnected(device: CameraDevice) {
                        device.close()
                        handlerThread.quitSafely()
                        cont.resumeWithException(RuntimeException("Camera disconnected"))
                    }
                    override fun onError(device: CameraDevice, error: Int) {
                        device.close()
                        handlerThread.quitSafely()
                        cont.resumeWithException(RuntimeException("Camera error: $error"))
                    }
                }, handler)

                cont.invokeOnCancellation { cleanup() }
            }
        } ?: throw RuntimeException("Camera capture timed out")
    }

    actual suspend fun scanLidar(): String? = null // Not available on Android

    // -- Audio --

    @SuppressLint("MissingPermission")
    actual suspend fun recordAudio(durationSeconds: Int, transcribe: Boolean): AudioResult {
        val timeoutMs = (durationSeconds * 1000L) + 2000L
        return withTimeoutOrNull(timeoutMs) {
            val sampleRate = 44100
            val bufferSize = AudioRecord.getMinBufferSize(
                sampleRate,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            )
            val recorder = AudioRecord(
                MediaRecorder.AudioSource.MIC,
                sampleRate,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                bufferSize
            )
            recorder.startRecording()
            val startTime = System.currentTimeMillis()
            val endTime = startTime + (durationSeconds * 1000L)
            var rmsSum = 0.0
            var chunkCount = 0

            while (System.currentTimeMillis() < endTime) {
                val buffer = ShortArray(bufferSize)
                val read = recorder.read(buffer, 0, bufferSize)
                if (read > 0) {
                    val sumSq = buffer.take(read).sumOf { it.toLong() * it.toLong() }
                    val rms = sqrt(sumSq.toDouble() / read)
                    rmsSum += rms
                    chunkCount++
                }
            }
            recorder.stop()
            recorder.release()

            val avgRms = if (chunkCount > 0) rmsSum / chunkCount else 0.0
            val avgDb = if (avgRms > 0) (20 * log10(avgRms / Short.MAX_VALUE)).toFloat() else -100f

            AudioResult(
                transcription = null,
                durationSeconds = durationSeconds.toDouble(),
                averageDecibels = avgDb,
                timestamp = startTime
            )
        } ?: throw RuntimeException("Audio recording timed out")
    }

    @SuppressLint("MissingPermission")
    actual suspend fun getAmbientSoundLevel(): AmbientSoundResult {
        return try {
            val bufferSize = AudioRecord.getMinBufferSize(
                44100,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            )
            val recorder = AudioRecord(
                MediaRecorder.AudioSource.MIC,
                44100,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                bufferSize
            )
            recorder.startRecording()
            val buffer = ShortArray(bufferSize)
            recorder.read(buffer, 0, bufferSize)
            recorder.stop()
            recorder.release()

            val amplitude = buffer.maxOrNull()?.toDouble() ?: 0.0
            val db = if (amplitude > 0) 20 * log10(amplitude / Short.MAX_VALUE) else -100.0

            AmbientSoundResult(
                decibels = db.toFloat(),
                timestamp = System.currentTimeMillis()
            )
        } catch (e: Exception) {
            AmbientSoundResult(decibels = -1f, timestamp = System.currentTimeMillis())
        }
    }

    // -- Location --

    @SuppressLint("MissingPermission")
    actual suspend fun getLocation(accuracy: LocationAccuracy): LocationResult {
        val priority = when (accuracy) {
            LocationAccuracy.BEST -> Priority.PRIORITY_HIGH_ACCURACY
            LocationAccuracy.BALANCED -> Priority.PRIORITY_BALANCED_POWER_ACCURACY
            LocationAccuracy.LOW_POWER -> Priority.PRIORITY_LOW_POWER
        }
        val cts = CancellationTokenSource()
        val location = suspendCancellableCoroutine { cont ->
            fusedLocationClient.getCurrentLocation(priority, cts.token)
                .addOnSuccessListener { loc ->
                    if (loc != null) {
                        cont.resume(loc)
                    } else {
                        cont.resume(null)
                    }
                }
                .addOnFailureListener {
                    cont.resume(null)
                }
            cont.invokeOnCancellation { cts.cancel() }
        }
        return if (location != null) {
            LocationResult(
                latitude = location.latitude,
                longitude = location.longitude,
                altitude = location.altitude,
                accuracy = location.accuracy,
                speed = location.speed,
                heading = location.bearing,
                timestamp = location.time
            )
        } else {
            throw RuntimeException("Could not get location. Check permissions and GPS enabled.")
        }
    }

    @SuppressLint("MissingPermission")
    @Suppress("DEPRECATION")
    actual suspend fun getAddress(): AddressResult {
        val location = getLocation(LocationAccuracy.BALANCED)
        val geocoder = Geocoder(appContext)
        val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
        val addr = addresses?.firstOrNull()
        return AddressResult(
            formattedAddress = addr?.getAddressLine(0) ?: "Unknown",
            street = addr?.thoroughfare,
            city = addr?.locality,
            state = addr?.adminArea,
            country = addr?.countryName,
            postalCode = addr?.postalCode
        )
    }

    // -- Motion sensors (SensorManager one-shot reads) --

    actual suspend fun readAccelerometer(): MotionResult =
        readSensorOnce(Sensor.TYPE_ACCELEROMETER)

    actual suspend fun readGyroscope(): MotionResult =
        readSensorOnce(Sensor.TYPE_GYROSCOPE)

    actual suspend fun readMagnetometer(): MotionResult =
        readSensorOnce(Sensor.TYPE_MAGNETIC_FIELD)

    actual suspend fun getDeviceMotion(): DeviceMotionResult {
        val accel = readSensorOnce(Sensor.TYPE_ACCELEROMETER)
        val gyro = readSensorOnce(Sensor.TYPE_GYROSCOPE)
        val mag = readSensorOnce(Sensor.TYPE_MAGNETIC_FIELD)

        val gravity = readSensorOnce(Sensor.TYPE_GRAVITY)
        val rotationVector = try {
            readSensorValues(Sensor.TYPE_ROTATION_VECTOR)
        } catch (_: Exception) { floatArrayOf(0f, 0f, 0f) }

        return DeviceMotionResult(
            attitude = Attitude(
                pitch = rotationVector.getOrElse(0) { 0f }.toDouble(),
                roll = rotationVector.getOrElse(1) { 0f }.toDouble(),
                yaw = rotationVector.getOrElse(2) { 0f }.toDouble()
            ),
            rotationRate = gyro,
            gravity = gravity,
            userAcceleration = MotionResult(
                x = accel.x - gravity.x,
                y = accel.y - gravity.y,
                z = accel.z - gravity.z,
                timestamp = accel.timestamp
            ),
            timestamp = System.currentTimeMillis()
        )
    }

    actual suspend fun getPedometer(fromTimestamp: Long?): PedometerResult {
        // Step counter gives total steps since reboot
        val values = readSensorValues(Sensor.TYPE_STEP_COUNTER)
        return PedometerResult(
            steps = values.getOrElse(0) { 0f }.toLong(),
            distanceMeters = null,
            floorsAscended = null,
            floorsDescended = null,
            startTime = fromTimestamp ?: 0L,
            endTime = System.currentTimeMillis()
        )
    }

    // -- Environment --

    actual suspend fun readBarometer(): BarometerResult {
        val values = readSensorValues(Sensor.TYPE_PRESSURE)
        return BarometerResult(
            pressureHPa = values.getOrElse(0) { 0f }.toDouble(),
            relativeAltitudeMeters = null,
            timestamp = System.currentTimeMillis()
        )
    }

    actual suspend fun readAmbientLight(): AmbientLightResult {
        val values = readSensorValues(Sensor.TYPE_LIGHT)
        return AmbientLightResult(
            lux = values.getOrElse(0) { 0f },
            timestamp = System.currentTimeMillis()
        )
    }

    actual suspend fun readProximity(): ProximityResult {
        val values = readSensorValues(Sensor.TYPE_PROXIMITY)
        val maxRange = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)?.maximumRange ?: 5f
        val distance = values.getOrElse(0) { maxRange }
        return ProximityResult(
            isNear = distance < maxRange,
            distanceCm = distance,
            timestamp = System.currentTimeMillis()
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
        val bm = appContext.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
        val level = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY).toFloat() / 100f
        val isCharging = bm.isCharging
        return BatteryResult(
            level = level,
            isCharging = isCharging,
            thermalState = null,
            timestamp = System.currentTimeMillis()
        )
    }

    actual fun getDeviceInfo(): DeviceInfoResult {
        val wm = appContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val dm = DisplayMetrics()
        @Suppress("DEPRECATION")
        wm.defaultDisplay.getRealMetrics(dm)
        return DeviceInfoResult(
            model = Build.MODEL,
            manufacturer = Build.MANUFACTURER,
            osName = "Android",
            osVersion = Build.VERSION.RELEASE,
            screenWidth = dm.widthPixels,
            screenHeight = dm.heightPixels,
            availableSensors = availableSensors().map { it.id }
        )
    }

    // -- Private helpers --

    private suspend fun readSensorOnce(sensorType: Int): MotionResult {
        val values = readSensorValues(sensorType)
        return MotionResult(
            x = values.getOrElse(0) { 0f }.toDouble(),
            y = values.getOrElse(1) { 0f }.toDouble(),
            z = values.getOrElse(2) { 0f }.toDouble(),
            timestamp = System.currentTimeMillis()
        )
    }

    private suspend fun readSensorValues(sensorType: Int): FloatArray {
        val sensor = sensorManager.getDefaultSensor(sensorType)
            ?: throw UnsupportedOperationException("Sensor type $sensorType not available")

        return withTimeoutOrNull(2000L) {
            suspendCancellableCoroutine { cont ->
                val listener = object : SensorEventListener {
                    override fun onSensorChanged(event: SensorEvent) {
                        sensorManager.unregisterListener(this)
                        cont.resume(event.values.copyOf())
                    }
                    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
                }
                sensorManager.registerListener(
                    listener, sensor, SensorManager.SENSOR_DELAY_NORMAL
                )
                cont.invokeOnCancellation {
                    sensorManager.unregisterListener(listener)
                }
            }
        } ?: throw RuntimeException("Sensor read timed out for type $sensorType")
    }
}
