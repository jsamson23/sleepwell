package com.example.morningfocusalarm.model

data class AlarmSettings(
    val isEnabled: Boolean = false,
    val alarmHour: Int = 7,
    val alarmMinute: Int = 0,
    val lockoutDurationMinutes: Int = 30,
    val isNightlyLockoutEnabled: Boolean = false,
    val nightlyLockoutHour: Int = 22,
    val nightlyLockoutMinute: Int = 0,
    val lockedAppPackages: Set<String> = emptySet()
)