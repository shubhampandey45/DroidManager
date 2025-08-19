package com.sp45.androidmanager

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat

class DroidService : Service() {

    override fun onCreate() {
        super.onCreate()
        Log.d("DroidService", "onCreate called")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("DroidService", "onStartCommand called")
        when(intent?.action){
            Actions.START.toString() -> startDroidService()
            Actions.STOP.toString() -> stopSelf()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun startDroidService(){
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )
        val notification = NotificationCompat.Builder(this, "droid_service_channel")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("DroidService is running")
            .setContentText("Logging System Vitals")
            .setContentIntent(pendingIntent)
            .build()
        startForeground(1, notification)
    }

    enum class Actions{
        START,
        STOP
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("DroidService", "onDestroy called")
        Toast.makeText(this, "DroidService stopping", Toast.LENGTH_SHORT).show()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}