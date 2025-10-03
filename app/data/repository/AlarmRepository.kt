package com.example.morningfocusalarm.data.repository

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.morningfocusalarm.data.local.PreferencesManager
import com.example.morningfocusalarm.model.AlarmSettings
import com.example.morningfocusalarm.service.AlarmReceiver
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AlarmRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val preferencesManager: PreferencesManager
) {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    
    val settings: Flow<AlarmSettings> = preferencesManager.settings
    
    suspend fun updateSettings(settings: AlarmSettings) {
        preferencesManager.updateSettings(settings)
        if (settings.isEnabled) {
            scheduleAlarms(settings)
        } else {
            cancelAlarms()
        }
    }
    
    suspend fun setEnabled(enabled: Boolean) {
        preferencesManager.setEnabled(enabled)
        val settings = preferencesManager.settings.first()
        if (enabled) {
            scheduleAlarms(settings)
        } else {
            cancelAlarms()
        }
    }
    
    suspend fun scheduleAlarms(settings: AlarmSettings = preferencesManager.settings.first()) {
        cancelAlarms()
        
        if (!settings.isEnabled) return
        
        // Schedule morning alarm
        val morningTime = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, settings.alarmHour)
            set(Calendar.MINUTE, settings.alarmMinute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            
            if (timeInMillis <= System.currentTimeMillis()) {
                add(Calendar.DAY_OF_MONTH, 1)
            }
        }
        
        val morningIntent = Intent(context, AlarmReceiver::class.java).apply {
            action = AlarmReceiver.ACTION_MORNING_ALARM
        }
        val morningPendingIntent = PendingIntent.getBroadcast(
            context,
            MORNING_ALARM_REQUEST_CODE,
            morningIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    morningTime.timeInMillis,
                    morningPendingIntent
                )
            }
        } else {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                morningTime.timeInMillis,
                morningPendingIntent
            )
        }
        
        // Schedule nightly lockout if enabled
        if (settings.isNightlyLockoutEnabled) {
            val nightlyTime = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, settings.nightlyLockoutHour)
                set(Calendar.MINUTE, settings.nightlyLockoutMinute)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
                
                if (timeInMillis <= System.currentTimeMillis()) {
                    add(Calendar.DAY_OF_MONTH, 1)
                }
            }
            
            val nightlyIntent = Intent(context, AlarmReceiver::class.java).apply {
                action = AlarmReceiver.ACTION_NIGHTLY_LOCKOUT
            }
            val nightlyPendingIntent = PendingIntent.getBroadcast(
                context,
                NIGHTLY_ALARM_REQUEST_CODE,
                nightlyIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        nightlyTime.timeInMillis,
                        nightlyPendingIntent
                    )
                }
            } else {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    nightlyTime.timeInMillis,
                    nightlyPendingIntent
                )
            }
        }
    }
    
    private fun cancelAlarms() {
        val morningIntent = Intent(context, AlarmReceiver::class.java).apply {
            action = AlarmReceiver.ACTION_MORNING_ALARM
        }
        val morningPendingIntent = PendingIntent.getBroadcast(
            context,
            MORNING_ALARM_REQUEST_CODE,
            morningIntent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        morningPendingIntent?.let { alarmManager.cancel(it) }
        
        val nightlyIntent = Intent(context, AlarmReceiver::class.java).apply {
            action = AlarmReceiver.ACTION_NIGHTLY_LOCKOUT
        }
        val nightlyPendingIntent = PendingIntent.getBroadcast(
            context,
            NIGHTLY_ALARM_REQUEST_CODE,
            nightlyIntent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        nightlyPendingIntent?.let { alarmManager.cancel(it) }
    }
    
    companion object {
        private const val MORNING_ALARM_REQUEST_CODE = 1001
        private const val NIGHTLY_ALARM_REQUEST_CODE = 1002
    }
}