package com.example.morningfocusalarm.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.morningfocusalarm.data.repository.AlarmRepository
import com.example.morningfocusalarm.ui.screens.alarm.AlarmAlertActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AlarmReceiver : BroadcastReceiver() {
    
    @Inject
    lateinit var alarmRepository: AlarmRepository
    
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            ACTION_MORNING_ALARM -> {
                // Show alarm alert
                val alarmIntent = Intent(context, AlarmAlertActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                context.startActivity(alarmIntent)
                
                // Start lockout service
                LockoutService.startService(context, LockoutService.TYPE_MORNING)
                
                // Reschedule for next day
                scope.launch {
                    alarmRepository.scheduleAlarms()
                }
            }
            ACTION_NIGHTLY_LOCKOUT -> {
                // Start nightly lockout
                LockoutService.startService(context, LockoutService.TYPE_NIGHTLY)
                
                // Reschedule for next day
                scope.launch {
                    alarmRepository.scheduleAlarms()
                }
            }
        }
    }
    
    companion object {
        const val ACTION_MORNING_ALARM = "com.example.morningfocusalarm.MORNING_ALARM"
        const val ACTION_NIGHTLY_LOCKOUT = "com.example.morningfocusalarm.NIGHTLY_LOCKOUT"
    }
}