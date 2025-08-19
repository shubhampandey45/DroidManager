package com.sp45.androidmanager.presentation.ui.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.sp45.androidmanager.R
import com.sp45.androidmanager.presentation.ui.components.BatteryCard
import com.sp45.androidmanager.presentation.ui.components.CpuCard
import com.sp45.androidmanager.presentation.ui.components.MemCard
import com.sp45.androidmanager.presentation.ui.components.NetworkCard
import com.sp45.androidmanager.presentation.ui.components.ServiceControlCard
import com.sp45.androidmanager.presentation.ui.components.StorageCard

@Composable
fun Dashboard(
    viewModel: StatsViewModel,
    modifier: Modifier = Modifier
) {
    val liveStats by viewModel.liveStats
    val serviceRunning by viewModel.serviceRunning.collectAsState()

    LazyColumn(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Service Control Card
        item {
            ServiceControlCard(
                isRunning = serviceRunning,
                onStartClick = { viewModel.startMonitoringService() },
                onStopClick = { viewModel.stopMonitoringService() }
            )
        }

        // Live Stats Section (only when data is available)
        liveStats?.let { stats ->
//            item {
//                Text(
//                    "🔴 LIVE DATA",
//                    style = MaterialTheme.typography.titleMedium,
//                    fontWeight = FontWeight.SemiBold,
//                    color = Color(0xFFD32F2F),
//                    modifier = Modifier.padding(top = 8.dp),
//                    textAlign = TextAlign(Alignment.Center)
//                )
//            }

            item { CpuCard(stats.cpu) }
            item { MemCard(stats.mem) }
            item { BatteryCard(stats.battery) }
            item { StorageCard(stats.storage) }
            item { NetworkCard(stats.net) }
        }

        // Message when no live data is available
        if (liveStats == null) {
            item {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(R.drawable.img_1),
                        contentDescription = null,
                        alignment = Alignment.Center,
                        modifier = Modifier.size(300.dp) // square 200x200
                    )

//                    Text(
//                        "Know your Droid",
//                        style = MaterialTheme.typography.titleMedium,
//                        textAlign = TextAlign.Center,
//                        color = Color.White
////                    )
//                    Spacer(modifier = Modifier.height(4.dp))
//                    Text(
//                        text = "Real-time data will appear here",
//                        style = MaterialTheme.typography.bodyMedium,
//                        textAlign = TextAlign.Center,
//                        color = Color.White
//                    )
                }
            }
        }
    }
}