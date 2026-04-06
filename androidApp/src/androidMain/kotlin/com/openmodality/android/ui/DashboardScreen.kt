package com.openmodality.android.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.net.NetworkInterface

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    isRunning: Boolean,
    toolCount: Int,
    currentPin: String,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    Column {
                        Text("Open Modality")
                        Text(
                            "Smartphone Sensor Server",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            )
        },
        modifier = modifier
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ServerStatusCard(isRunning = isRunning, onToggle = onToggle)

            if (isRunning) {
                ConnectionInfoCard(currentPin = currentPin)
                ToolCountCard(toolCount = toolCount)
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun ServerStatusCard(isRunning: Boolean, onToggle: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isRunning)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surfaceContainerHigh
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(
                            if (isRunning)
                                androidx.compose.ui.graphics.Color(0xFF4CAF50)
                            else
                                androidx.compose.ui.graphics.Color(0xFFBDBDBD)
                        )
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = if (isRunning) "Server Running" else "Server Stopped",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp
                    )
                    Text(
                        text = if (isRunning)
                            "Accepting connections on port 8080"
                        else
                            "Tap Start to begin accepting connections",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Button(
                onClick = onToggle,
                modifier = Modifier.fillMaxWidth(),
                colors = if (isRunning)
                    ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                else
                    ButtonDefaults.buttonColors()
            ) {
                Text(
                    text = if (isRunning) "Stop Server" else "Start Server",
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun ConnectionInfoCard(currentPin: String) {
    val ipAddress = remember { getLocalIpAddress() }
    val clipboardManager = LocalClipboardManager.current
    var copiedWs by remember { mutableStateOf(false) }
    var copiedPython by remember { mutableStateOf(false) }

    val wsUrl = "ws://$ipAddress:8080/ws?pin=$currentPin"
    val pythonSnippet = """
import websocket, json

ws = websocket.create_connection("$wsUrl")

# List available tools
ws.send(json.dumps({"id": "1", "method": "list_tools"}))
print(json.loads(ws.recv()))

# Call a tool
ws.send(json.dumps({"id": "2", "method": "get_location"}))
print(json.loads(ws.recv()))

ws.close()""".trimStart()

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Connection", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)

            // PIN section
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = MaterialTheme.shapes.medium
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        "PIN",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = currentPin,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 8.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        "Use this PIN to connect",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                }
            }

            HorizontalDivider()

            // WebSocket URL section
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    "WebSocket URL:",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Surface(
                    color = MaterialTheme.colorScheme.surfaceContainerHigh,
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = wsUrl,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(12.dp)
                    )
                }
                OutlinedButton(
                    onClick = {
                        clipboardManager.setText(AnnotatedString(wsUrl))
                        copiedWs = true
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (copiedWs) "Copied!" else "Copy URL")
                }
            }

            HorizontalDivider()

            // HTTP endpoint section
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    "HTTP (one-shot):",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Surface(
                    color = MaterialTheme.colorScheme.surfaceContainerHigh,
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = "POST http://$ipAddress:8080/call\nHeader: X-Pin: $currentPin",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            HorizontalDivider()

            // Python example
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    "Python example:",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Surface(
                    color = MaterialTheme.colorScheme.surfaceContainerHigh,
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = pythonSnippet,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(12.dp)
                    )
                }
                OutlinedButton(
                    onClick = {
                        clipboardManager.setText(AnnotatedString(pythonSnippet))
                        copiedPython = true
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (copiedPython) "Copied!" else "Copy Python Example")
                }
            }
        }
    }
}

@Composable
private fun ToolCountCard(toolCount: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "$toolCount tools available",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun getLocalIpAddress(): String {
    try {
        val interfaces = NetworkInterface.getNetworkInterfaces()
        while (interfaces.hasMoreElements()) {
            val iface = interfaces.nextElement()
            val addresses = iface.inetAddresses
            while (addresses.hasMoreElements()) {
                val addr = addresses.nextElement()
                if (!addr.isLoopbackAddress && addr.hostAddress?.contains('.') == true) {
                    return addr.hostAddress ?: "unknown"
                }
            }
        }
    } catch (_: Exception) {}
    return "unknown"
}
