package com.sp45.androidmanager.presentation.ui.livestats

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.sp45.androidmanager.presentation.ui.components.LiveBatteryCard
import com.sp45.androidmanager.presentation.ui.components.LiveCpuCard
import com.sp45.androidmanager.presentation.ui.components.LiveMemoryCard
import com.sp45.androidmanager.presentation.ui.components.LivePerformanceSummaryCard
import com.sp45.androidmanager.presentation.ui.main.StatsViewModel

@Composable
fun LiveStatsScreen(
    viewModel: StatsViewModel,
    modifier: Modifier = Modifier,
    innerPadding: PaddingValues = PaddingValues(0.dp) // Add this parameter
) {
    val liveStats by viewModel.liveStats
    val serviceRunning by viewModel.serviceRunning.collectAsState()
    val recentLiveSamples by viewModel.recentLiveSamples.collectAsState()

    LazyColumn(
        modifier = modifier
            .padding(innerPadding) // Apply innerPadding from Scaffold
            .padding(16.dp), // Keep your existing horizontal padding
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Live Stats Section with Sparklines
        liveStats?.let { stats ->
            // Live Performance Summary
            item {
                LivePerformanceSummaryCard(
                    stats = stats,
                    recentSamples = recentLiveSamples
                )
            }
            // Live cards with sparklines
            item {
                LiveCpuCard(
                    currentStats = stats.cpu,
                    recentSamples = recentLiveSamples
                )
            }
            item {
                LiveMemoryCard(
                    currentStats = stats.mem,
                    recentSamples = recentLiveSamples
                )
            }
            item {
                LiveBatteryCard(
                    currentStats = stats.battery,
                    recentSamples = recentLiveSamples
                )
            }
        }

        // Message when no live data is available
        if (liveStats == null) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            color = Color.White,
                            shape = RoundedCornerShape(8.dp),
                            width = 1.dp
                        ),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.Transparent
                    )
                ) {
                    Text(
                        text = if (serviceRunning) {
                            "📊 Collecting live data...\nGraphs will appear shortly"
                        } else {
                            "📊 Start monitoring from Dashboard to see live charts and trends"
                        },
                        modifier = Modifier.padding(24.dp),
                        style = MaterialTheme.typography.titleSmall,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}