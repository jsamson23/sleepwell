package com.example.morningfocusalarm.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.example.morningfocusalarm.model.AlarmSettings
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Singleton
class PreferencesManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val dataStore = context.dataStore
    
    private object Keys {
        val IS_ENABLED = booleanPreferencesKey("is_enabled")
        val ALARM_HOUR = intPreferencesKey("alarm_hour")
        val ALARM_MINUTE = intPreferencesKey("alarm_minute")
        val LOCKOUT_DURATION = intPreferencesKey("lockout_duration")
        val IS_NIGHTLY_ENABLED = booleanPreferencesKey("is_nightly_enabled")
        val NIGHTLY_HOUR = intPreferencesKey("nightly_hour")
        val NIGHTLY_MINUTE = intPreferencesKey("nightly_minute")
        val LOCKED_APPS = stringSetPreferencesKey("locked_apps")
        val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
    }
    
    val settings: Flow<AlarmSettings> = dataStore.data.map { preferences ->
        AlarmSettings(
            isEnabled = preferences[Keys.IS_ENABLED] ?: false,
            alarmHour = preferences[Keys.ALARM_HOUR] ?: 7,
            alarmMinute = preferences[Keys.ALARM_MINUTE] ?: 0,
            lockoutDurationMinutes = preferences[Keys.LOCKOUT_DURATION] ?: 30,
            isNightlyLockoutEnabled = preferences[Keys.IS_NIGHTLY_ENABLED] ?: false,
            nightlyLockoutHour = preferences[Keys.NIGHTLY_HOUR] ?: 22,
            nightlyLockoutMinute = preferences[Keys.NIGHTLY_MINUTE] ?: 0,
            lockedAppPackages = preferences[Keys.LOCKED_APPS] ?: emptySet()
        )
    }
    
    val isOnboardingCompleted: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[Keys.ONBOARDING_COMPLETED] ?: false
    }
    
    suspend fun updateSettings(settings: AlarmSettings) {
        dataStore.edit { preferences ->
            preferences[Keys.IS_ENABLED] = settings.isEnabled
            preferences[Keys.ALARM_HOUR] = settings.alarmHour
            preferences[Keys.ALARM_MINUTE] = settings.alarmMinute
            preferences[Keys.LOCKOUT_DURATION] = settings.lockoutDurationMinutes
            preferences[Keys.IS_NIGHTLY_ENABLED] = settings.isNightlyLockoutEnabled
            preferences[Keys.NIGHTLY_HOUR] = settings.nightlyLockoutHour
            preferences[Keys.NIGHTLY_MINUTE] = settings.nightlyLockoutMinute
            preferences[Keys.LOCKED_APPS] = settings.lockedAppPackages
        }
    }
    
    suspend fun setOnboardingCompleted() {
        dataStore.edit { it[Keys.ONBOARDING_COMPLETED] = true }
    }
    
    suspend fun setEnabled(enabled: Boolean) {
        dataStore.edit { it[Keys.IS_ENABLED] = enabled }
    }
}