package com.sp45.androidmanager.presentation.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sp45.androidmanager.presentation.ui.main.DatabaseInfo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatabaseInfoCard(
    databaseInfo: DatabaseInfo,
    onManualCollect: (() -> Unit)? = null,
    onClearAllData: (() -> Unit)? = null,
    onDeleteOldData: ((Int) -> Unit)? = null
) {
    var showClearDialog by remember { mutableStateOf(false) }
    var showDeleteOldDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Database Stats Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "📊 ${databaseInfo.totalRecords} records",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "${String.format("%.1f", databaseInfo.databaseSizeEstimate)} KB used",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                onManualCollect?.let { collect ->
                    FilledTonalButton(
                        onClick = collect,
                        modifier = Modifier.size(height = 36.dp, width = 100.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp)
                    ) {
                        Text(
                            "Collect",
                            fontSize = 12.sp
                        )
                    }
                }
            }

            // Action Buttons Row (only show if callbacks are provided)
            if (onClearAllData != null || onDeleteOldData != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    onDeleteOldData?.let { deleteOld ->
                        OutlinedButton(
                            onClick = { showDeleteOldDialog = true },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Delete Old", fontSize = 12.sp)
                        }
                    }

                    onClearAllData?.let { clearAll ->
                        OutlinedButton(
                            onClick = { showClearDialog = true },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Clear All", fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }

    // Clear All Data Dialog
    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = {
                Text(
                    text = "🗑️ Clear All Data",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text("This will permanently delete all stored system statistics. This action cannot be undone!")
            },
            confirmButton = {
                Button(
                    onClick = {
                        onClearAllData?.invoke()
                        showClearDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Clear All Data")
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Delete Old Data Dialog
    if (showDeleteOldDialog) {
        var selectedDays by remember { mutableIntStateOf(7) }

        AlertDialog(
            onDismissRequest = { showDeleteOldDialog = false },
            title = {
                Text(
                    text = "🗑️ Delete Old Data",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text("Delete data older than:")
                    Spacer(modifier = Modifier.height(12.dp))

                    // Days selection
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(1, 7, 30).forEach { days ->
                            FilterChip(
                                onClick = { selectedDays = days },
                                label = {
                                    Text("${days}d", fontSize = 12.sp)
                                },
                                selected = selectedDays == days,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "This will delete all data older than $selectedDays day${if (selectedDays != 1) "s" else ""}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteOldData?.invoke(selectedDays)
                        showDeleteOldDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("Delete Old Data")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteOldDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}