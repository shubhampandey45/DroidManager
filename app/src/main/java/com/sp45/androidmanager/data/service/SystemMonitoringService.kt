package com.sp45.androidmanager.data.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
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

    private var collectJob: Job? = null
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var currentSessionId: String? = null

    companion object {
        const val NOTIFICATION_ID = 1001
        const val CHANNEL_ID = "system_monitoring_channel"
        const val CHANNEL_NAME = "System Monitoring"
        const val ACTION_START_MONITORING = "START_MONITORING"
        const val ACTION_STOP_MONITORING = "STOP_MONITORING"

        private const val TAG = "SystemMonitoringService"
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "Service created")
        createNotificationChannel()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "Service started with action: ${intent?.action}")

        when (intent?.action) {
            ACTION_START_MONITORING -> startMonitoring()
            ACTION_STOP_MONITORING -> stopMonitoring()
            else -> startMonitoring()
        }

        return START_STICKY
    }

    private fun startMonitoring() {
        if (collectJob?.isActive == true) {
            Log.d(TAG, "Monitoring already active")
            return
        }

        currentSessionId = "session_${System.currentTimeMillis()}"
        startForeground(NOTIFICATION_ID, createNotification("Starting monitoring..."))

        collectJob = serviceScope.launch {
            Log.d(TAG, "Starting data collection loop with session: $currentSessionId")
            var collectCount = 0

            try {
                while (isActive) {
                    val startTime = System.currentTimeMillis()

                    try {
                        // Use repository to collect and store data
                        val insertedId = repository.collectAndStoreStats(currentSessionId)

                        // Get the stats for notification update
                        val systemStats = repository.collectCurrentSystemStats()

                        collectCount++
                        Log.d(TAG, "Collected stats #$collectCount, inserted with ID: $insertedId")

                        // Update notification with latest data
                        updateNotification(systemStats, collectCount)

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
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
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
        val notificationIntent = Intent(this, MainActivity::class.java)
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
                stats != null -> "Collected $count samples | CPU: ${String.format("%.1f", stats.cpu.systemLoad)} | Battery: ${stats.battery.levelPct}%"
                else -> "Collecting data... ($count samples)"
            }

            val notification = createNotification(contentText)
            notificationManager.notify(NOTIFICATION_ID, notification)
        } catch (e: Exception) {
            Log.w(TAG, "Failed to update notification: ${e.message}")
        }
    }

    override fun onDestroy() {
        Log.d(TAG, "Service destroyed")
        collectJob?.cancel()
        serviceScope.cancel()
        super.onDestroy()
    }
}