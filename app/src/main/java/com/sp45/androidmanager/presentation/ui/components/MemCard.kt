package com.sp45.androidmanager.presentation.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sp45.androidmanager.domain.model.BatteryStatsUi
import com.sp45.androidmanager.domain.model.MemoryStats
import com.sp45.androidmanager.domain.model.NetworkStatsUi
import com.sp45.androidmanager.domain.model.StorageStatsUi
import java.util.Locale

@Composable
fun MemCard(mem: MemoryStats) {
    val usagePercentage = (mem.usedMB.toFloat() / mem.totalMB.toFloat()) * 100f

    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(
            width = 1.dp,
            color = Color.White.copy(alpha = 0.6f)
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "💾 Memory Usage",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${String.format(Locale.US, "%.1f", usagePercentage)}%",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = when {
                        usagePercentage > 85 -> Color(0xFFF44336)
                        usagePercentage > 70 -> Color(0xFFFF9800)
                        else -> Color(0xFF4CAF50)
                    }
                )
            }

            // Memory Usage Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(fraction = usagePercentage / 100f)
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(
                            when {
                                usagePercentage > 85 -> Color(0xFFF44336)
                                usagePercentage > 70 -> Color(0xFFFF9800)
                                else -> Color(0xFF4CAF50)
                            }
                        )
                )
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))

            // Memory Details Grid
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    InfoItem(label = "Total", value = "${mem.totalMB} MB")
                    InfoItem(label = "Used", value = "${mem.usedMB} MB")
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    InfoItem(label = "Free", value = "${mem.freeMB} MB")
                    InfoItem(label = "Cached", value = "${mem.cachedMB} MB")
                }
                if (mem.swapTotalMB > 0) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        InfoItem(label = "Swap", value = "${mem.swapUsedMB}/${mem.swapTotalMB} MB")
                    }
                }
            }
        }
    }
}

/**
 * Enhanced Storage Card
 */
@Composable
fun StorageCard(st: StorageStatsUi) {
    val usagePercentage = ((st.internalTotalGB - st.internalFreeGB) / st.internalTotalGB) * 100f

    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(
            width = 1.dp,
            color = Color.White.copy(alpha = 0.6f)
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "💽 Internal Storage",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${String.format(Locale.US, "%.1f", usagePercentage)}%",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = when {
                        usagePercentage > 90 -> Color(0xFFF44336)
                        usagePercentage > 80 -> Color(0xFFFF9800)
                        else -> Color(0xFF4CAF50)
                    }
                )
            }

            // Storage Usage Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(fraction = usagePercentage / 100f)
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(
                            when {
                                usagePercentage > 90 -> Color(0xFFF44336)
                                usagePercentage > 80 -> Color(0xFFFF9800)
                                else -> Color(0xFF4CAF50)
                            }
                        )
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                InfoItem(
                    label = "Available",
                    value = "${String.format(Locale.US, "%.1f", st.internalFreeGB)} GB"
                )
                InfoItem(
                    label = "Total",
                    value = "${String.format(Locale.US, "%.1f", st.internalTotalGB)} GB"
                )
            }
        }
    }
}

/**
 * Enhanced Battery Card
 */
