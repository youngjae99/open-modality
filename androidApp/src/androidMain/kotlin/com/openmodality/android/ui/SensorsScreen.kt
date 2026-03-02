package com.openmodality.android.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.openmodality.sensor.PlatformSensors
import com.openmodality.sensor.SensorCategory
import com.openmodality.sensor.SensorType

private data class SensorCategoryGroup(
    val category: SensorCategory,
    val displayName: String,
    val sensors: List<SensorInfo>
)

private data class SensorInfo(
    val id: String,
    val name: String,
    val permission: String
)

private val CATEGORY_ORDER = listOf(
    SensorCategory.VISION to "Vision",
    SensorCategory.AUDIO to "Audio",
    SensorCategory.LOCATION to "Location",
    SensorCategory.MOTION to "Motion",
    SensorCategory.ENVIRONMENT to "Environment",
    SensorCategory.CONNECTIVITY to "Connectivity",
    SensorCategory.DEVICE to "Device",
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SensorsScreen(
    platformSensors: PlatformSensors,
    modifier: Modifier = Modifier
) {
    val groups = remember {
        val available = platformSensors.availableSensors()
        val byCategory = available.groupBy { it.category }

        CATEGORY_ORDER.mapNotNull { (category, displayName) ->
            val sensors = byCategory[category] ?: return@mapNotNull null
            SensorCategoryGroup(
                category = category,
                displayName = displayName,
                sensors = sensors.map { sensor ->
                    SensorInfo(
                        id = sensor.id,
                        name = sensor.displayName,
                        permission = platformSensors.permissionStatus(sensor).name
                    )
                }
            )
        }
    }

    Scaffold(
        topBar = {
            LargeTopAppBar(title = { Text("Sensors") })
        },
        modifier = modifier
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            groups.forEach { group ->
                item(key = "header_${group.category.name}") {
                    Text(
                        text = group.displayName,
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                    )
                }

                items(
                    items = group.sensors,
                    key = { it.id }
                ) { sensor ->
                    SensorItem(sensor)
                }
            }
        }
    }
}

@Composable
private fun SensorItem(sensor: SensorInfo) {
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
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = sensor.name,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = sensor.id,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            PermissionChip(status = sensor.permission)
        }
    }
}

@Composable
private fun PermissionChip(status: String) {
    val (text, color) = when (status) {
        "GRANTED" -> "granted" to MaterialTheme.colorScheme.primary
        "DENIED" -> "denied" to MaterialTheme.colorScheme.error
        "RESTRICTED" -> "restricted" to MaterialTheme.colorScheme.tertiary
        else -> "not requested" to MaterialTheme.colorScheme.outline
    }

    Surface(
        color = color.copy(alpha = 0.12f),
        shape = CircleShape
    ) {
        Text(
            text = text,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = color,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}
