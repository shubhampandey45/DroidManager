package com.sp45.androidmanager.presentation.ui.session

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.sp45.androidmanager.presentation.ui.main.StatsViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionScreen(
    modifier: Modifier = Modifier,
    viewModel: StatsViewModel,
    onNavigateToSessionDetail: (String) -> Unit = {},
) {
    val allSessions by viewModel.allSessions.collectAsState()
    val databaseInfo by viewModel.databaseInfo.collectAsState()

    var showDeleteDialog by remember { mutableStateOf<String?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Sessions Overview Card with storage refresh action
            SessionsOverviewCard(
                totalSessions = allSessions.size,
                totalRecords = databaseInfo.totalRecords,
                storageUsed = databaseInfo.databaseSizeEstimate,
                onRefreshStorage = {
                    viewModel.refreshDatabaseInfo()
                    // optional: show snackbar feedback
                    scope.launch {
                        snackbarHostState.showSnackbar("Storage info refreshed", duration = SnackbarDuration.Short)
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (allSessions.isNotEmpty()) {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(allSessions) { sessionId ->
                        SessionCard(
                            sessionId = sessionId,
                            viewModel = viewModel,
                            onDeleteClick = { showDeleteDialog = sessionId },
                            onViewDetailsClick = { onNavigateToSessionDetail(sessionId) }
                        )
                    }
                }
            } else {
                // Empty state
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "📁",
                            style = MaterialTheme.typography.displayLarge
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No monitoring sessions found",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Sessions are created automatically when you start monitoring",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }

    // Delete Session Confirmation Dialog
    showDeleteDialog?.let { sessionId ->
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = {
                Text(
                    text = "🗑️ Delete Session",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text("Delete session: $sessionId")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "This will permanently delete all data from this monitoring session. This action cannot be undone!",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteSession(sessionId)
                        showDeleteDialog = null
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                message = "Session deleted successfully",
                                duration = SnackbarDuration.Short
                            )
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Delete Session")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun SessionsOverviewCard(
    totalSessions: Int,
    totalRecords: Int,
    storageUsed: Float,
    onRefreshStorage: () -> Unit = {}
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Each item gets equal weight so spacing is even
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                OverviewStat(
                    title = "Sessions",
                    value = totalSessions.toString(),
                    icon = "📁"
                )
            }

            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                OverviewStat(
                    title = "Records",
                    value = totalRecords.toString(),
                    icon = "📊"
                )
            }

            // Storage column (also centered)
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "💾",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        text = "${String.format("%.1f", storageUsed)} KB",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Storage",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Refresh icon gets its own weight so spacing remains even
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                IconButton(
                    onClick = onRefreshStorage,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh Storage Info",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}


@Composable
private fun OverviewStat(
    title: String,
    value: String,
    icon: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = icon,
            style = MaterialTheme.typography.titleLarge
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun SessionCard(
    sessionId: String,
    viewModel: StatsViewModel,
    onDeleteClick: () -> Unit,
    onViewDetailsClick: () -> Unit
) {
    // Get session data
    val sessionStats by viewModel.getStatsForSession(sessionId).collectAsState()

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Session Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = formatSessionId(sessionId),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = sessionId,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Row {
                    IconButton(onClick = onDeleteClick) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Session",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Session Statistics
            if (sessionStats.isNotEmpty()) {
                val firstRecord = sessionStats.last() // Last because list is DESC ordered
                val lastRecord = sessionStats.first()
                val duration = lastRecord.timestamp - firstRecord.timestamp

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    SessionStat(
                        label = "Records",
                        value = "${sessionStats.size}",
                        icon = "📊"
                    )
                    SessionStat(
                        label = "Duration",
                        value = formatDuration(duration),
                        icon = "⏱️"
                    )
                    SessionStat(
                        label = "Started",
                        value = SimpleDateFormat("HH:mm", Locale.getDefault()).format(
                            Date(firstRecord.timestamp)
                        ),
                        icon = "🕐"
                    )
                }
            } else {
                Text(
                    text = "Loading session data...",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun SessionStat(
    label: String,
    value: String,
    icon: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = icon)
        Text(
            text = value,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

private fun formatSessionId(sessionId: String): String {
    return try {
        val timestamp = sessionId.removePrefix("session_").toLong()
        val date = Date(timestamp)
        SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault()).format(date)
    } catch (e: Exception) {
        sessionId.take(20) + "..."
    }
}

private fun formatDuration(durationMs: Long): String {
    val minutes = durationMs / (1000 * 60)
    val hours = minutes / 60
    return when {
        hours > 0 -> "${hours}h ${minutes % 60}m"
        minutes > 0 -> "${minutes}m"
        else -> "<1m"
    }
}