@Composable
fun BatteryCard(b: BatteryStatsUi) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(
            width = 1.dp,
            color = Color.White.copy(alpha = 0.6f)
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🔋 Battery Status",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${b.levelPct}%",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = when {
                        b.levelPct > 50 -> Color(0xFF4CAF50)
                        b.levelPct > 20 -> Color(0xFFFF9800)
                        else -> Color(0xFFF44336)
                    }
                )
            }

            // Battery Level Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(fraction = b.levelPct / 100f)
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(
                            when {
                                b.levelPct > 50 -> Color(0xFF4CAF50)
                                b.levelPct > 20 -> Color(0xFFFF9800)
                                else -> Color(0xFFF44336)
                            }
                        )
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                b.status?.let { status ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Status:", fontWeight = FontWeight.Medium)
                        Text(status)
                    }
                }

                b.health?.let { health ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Health:", fontWeight = FontWeight.Medium)
                        Text(health)
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    b.temperatureC?.let { temp ->
                        InfoItem(
                            label = "Temperature",
                            value = "${String.format(Locale.US, "%.1f", temp)}°C"
                        )
                    }
                    b.voltageMV?.let { voltage ->
                        InfoItem(
                            label = "Voltage",
                            value = "$voltage mV"
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NetworkCard(
    n: NetworkStatsUi
) {
    val totalDataMB = n.totalRxMB + n.totalTxMB
    val (connLabel, connQualityColor) = when (n.wifiRssiDbm) {
        null -> Pair("Mobile", MaterialTheme.colorScheme.onSurfaceVariant)
        else -> {
            val q = wifiRssiToQuality(n.wifiRssiDbm)
            Pair("Wi-Fi ($q)", wifiQualityColor(q))
        }
    }
    val (dataUsageLabel, dataUsageColor) = dataUsageCategory(totalDataMB)

    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(
            width = 1.dp,
            color = Color.White.copy(alpha = 0.6f)
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Header
            Text(
                text = "📶 Network",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            // Connection Status
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Connection:", style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.width(4.dp))
                Text(
                    text = connLabel,
                    color = connQualityColor,
                    fontWeight = FontWeight.Medium
                )
            }

            // Signal Strength (when available)
            n.wifiRssiDbm?.let { rssi ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Signal:", style = MaterialTheme.typography.bodyMedium)
                    Spacer(Modifier.width(4.dp))
                    Text("$rssi dBm", style = MaterialTheme.typography.bodySmall)
                    Spacer(Modifier.width(4.dp))
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(wifiQualityColor(wifiRssiToQuality(rssi)))
                    )
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

            // Data Summary
            Column {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Total Data:", fontWeight = FontWeight.Medium)
                    Text(
                        text = "${formatDataMB(totalDataMB)} • $dataUsageLabel",
                        color = dataUsageColor,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(Modifier.height(2.dp))

                // Data Breakdown
                GridRow("Download", n.totalRxMB, n.mobileRxMB)
                GridRow("Upload", n.totalTxMB, n.mobileTxMB)
            }
        }
    }
}

@Composable
private fun GridRow(label: String, total: Long, mobile: Long) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text("$label:", style = MaterialTheme.typography.bodySmall)
        Text(
            "${formatDataMB(total)} (${formatDataMB(mobile)} mobile)",
            style = MaterialTheme.typography.bodySmall
        )
    }
}

// Helper composable for consistent grid rows
//@Composable
//private fun GridRow(label: String, total: Long, mobile: Long) {
//    Row(
//        modifier = Modifier.fillMaxWidth(),
//        horizontalArrangement = Arrangement.SpaceBetween
//    ) {
//        Text("$label:", style = MaterialTheme.typography.bodySmall)
//        Text(
//            "${formatDataMB(total)} (${formatDataMB(mobile)} mobile)",
//            style = MaterialTheme.typography.bodySmall
//        )
//    }
//}

// Keep your existing helper functions (wifiRssiToQuality, wifiQualityColor,
// dataUsageCategory, formatDataMB) unchanged

/** Helper: map RSSI to quality label */
private fun wifiRssiToQuality(rssi: Int): String {
    return when {
        rssi == -127 -> "Not Connected"
        rssi >= -50 -> "Excellent"
        rssi >= -60 -> "Good"
        rssi >= -70 -> "Moderate"
        rssi >= -80 -> "Poor"
        else -> "Very poor"
    }
}

/** Helper: color for quality label */
@Composable
private fun wifiQualityColor(label: String) = when (label) {
    "Excellent" -> Color(0xFF4CAF50)
    "Good" -> Color(0xFF8BC34A)
    "Moderate" -> Color(0xFFFFA000)
    "Poor" -> Color(0xFFF44336)
    "Very poor" -> Color(0xFFB00020)
    else -> MaterialTheme.colorScheme.onSurfaceVariant
}

/**
 * Data usage categorization (total MB -> label + color)
 * thresholds (tweakable):
 *  < 100 MB       -> Low
 *  100 - 500 MB   -> Moderate
 *  500 - 2048 MB  -> High
 *  > 2048 MB      -> Very high
 */
private fun dataUsageCategory(totalMb: Long): Pair<String, Color> {
    return when {
        totalMb < 100 -> Pair("Low", Color(0xFF4CAF50))
        totalMb < 500 -> Pair("Moderate", Color(0xFFFFA000))
        totalMb < 2048 -> Pair("High", Color(0xFFFF7043))
        else -> Pair("Very high", Color(0xFFF44336))
    }
}

/** format MB into MB/GB string */
private fun formatDataMB(totalMb: Long): String {
    return if (totalMb >= 1024) {
        val gb = totalMb.toFloat() / 1024f
        String.format(Locale.US, "%.2f GB", gb)
    } else {
        "$totalMb MB"
    }
}


/**
 * Helper composable for consistent info display
 */
@Composable
fun InfoItem(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}

/** Helper functions */
fun formatPct(p: Float) = String.format(Locale.US, "%.1f%%", p)

fun formatLargeNumber(num: Long): String {
    return when {
        num >= 1_000_000 -> String.format(Locale.US, "%.1fM", num / 1_000_000f)
        num >= 1_000 -> String.format(Locale.US, "%.1fK", num / 1_000f)
        else -> num.toString()
    }
}