package com.sp45.androidmanager.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sp45.androidmanager.domain.model.SystemStats
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun StoredStatsCard(stats: SystemStats, timestamp: Long? = null) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Timestamp header
            val displayTimestamp = timestamp ?: stats.timestamp
            Text(
                text = "🕐 ${formatTimestamp(displayTimestamp)}",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )

            // CPU Info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "💻 CPU Load:",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "${String.format("%.1f", stats.cpu.systemLoad)}% (${stats.cpu.loadLevel})",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = getCpuLoadColor(stats.cpu.loadLevel)
                )
            }

            // Memory Info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "🧠 Memory:",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "${formatMB(stats.mem.usedMB)}/${formatMB(stats.mem.totalMB)} MB",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
            }

            // Battery Info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "🔋 Battery:",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "${stats.battery.levelPct}% (${stats.battery.status ?: "Unknown"})",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = getBatteryColor(stats.battery.levelPct)
                )
            }

            // Storage Info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "💾 Storage:",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "${String.format("%.1f", stats.storage.internalFreeGB)}GB free",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
            }

            // Network Info
            stats.net.wifiRssiDbm?.let { rssi ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "📶 WiFi:",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "${rssi}dBm",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = getWifiSignalColor(rssi)
                    )
                }
            }
        }
    }
}

// Helper functions
private fun formatTimestamp(timestamp: Long): String {
    return SimpleDateFormat("MMM dd, yyyy HH:mm:ss", Locale.getDefault()).format(Date(timestamp))
}
