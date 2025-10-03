package com.example.morningfocusalarm.ui.screens.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.morningfocusalarm.ui.components.TimePickerDialog
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToAppList: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val settings by viewModel.settings.collectAsState()
    val permissions by viewModel.permissions.collectAsState()
    
    var showAlarmTimePicker by remember { mutableStateOf(false) }
    var showLockoutDurationDialog by remember { mutableStateOf(false) }
    var showNightlyTimePicker by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Permissions Section
            Text(
                text = "Permissions",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(16.dp)
            )
            
            PermissionItem(
                title = "Usage Stats",
                description = "Required to detect foreground apps",
                isGranted = permissions.hasUsageStats,
                onClick = { viewModel.requestUsageStatsPermission() }
            )
            
            PermissionItem(
                title = "Display Over Apps",
                description = "Required to show lock screen",
                isGranted = permissions.hasOverlay,
                onClick = { viewModel.requestOverlayPermission() }
            )
            
            PermissionItem(
                title = "Exact Alarms",
                description = "Required for precise alarm timing",
                isGranted = permissions.hasExactAlarm,
                onClick = { viewModel.requestExactAlarmPermission() }
            )
            
            PermissionItem(
                title = "Notifications",
                description = "Required for alarm alerts",
                isGranted = permissions.hasNotification,
                onClick = { viewModel.requestNotificationPermission() }
            )
            
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            
            // Alarm Settings
            Text(
                text = "Alarm Configuration",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(16.dp)
            )
            
            SettingItem(
                title = "Alarm Time",
                value = formatTime(settings.alarmHour, settings.alarmMinute),
                onClick = { showAlarmTimePicker = true }
            )
            
            SettingItem(
                title = "Lockout Duration",
                value = "${settings.lockoutDurationMinutes} minutes",
                onClick = { showLockoutDurationDialog = true }
            )
            
            SettingItem(
                title = "Select Apps to Lock",
                value = "${settings.lockedAppPackages.size} apps selected",
                onClick = onNavigateToAppList
            )
            
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            
            // Nightly Lockout
            Text(
                text = "Nightly Lockout",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(16.dp)
            )
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Enable Nightly Lockout",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = "Lock apps the night before",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = settings.isNightlyLockoutEnabled,
                    onCheckedChange = { viewModel.setNightlyLockoutEnabled(it) }
                )
            }
            
            if (settings.isNightlyLockoutEnabled) {
                SettingItem(
                    title = "Nightly Lockout Time",
                    value = formatTime(settings.nightlyLockoutHour, settings.nightlyLockoutMinute),
                    onClick = { showNightlyTimePicker = true }
                )
            }
        }
    }

    // Dialogs
    if (showAlarmTimePicker) {
        TimePickerDialog(
            initialHour = settings.alarmHour,
            initialMinute = settings.alarmMinute,
            onDismiss = { showAlarmTimePicker = false },
            onConfirm = { hour, minute ->
                viewModel.setAlarmTime(hour, minute)
                showAlarmTimePicker = false
            }
        )
    }

    if (showLockoutDurationDialog) {
        LockoutDurationDialog(
            currentDuration = settings.lockoutDurationMinutes,
            onDismiss = { showLockoutDurationDialog = false },
            onConfirm = { duration ->
                viewModel.setLockoutDuration(duration)
                showLockoutDurationDialog = false
            }
        )
    }

    if (showNightlyTimePicker) {
        TimePickerDialog(
            initialHour = settings.nightlyLockoutHour,
            initialMinute = settings.nightlyLockoutMinute,
            onDismiss = { showNightlyTimePicker = false },
            onConfirm = { hour, minute ->
                viewModel.setNightlyLockoutTime(hour, minute)
                showNightlyTimePicker = false
            }
        )
    }
}

private fun formatTime(hour: Int, minute: Int): String {
    val calendar = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, hour)
        set(Calendar.MINUTE, minute)
    }
    return SimpleDateFormat("h:mm a", Locale.getDefault()).format(calendar.time)
}