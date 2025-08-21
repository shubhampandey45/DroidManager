package com.sp45.androidmanager.presentation.ui.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
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
    modifier: Modifier = Modifier,
    innerPadding: PaddingValues = PaddingValues(0.dp) // Add this parameter
) {
    val liveStats by viewModel.liveStats
    val serviceRunning by viewModel.serviceRunning.collectAsState()

    LazyColumn(
        modifier = modifier
            .padding(innerPadding) // Apply innerPadding from Scaffold
            .padding(16.dp), // Keep your existing horizontal padding
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
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(R.drawable.img_1),
                        contentDescription = null,
                        alignment = Alignment.Center,
                        modifier = Modifier.size(300.dp)
                    )
                }
            }
        }
    }
}