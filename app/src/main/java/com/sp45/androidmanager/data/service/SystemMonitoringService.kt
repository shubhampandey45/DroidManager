package com.sp45.androidmanager.data.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.sp45.androidmanager.MainActivity
import com.sp45.androidmanager.R
import com.sp45.androidmanager.domain.model.SystemStats
import com.sp45.androidmanager.domain.repository.SystemStatsRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SystemMonitoringService : Service() {

    @Inject
    lateinit var repository: SystemStatsRepository

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    @Inject
    lateinit var alertNotificationManager: AlertNotificationManager

    private var collectJob: Job? = null
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var currentSessionId: String? = null
    private var collectCount = 0

    companion object {
        const val NOTIFICATION_ID = 1001
        const val CHANNEL_ID = "system_monitoring_channel"
        const val CHANNEL_NAME = "System Monitoring"
        const val ACTION_START_MONITORING = "START_MONITORING"
        const val ACTION_STOP_MONITORING = "STOP_MONITORING"

        // Broadcast actions
        const val ACTION_STATS_UPDATED = "com.sp45.androidmanager.STATS_UPDATED"
        const val ACTION_SERVICE_STATE_CHANGED = "com.sp45.androidmanager.SERVICE_STATE_CHANGED"
        const val EXTRA_STATS = "extra_stats"
        const val EXTRA_COLLECT_COUNT = "extra_collect_count"
        const val EXTRA_IS_RUNNING = "extra_is_running"

        // SharedPreferences keys
        const val PREF_SERVICE_RUNNING = "service_running"
        const val PREF_SESSION_ID = "current_session_id"
        const val PREF_COLLECT_COUNT = "collect_count"

        private const val TAG = "SystemMonitoringService"
        private const val CPU_USAGE_THRESHOLD = 80.0
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "Service created")
        createNotificationChannel()

        // Restore state if service was restarted
        restoreState()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "Service started with action: ${intent?.action}")

        when (intent?.action) {
            ACTION_START_MONITORING -> startMonitoring()
            ACTION_STOP_MONITORING -> stopMonitoring()
            else -> {
                // If no action specified and service is supposed to be running, restart monitoring
                if (sharedPreferences.getBoolean(PREF_SERVICE_RUNNING, false)) {
                    startMonitoring()
                } else {
                    stopMonitoring()
                }
            }
        }

        return START_STICKY // This ensures service restarts if killed by system
    }

    private fun restoreState() {
        val isRunning = sharedPreferences.getBoolean(PREF_SERVICE_RUNNING, false)
        if (isRunning) {
            currentSessionId = sharedPreferences.getString(PREF_SESSION_ID, null)
            collectCount = sharedPreferences.getInt(PREF_COLLECT_COUNT, 0)
            Log.d(TAG, "Restoring service state - running: $isRunning, session: $currentSessionId, count: $collectCount")
        }
    }

    private fun saveState() {
        sharedPreferences.edit().apply {
            putBoolean(PREF_SERVICE_RUNNING, collectJob?.isActive == true)
            putString(PREF_SESSION_ID, currentSessionId)
            putInt(PREF_COLLECT_COUNT, collectCount)
            apply()
        }
    }

    private fun startMonitoring() {
        if (collectJob?.isActive == true) {
            Log.d(TAG, "Monitoring already active")
            broadcastServiceState(true)
            return
        }

        currentSessionId = currentSessionId ?: "session_${System.currentTimeMillis()}"
        startForeground(NOTIFICATION_ID, createNotification("Starting monitoring..."))

        // Save state and broadcast
        saveState()
        broadcastServiceState(true)

        collectJob = serviceScope.launch {
            Log.d(TAG, "Starting data collection loop with session: $currentSessionId")

            try {
                while (isActive) {
                    val startTime = System.currentTimeMillis()

                    try {
                        // Use repository to collect and store data
                        val insertedId = repository.collectAndStoreStats(currentSessionId)

                        // Get the stats for notification update and broadcast
                        val systemStats = repository.collectCurrentSystemStats()

                        // Check for high CPU usage and send alert
                        if (systemStats.cpu.systemLoad > CPU_USAGE_THRESHOLD) {
                            try {
                                alertNotificationManager.showCpuAlert()
                                Log.d(TAG, "High CPU usage detected (>${CPU_USAGE_THRESHOLD}%), triggering alert.")
                            } catch (e: Exception) {
                                Log.e(TAG, "Failed to show CPU alert: ${e.message}", e)
                            }
                        }

                        collectCount++
                        Log.d(TAG, "Collected stats #$collectCount, inserted with ID: $insertedId")

                        // Update notification with latest data
                        updateNotification(systemStats, collectCount)

                        // Broadcast the updated stats
                        broadcastStatsUpdate(systemStats, collectCount)

                        // Save updated count
                        saveState()

                    } catch (e: Exception) {
                        Log.e(TAG, "Error collecting/storing stats: ${e.message}", e)
                        updateNotification(null, collectCount, "Error: ${e.message}")
                    }

                    // Calculate delay to maintain 30-second intervals
                    val elapsedTime = System.currentTimeMillis() - startTime
                    val delayTime = (30_000 - elapsedTime).coerceAtLeast(1000)

                    delay(delayTime)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Data collection loop error: ${e.message}", e)
            }
        }
    }

    private fun stopMonitoring() {
        Log.d(TAG, "Stopping monitoring")
        collectJob?.cancel()
        collectJob = null
        currentSessionId = null
        collectCount = 0

        // Clear state and broadcast
        clearState()
        broadcastServiceState(false)

        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun clearState() {
        sharedPreferences.edit().apply {
            putBoolean(PREF_SERVICE_RUNNING, false)
            remove(PREF_SESSION_ID)
            remove(PREF_COLLECT_COUNT)
            apply()
        }
    }

    private fun broadcastServiceState(isRunning: Boolean) {
        val intent = Intent(ACTION_SERVICE_STATE_CHANGED).apply {
            putExtra(EXTRA_IS_RUNNING, isRunning)
        }
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }

    private fun broadcastStatsUpdate(stats: SystemStats, count: Int) {
        val intent = Intent(ACTION_STATS_UPDATED).apply {
            // You might want to serialize stats or send specific values
            putExtra(EXTRA_COLLECT_COUNT, count)
            // Add other stats data as needed
        }
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Shows system monitoring status"
                setShowBadge(false)
                setSound(null, null)
            }

            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(contentText: String = "Monitoring system parameters..."): Notification {
        val notificationIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val stopIntent = Intent(this, SystemMonitoringService::class.java).apply {
            action = ACTION_STOP_MONITORING
        }
        val stopPendingIntent = PendingIntent.getService(
            this, 1, stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("System Monitor")
            .setContentText(contentText)
            .setSmallIcon(R.drawable.ic_monitoring)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setSilent(true)
            .addAction(
                R.drawable.ic_stop,
                "Stop",
                stopPendingIntent
            )
            .setPriority(NotificationCompat.PRIORITY_LOW) // Add explicit priority
            .build()
    }

    private fun updateNotification(
        stats: SystemStats?,
        count: Int,
        errorMessage: String? = null
    ) {
        try {
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

            val contentText = when {
                errorMessage != null -> errorMessage
                stats != null -> "Sample #$count | CPU: ${String.format("%.1f", stats.cpu.systemLoad)}% | Battery: ${stats.battery.levelPct}%"
                else -> "Collecting sample #$count..."
            }

            val notification = createNotification(contentText)
            notificationManager.notify(NOTIFICATION_ID, notification)

            Log.d(TAG, "Notification updated: $contentText")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to update notification: ${e.message}", e)
        }
    }

    override fun onDestroy() {
        Log.d(TAG, "Service destroyed")
        collectJob?.cancel()
        serviceScope.cancel()
        clearState()
        broadcastServiceState(false)
        super.onDestroy()
    }
}
