package com.sp45.androidmanager.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sp45.androidmanager.domain.model.SystemStats

@Composable
fun LivePerformanceSummaryCard(
    stats: SystemStats,
    recentSamples: List<SystemStats>
) {
    val overallHealth = calculateSystemHealth(stats)
    val healthColor = when {
        overallHealth > 80 -> Color(0xFF81C784)
        overallHealth > 60 -> Color(0xFFFFB74D)
        else -> Color(0xFFE57373)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = healthColor.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "⚡ System Health",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "${overallHealth.toInt()}%",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = healthColor
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { overallHealth / 100f },
                modifier = Modifier.fillMaxWidth(),
                color = healthColor,
                trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = getHealthDescription(overallHealth),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun calculateSystemHealth(stats: SystemStats): Float {
    val cpuScore = (100 - stats.cpu.systemLoad).coerceAtLeast(0f)
    val memoryUsagePercent = (stats.mem.usedMB.toFloat() / stats.mem.totalMB.toFloat()) * 100f
    val memoryScore = (100 - memoryUsagePercent).coerceAtLeast(0f)
    val batteryScore = stats.battery.levelPct.toFloat()

    // Weighted average: CPU 40%, Memory 40%, Battery 20%
    return (cpuScore * 0.4f + memoryScore * 0.4f + batteryScore * 0.2f).coerceIn(0f, 100f)
}

private fun getHealthDescription(health: Float): String {
    return when {
        health > 80 -> "System running optimally"
        health > 60 -> "System performance is good"
        health > 40 -> "System performance is moderate"
        else -> "System may be under stress"
    }
}