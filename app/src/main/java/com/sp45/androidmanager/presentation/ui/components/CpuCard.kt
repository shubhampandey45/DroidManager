package com.sp45.androidmanager.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.unit.sp
import com.sp45.androidmanager.domain.model.CpuStats
import java.util.Locale

@Composable
fun CpuCard(cpu: CpuStats) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(
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
                    text = "🔧 CPU Performance",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                // Load Level Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            when (cpu.loadLevel) {
                                "LOW" -> Color(0xFF4CAF50).copy(alpha = 0.2f)
                                "MODERATE" -> Color(0xFFFF9800).copy(alpha = 0.2f)
                                "HIGH" -> Color(0xFFFF5722).copy(alpha = 0.2f)
                                "CRITICAL" -> Color(0xFFF44336).copy(alpha = 0.2f)
                                else -> Color.Gray.copy(alpha = 0.2f)
                            }
                        )
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = cpu.loadLevel,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = when (cpu.loadLevel) {
                            "LOW" -> Color(0xFF2E7D32)
                            "MODERATE" -> Color(0xFFE65100)
                            "HIGH" -> Color(0xFFD84315)
                            "CRITICAL" -> Color(0xFFC62828)
                            else -> Color.Gray
                        }
                    )
                }
            }

            // System Load with visual indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "System Load",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = String.format(Locale.US, "%.2f", cpu.systemLoad),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = when (cpu.loadLevel) {
                        "LOW" -> Color(0xFF4CAF50)
                        "MODERATE" -> Color(0xFFFF9800)
                        "HIGH" -> Color(0xFFFF5722)
                        "CRITICAL" -> Color(0xFFF44336)
                        else -> MaterialTheme.colorScheme.onSurface
                    }
                )
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))

            // Core Information
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                InfoItem(
                    label = "Active Processes",
                    value = cpu.runningProcesses.toString()
                )
                InfoItem(
                    label = "Online Cores",
                    value = cpu.onlineCores.toString()
                )
            }

            // Temperature if available
            cpu.temperatureC?.let { temperature ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Temperature",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = String.format(Locale.US, "%.1f°C", temperature),
                        fontWeight = FontWeight.Medium,
                        color = if (temperature > 70) Color(0xFFFF5722) else MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            // Core Frequencies (collapsed view)
            if (cpu.coreFreqMHz.isNotEmpty()) {
                Text(
                    text = "Core Frequencies: ${cpu.coreFreqMHz.take(4).joinToString(", ")} MHz${if (cpu.coreFreqMHz.size > 4) "..." else ""}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}