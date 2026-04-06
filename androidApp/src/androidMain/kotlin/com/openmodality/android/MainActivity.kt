package com.openmodality.android

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.openmodality.android.background.SensorServerService
import com.openmodality.android.ui.MainScreen
import com.openmodality.server.OpenModalityServer
import org.koin.core.context.GlobalContext

class MainActivity : ComponentActivity() {

    private val server: OpenModalityServer by lazy {
        GlobalContext.get().get()
    }

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        // Permission results handled - server can now use granted sensors
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestSensorPermissions()
        setContent {
            MainScreen(
                onServerStart = { startServerService() },
                onServerStop = { stopServerService() }
            )
        }
    }

    private fun startServerService() {
        val intent = Intent(this, SensorServerService::class.java)
        ContextCompat.startForegroundService(this, intent)
    }

    private fun stopServerService() {
        server.stop()
        stopService(Intent(this, SensorServerService::class.java))
    }

    private fun requestSensorPermissions() {
        val permissions = mutableListOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.ACTIVITY_RECOGNITION,
            Manifest.permission.BODY_SENSORS
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.POST_NOTIFICATIONS)
            permissions.add(Manifest.permission.NEARBY_WIFI_DEVICES)
        }

        val needed = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (needed.isNotEmpty()) {
            permissionLauncher.launch(needed.toTypedArray())
        }
    }
}
