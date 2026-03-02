package com.openmodality.android.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.openmodality.mcp.McpServer
import com.openmodality.mcp.RequestLogEntry
import org.koin.core.context.GlobalContext
import java.net.NetworkInterface

@Composable
fun MainScreen(
    mcpServer: McpServer = GlobalContext.get().get(),
    onServerStart: () -> Unit = { mcpServer.start() },
    onServerStop: () -> Unit = { mcpServer.stop() }
) {
    val isRunning by mcpServer.isRunning.collectAsState()
    val requestLog by mcpServer.sessions.requestLog.collectAsState()

    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Open Modality",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Smartphone Sensor MCP Server",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Server status card
                ServerStatusCard(isRunning = isRunning, onToggle = {
                    if (isRunning) onServerStop() else onServerStart()
                })

                Spacer(modifier = Modifier.height(16.dp))

                // Connection info
                if (isRunning) {
                    ConnectionInfoCard()
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Request log
                Text(
                    text = "Request Log",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.align(Alignment.Start)
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    items(requestLog.reversed()) { entry ->
                        RequestLogItem(entry)
                    }
                }
            }
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
                MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = if (isRunning) "Server Running" else "Server Stopped",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp
                )
                Text(
                    text = if (isRunning) "Accepting MCP connections" else "Tap to start",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Button(onClick = onToggle) {
                Text(if (isRunning) "Stop" else "Start")
            }
        }
    }
}

@Composable
private fun ConnectionInfoCard() {
    val ipAddress = remember { getLocalIpAddress() }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("Connect from Claude Code:", fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(8.dp))
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = MaterialTheme.shapes.small
            ) {
                Text(
                    text = """
                        {
                          "mcpServers": {
                            "open-modality": {
                              "url": "http://$ipAddress:8080/mcp"
                            }
                          }
                        }
                    """.trimIndent(),
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }
    }
}

@Composable
private fun RequestLogItem(entry: RequestLogEntry) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        color = if (entry.success)
            MaterialTheme.colorScheme.surface
        else
            MaterialTheme.colorScheme.errorContainer
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = entry.toolName ?: entry.method,
                fontSize = 13.sp,
                fontFamily = FontFamily.Monospace
            )
            Text(
                text = entry.durationMs?.let { "${it}ms" } ?: "",
                fontSize = 12.sp,
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
