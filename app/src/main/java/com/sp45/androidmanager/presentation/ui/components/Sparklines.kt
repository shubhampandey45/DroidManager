package com.sp45.androidmanager.presentation.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.sp45.androidmanager.domain.model.SystemStats
import kotlin.math.max

@Composable
fun LiveStatsCard(
    title: String,
    currentValue: String,
    unit: String,
    data: List<Float>,
    trend: String = "",
    color: Color = MaterialTheme.colorScheme.primary,
    maxValue: Float = 100f,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )

                if (trend.isNotEmpty()) {
                    Text(
                        text = trend,
                        style = MaterialTheme.typography.bodySmall,
                        color = when {
                            trend.contains("↑") -> Color(0xFFE57373)
                            trend.contains("↓") -> Color(0xFF81C784)
                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Current Value
            Row(
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = currentValue,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = unit,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Sparkline Chart
            if (data.isNotEmpty()) {
                SparklineChart(
                    data = data,
                    color = color,
                    maxValue = maxValue,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Collecting data...",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Data points indicator
            if (data.isNotEmpty()) {
                Text(
                    text = "${data.size} samples (last ${data.size} seconds)",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.End,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun SparklineChart(
    data: List<Float>,
    color: Color,
    maxValue: Float,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        if (data.size < 2) return@Canvas

        val strokeWidth = 3.dp.toPx()
        val width = size.width
        val height = size.height

        // Calculate data bounds
        val minData = data.minOrNull() ?: 0f
        val maxData = max(data.maxOrNull() ?: maxValue, maxValue * 0.1f) // Ensure some height
        val range = maxData - minData

        if (range == 0f) return@Canvas

        // Create path for line chart
        val path = Path()
        val stepX = width / (data.size - 1)

        data.forEachIndexed { index, value ->
            val x = index * stepX
            val normalizedValue = (value - minData) / range
            val y =
                height - (normalizedValue * height * 0.8f) - (height * 0.1f) // Leave 10% padding top/bottom

            if (index == 0) {
                path.moveTo(x, y)
            } else {
                path.lineTo(x, y)
            }
        }

        // Draw the sparkline
        drawPath(
            path = path,
            color = color,
            style = Stroke(
                width = strokeWidth,
                cap = StrokeCap.Round
            )
        )

        // Draw data points
        data.forEachIndexed { index, value ->
            val x = index * stepX
            val normalizedValue = (value - minData) / range
            val y = height - (normalizedValue * height * 0.8f) - (height * 0.1f)

            // Highlight the last point
            if (index == data.size - 1) {
                drawCircle(
                    color = color,
                    radius = strokeWidth * 1.5f,
                    center = Offset(x, y)
                )
                drawCircle(
                    color = Color.White,
                    radius = strokeWidth * 0.8f,
                    center = Offset(x, y)
                )
            }
        }
    }
}

// Enhanced CPU Card with Live Data
@Composable
fun LiveCpuCard(
    currentStats: com.sp45.androidmanager.domain.model.CpuStats,
    recentSamples: List<SystemStats>,
    modifier: Modifier = Modifier
) {
    val cpuData = recentSamples.map { it.cpu.systemLoad }
    val trend = calculateTrend(cpuData)

    LiveStatsCard(
        title = "🔥 CPU Usage",
        currentValue = String.format("%.1f", currentStats.systemLoad),
        unit = "%",
        data = cpuData,
        trend = trend,
        color = when {
            currentStats.systemLoad > 80 -> Color(0xFFE57373)
            currentStats.systemLoad > 60 -> Color(0xFFFFB74D)
            else -> Color(0xFF81C784)
        },
        maxValue = 100f,
        modifier = modifier.border(
            width = 1.dp,
            color = Color.White.copy(alpha = 0.6f),
            shape = RoundedCornerShape(8.dp)
        )
    )
}

// Enhanced Memory Card with Live Data
@Composable
fun LiveMemoryCard(
    currentStats: com.sp45.androidmanager.domain.model.MemoryStats,
    recentSamples: List<SystemStats>,
    modifier: Modifier = Modifier
) {
    val memoryData = recentSamples.map {
        (it.mem.usedMB.toFloat() / it.mem.totalMB.toFloat()) * 100f
    }
    val trend = calculateTrend(memoryData)
    val usagePercentage = (currentStats.usedMB.toFloat() / currentStats.totalMB.toFloat()) * 100f

    LiveStatsCard(
        title = "🧠 Memory Usage",
        currentValue = String.format("%.1f", usagePercentage),
        unit = "%",
        data = memoryData,
        trend = trend,
        color = when {
            usagePercentage > 85 -> Color(0xFFE57373)
            usagePercentage > 70 -> Color(0xFFFFB74D)
            else -> Color(0xFF64B5F6)
        },
        maxValue = 100f,
        modifier = modifier.border(
            width = 1.dp,
            color = Color.White.copy(alpha = 0.6f),
            shape = RoundedCornerShape(8.dp)
        )
    )
}

// Enhanced Battery Card with Live Data
@Composable
fun LiveBatteryCard(
    currentStats: com.sp45.androidmanager.domain.model.BatteryStatsUi,
    recentSamples: List<SystemStats>,
    modifier: Modifier = Modifier
) {
    val batteryData = recentSamples.map { it.battery.levelPct.toFloat() }
    val trend = calculateTrend(batteryData)

    LiveStatsCard(
        title = "🔋 Battery Level",
        currentValue = currentStats.levelPct.toString(),
        unit = "%",
        data = batteryData,
        trend = trend,
        color = when {
            currentStats.levelPct < 20 -> Color(0xFFE57373)
            currentStats.levelPct < 50 -> Color(0xFFFFB74D)
            else -> Color(0xFF81C784)
        },
        maxValue = 100f,
        modifier = modifier.border(
            width = 1.dp,
            color = Color.White.copy(alpha = 0.6f),
            shape = RoundedCornerShape(8.dp)
        )
    )
}

private fun calculateTrend(data: List<Float>): String {
    if (data.size < 3) return ""

    val recent = data.takeLast(3)
    val older = data.dropLast(3).takeLast(3)

    if (older.isEmpty()) return ""

    val recentAvg = recent.average()
    val olderAvg = older.average()
    val change = recentAvg - olderAvg

    return when {
        change > 2 -> "↑ Rising"
        change < -2 -> "↓ Falling"
        else -> "→ Stable"
    }
}