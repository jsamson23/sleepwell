package com.example.morningfocusalarm.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.morningfocusalarm.MorningFocusApplication
import com.example.morningfocusalarm.R
import com.example.morningfocusalarm.data.local.PreferencesManager
import com.example.morningfocusalarm.ui.screens.lockscreen.LockScreenActivity
import com.example.morningfocusalarm.util.UsageStatsHelper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class LockoutService : Service() {
    
    @Inject
    lateinit var preferencesManager: PreferencesManager
    
    @Inject
    lateinit var usageStatsHelper: UsageStatsHelper
    
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var monitoringJob: Job? = null
    
    override fun onBind(intent: Intent?): IBinder? = null
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val lockoutType = intent?.getStringExtra(EXTRA_LOCKOUT_TYPE) ?: TYPE_MORNING
        
        val notification = createNotification()
        startForeground(NOTIFICATION_ID, notification)
        
        serviceScope.launch {
            val settings = preferencesManager.settings.first()
            val unlockTime = calculateUnlockTime(lockoutType, settings.lockoutDurationMinutes)
            
            startMonitoring(settings.lockedAppPackages, unlockTime)
            scheduleStop(unlockTime)
        }
        
        return START_STICKY
    }
    
    private fun calculateUnlockTime(type: String, lockoutDurationMinutes: Int): Long {
        return Calendar.getInstance().apply {
            if (type == TYPE_MORNING) {
                add(Calendar.MINUTE, lockoutDurationMinutes)
            } else {
                // Nightly lockout: calculate next morning + lockout duration
                val settings = runBlocking { preferencesManager.settings.first() }
                set(Calendar.HOUR_OF_DAY, settings.alarmHour)
                set(Calendar.MINUTE, settings.alarmMinute)
                set(Calendar.SECOND, 0)
                
                if (timeInMillis <= System.currentTimeMillis()) {
                    add(Calendar.DAY_OF_MONTH, 1)
                }
                add(Calendar.MINUTE, lockoutDurationMinutes)
            }
        }.timeInMillis
    }
    
    private fun startMonitoring(lockedApps: Set<String>, unlockTime: Long) {
        monitoringJob?.cancel()
        monitoringJob = serviceScope.launch {
            while (isActive && System.currentTimeMillis() < unlockTime) {
                val foregroundApp = usageStatsHelper.getForegroundApp()
                if (foregroundApp != null && lockedApps.contains(foregroundApp)) {
                    withContext(Dispatchers.Main) {
                        showLockScreen(unlockTime)
                    }
                }
                delay(1000) // Check every second
            }
        }
    }
    
    private fun showLockScreen(unlockTime: Long) {
        val intent = Intent(this, LockScreenActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(LockScreenActivity.EXTRA_UNLOCK_TIME, unlockTime)
        }
        startActivity(intent)
    }
    
    private fun scheduleStop(unlockTime: Long) {
        val delay = unlockTime - System.currentTimeMillis()
        serviceScope.launch {
            delay(delay)
            stopSelf()
        }
    }
    
    private fun createNotification(): Notification {
        val builder = NotificationCompat.Builder(this, MorningFocusApplication.SERVICE_CHANNEL_ID)
            .setContentTitle("Morning Focus Active")
            .setContentText("Selected apps are locked")
            .setSmallIcon(R.drawable.ic_notification)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
        
        return builder.build()
    }
    
    override fun onDestroy() {
        monitoringJob?.cancel()
        serviceScope.cancel()
        super.onDestroy()
    }
    
    companion object {
        private const val NOTIFICATION_ID = 1
        private const val EXTRA_LOCKOUT_TYPE = "lockout_type"
        
        const val TYPE_MORNING = "morning"
        const val TYPE_NIGHTLY = "nightly"
        
        fun startService(context: Context, type: String) {
            val intent = Intent(context, LockoutService::class.java).apply {
                putExtra(EXTRA_LOCKOUT_TYPE, type)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }
    }
}