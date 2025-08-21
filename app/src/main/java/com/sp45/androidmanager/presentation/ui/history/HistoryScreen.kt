package com.sp45.androidmanager.presentation.ui.history

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.sp45.androidmanager.presentation.ui.components.DatabaseInfoCard
import com.sp45.androidmanager.presentation.ui.components.StoredStatsCard
import com.sp45.androidmanager.presentation.ui.main.DatabaseInfo
import com.sp45.androidmanager.presentation.ui.main.StatsViewModel
import androidx.compose.foundation.layout.padding

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    viewModel: StatsViewModel,
    modifier: Modifier = Modifier,
    innerPadding: PaddingValues = PaddingValues(0.dp) // Add this parameter
) {
    val recentStoredStats by viewModel.recentStoredStats.collectAsState()
    val databaseInfo by viewModel.databaseInfo.collectAsState()

    // Snackbar state for user feedback
    val snackbarHostState = remember { SnackbarHostState() }
    var lastOperation by remember { mutableStateOf<String?>(null) }

    // Show snackbar for operations
    LaunchedEffect(databaseInfo.totalRecords, lastOperation) {
        lastOperation?.let { operation ->
            when (operation) {
                "clear" -> {
                    snackbarHostState.showSnackbar(
                        message = "All data cleared successfully",
                        actionLabel = "OK",
                        duration = SnackbarDuration.Short
                    )
                }
                "delete_old" -> {
                    snackbarHostState.showSnackbar(
                        message = "Old data deleted successfully",
                        actionLabel = "OK",
                        duration = SnackbarDuration.Short
                    )
                }
                "manual_collect" -> {
                    snackbarHostState.showSnackbar(
                        message = "Data collected manually",
                        actionLabel = "OK",
                        duration = SnackbarDuration.Short
                    )
                }
                "refreshed" -> {
                    snackbarHostState.showSnackbar(
                        message = "Data refreshed",
                        duration = SnackbarDuration.Short
                    )
                }
            }
            lastOperation = null
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 8.dp) // Added top padding
        ) {
            // Enhanced Database Info Card with Actions
            DatabaseInfoCard(
                databaseInfo = databaseInfo,
                onClearAllData = {
                    viewModel.clearAllData()
                    lastOperation = "clear"
                },
                onDeleteOldData = { days ->
                    viewModel.deleteOldData(days)
                    lastOperation = "delete_old"
                },
                onManualCollect = {
                    viewModel.collectDataManually()
                    lastOperation = "manual_collect"
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Header with record count and refresh action
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "📊 Stats History",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${databaseInfo.totalRecords} records",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium
                    )

                    // Refresh button - only refreshes database info, doesn't collect new data
                    IconButton(
                        onClick = {
                            viewModel.refreshDatabaseInfo()
                            lastOperation = "refreshed"
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh Data",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Storage usage indicator
            if (databaseInfo.totalRecords > 0) {
                StorageIndicatorCard(databaseInfo)
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Show historical data or empty state
            if (recentStoredStats.isNotEmpty()) {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(recentStoredStats) { stats ->
                        StoredStatsCard(
                            stats = stats,
                            timestamp = stats.timestamp // Use the timestamp from SystemStats
                        )
                    }

                    // Footer with helpful info
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            )
                        ) {
                            Text(
                                text = "💡 Tip: Data is collected every 30 seconds while monitoring is active",
                                modifier = Modifier.padding(12.dp),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                // Enhanced empty state
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "📈",
                            style = MaterialTheme.typography.displayLarge
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No history data available",
                            style = MaterialTheme.typography.headlineSmall,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Start monitoring from the Dashboard to collect historical data",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        // Quick action button in empty state
                        FilledTonalButton(
                            onClick = {
                                viewModel.collectDataManually()
                                lastOperation = "manual_collect"
                            }
                        ) {
                            Text("Collect Sample Data")
                        }
                    }
                }
            }
        }

        // Snackbar host positioned at the bottom
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun StorageIndicatorCard(databaseInfo: DatabaseInfo) {
    val usagePercentage = (databaseInfo.databaseSizeEstimate / 10000f * 100).coerceAtMost(100f) // Assuming 10MB as "full"
    val indicatorColor = when {
        usagePercentage > 80 -> MaterialTheme.colorScheme.error
        usagePercentage > 60 -> Color(0xFFFF9800)
        else -> MaterialTheme.colorScheme.primary
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Storage Usage",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "${String.format("%.1f", usagePercentage)}%",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = indicatorColor
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            LinearProgressIndicator(
                progress = { usagePercentage / 100f },
                modifier = Modifier.fillMaxWidth(),
                color = indicatorColor,
                trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
            )

            if (usagePercentage > 70) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Consider deleting old data to free up space",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}