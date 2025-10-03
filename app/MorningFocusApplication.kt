package com.example.morningfocusalarm

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MorningFocusApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
    }
    
    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(NotificationManager::class.java)
            
            val alarmChannel = NotificationChannel(
                ALARM_CHANNEL_ID,
                "Alarm Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Morning alarm notifications"
                enableVibration(true)
            }
            
            val serviceChannel = NotificationChannel(
                SERVICE_CHANNEL_ID,
                "Lockout Service",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "App lockout monitoring service"
            }
            
            notificationManager.createNotificationChannel(alarmChannel)
            notificationManager.createNotificationChannel(serviceChannel)
        }
    }
    
    companion object {
        const val ALARM_CHANNEL_ID = "alarm_channel"
        const val SERVICE_CHANNEL_ID = "service_channel"
    }
}
