package com.sp45.androidmanager.data.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.sp45.androidmanager.R
import javax.inject.Inject

class AlertNotificationManager @Inject constructor(
    private val context: Context
) {

    companion object {
        private const val ALERT_CHANNEL_ID = "droid_manager_alert_channel"
        private const val CPU_ALERT_NOTIFICATION_ID = 2
    }

    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "System Alerts"
            val descriptionText = "Notifications for high system resource usage"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(ALERT_CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showCpuAlert() {
        val builder = NotificationCompat.Builder(context, ALERT_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("High CPU Usage Detected")
            .setContentText("System CPU load is critically high.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        notificationManager.notify(CPU_ALERT_NOTIFICATION_ID, builder.build())
    }
}
