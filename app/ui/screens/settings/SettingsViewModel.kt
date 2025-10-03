package com.example.morningfocusalarm.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.morningfocusalarm.data.repository.AlarmRepository
import com.example.morningfocusalarm.util.PermissionHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PermissionState(
    val hasUsageStats: Boolean = false,
    val hasOverlay: Boolean = false,
    val hasExactAlarm: Boolean = false,
    val hasNotification: Boolean = false
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val alarmRepository: AlarmRepository,
    private val permissionHelper: PermissionHelper
) : ViewModel() {
    
    val settings = alarmRepository.settings.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        com.example.morningfocusalarm.model.AlarmSettings()
    )
    
    val permissions = flow {
        while (true) {
            emit(
                PermissionState(
                    hasUsageStats = permissionHelper.hasUsageStatsPermission(),
                    hasOverlay = permissionHelper.hasOverlayPermission(),
                    hasExactAlarm = permissionHelper.hasExactAlarmPermission(),
                    hasNotification = permissionHelper.hasNotificationPermission()
                )
            )
            kotlinx.coroutines.delay(1000)
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        PermissionState()
    )
    
    fun setAlarmTime(hour: Int, minute: Int) {
        viewModelScope.launch {
            val current = settings.value
            alarmRepository.updateSettings(
                current.copy(alarmHour = hour, alarmMinute = minute)
            )
        }
    }
    
    fun setLockoutDuration(minutes: Int) {
        viewModelScope.launch {
            val current = settings.value
            alarmRepository.updateSettings(
                current.copy(lockoutDurationMinutes = minutes)
            )
        }
    }
    
    fun setNightlyLockoutEnabled(enabled: Boolean) {
        viewModelScope.launch {
            val current = settings.value
            alarmRepository.updateSettings(
                current.copy(isNightlyLockoutEnabled = enabled)
            )
        }
    }
    
    fun setNightlyLockoutTime(hour: Int, minute: Int) {
        viewModelScope.launch {
            val current = settings.value
            alarmRepository.updateSettings(
                current.copy(nightlyLockoutHour = hour, nightlyLockoutMinute = minute)
            )
        }
    }
    
    fun requestUsageStatsPermission() {
        permissionHelper.openUsageStatsSettings()
    }
    
    fun requestOverlayPermission() {
        permissionHelper.openOverlaySettings()
    }
    
    fun requestExactAlarmPermission() {
        permissionHelper.openExactAlarmSettings()
    }
    
    fun requestNotificationPermission() {
        permissionHelper.openNotificationSettings()
    }
}