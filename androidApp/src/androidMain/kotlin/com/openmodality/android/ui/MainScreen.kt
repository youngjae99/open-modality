package com.openmodality.android.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.openmodality.mcp.McpServer
import com.openmodality.sensor.PlatformSensors
import org.koin.core.context.GlobalContext

private enum class Tab(
    val label: String,
    val icon: ImageVector
) {
    DASHBOARD("Dashboard", Icons.Default.Home),
    SENSORS("Sensors", Icons.Default.Settings),
    LOG("Log", Icons.AutoMirrored.Filled.List)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    mcpServer: McpServer = GlobalContext.get().get(),
    platformSensors: PlatformSensors = GlobalContext.get().get(),
    onServerStart: () -> Unit = { mcpServer.start() },
    onServerStop: () -> Unit = { mcpServer.stop() }
) {
    val isRunning by mcpServer.isRunning.collectAsState()
    val requestLog by mcpServer.sessions.requestLog.collectAsState()
    var selectedTab by remember { mutableStateOf(Tab.DASHBOARD) }

    MaterialTheme {
        Scaffold(
            bottomBar = {
                NavigationBar {
                    Tab.entries.forEach { tab ->
                        NavigationBarItem(
                            selected = selectedTab == tab,
                            onClick = { selectedTab = tab },
                            icon = { Icon(tab.icon, contentDescription = tab.label) },
                            label = { Text(tab.label) }
                        )
                    }
                }
            }
        ) { padding ->
            when (selectedTab) {
                Tab.DASHBOARD -> DashboardScreen(
                    isRunning = isRunning,
                    toolCount = mcpServer.toolCount,
                    onToggle = { if (isRunning) onServerStop() else onServerStart() },
                    modifier = Modifier.padding(padding)
                )
                Tab.SENSORS -> SensorsScreen(
                    platformSensors = platformSensors,
                    modifier = Modifier.padding(padding)
                )
                Tab.LOG -> LogScreen(
                    requestLog = requestLog,
                    modifier = Modifier.padding(padding)
                )
            }
        }
    }
}
