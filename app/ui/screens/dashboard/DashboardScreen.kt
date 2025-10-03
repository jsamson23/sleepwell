package com.example.morningfocusalarm.ui.screens.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onNavigateToSettings: () -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val settings by viewModel.settings.collectAsState()
    val hasAllPermissions by viewModel.hasAllPermissions.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Morning Focus Alarm") },
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, "Settings")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (!hasAllPermissions) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "⚠️ Permissions Required",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                        Text(
                            text = "Some permissions are missing. Go to Settings to grant them.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }
            
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Alarm Status",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    
                    Text(
                        text = formatTime(settings.alarmHour, settings.alarmMinute),
                        style = MaterialTheme.typography.displayLarge,
                        fontWeight = FontWeight.Bold,
                        color = if (settings.isEnabled) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                    
                    Text(
                        text = if (settings.isEnabled) "Alarm is active" else "Alarm is off",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                    
                    if (settings.isEnabled) {
                        val unlockTime = Calendar.getInstance().apply {
                            set(Calendar.HOUR_OF_DAY, settings.alarmHour)
                            set(Calendar.MINUTE, settings.alarmMinute)
                            add(Calendar.MINUTE, settings.lockoutDurationMinutes)
                        }
                        
                        Text(
                            text = "Apps unlock at ${formatTime(unlockTime.get(Calendar.HOUR_OF_DAY), unlockTime.get(Calendar.MINUTE))}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Enable Alarm",
                    style = MaterialTheme.typography.titleMedium
                )
                Switch(
                    checked = settings.isEnabled,
                    onCheckedChange = { viewModel.setEnabled(it) },
                    enabled = hasAllPermissions
                )
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "📱 ${settings.lockedAppPackages.size} apps will be locked",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = "⏱️ Lockout duration: ${settings.lockoutDurationMinutes} minutes",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 4.dp)
                    )
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

@Composable
fun PermissionItem(
    title: String,
    description: String,
    isGranted: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = !isGranted, onClick = onClick),
        color = if (isGranted) {
            MaterialTheme.colorScheme.surfaceVariant
        } else {
            MaterialTheme.colorScheme.errorContainer
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = if (isGranted) "✓" else "!",
                style = MaterialTheme.typography.headlineSmall,
                color = if (isGranted) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.error
                }
            )
        }
    }
}

@Composable
fun SettingItem(
    title: String,
    value: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun LockoutDurationDialog(
    currentDuration: Int,
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit
) {
    var duration by remember { mutableStateOf(currentDuration.toString()) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Lockout Duration") },
        text = {
            Column {
                Text(
                    text = "Enter duration in minutes:",
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = duration,
                    onValueChange = { 
                        if (it.all { char -> char.isDigit() }) {
                            duration = it
                        }
                    },
                    label = { Text("Minutes") },
                    singleLine = true
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    duration.toIntOrNull()?.let { onConfirm(it) }
                },
                enabled = duration.toIntOrNull() != null && duration.toInt() > 0
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

private fun formatTime(hour: Int, minute: Int): String {
    val calendar = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, hour)
        set(Calendar.MINUTE, minute)
    }
    return SimpleDateFormat("h:mm a", Locale.getDefault()).format(calendar.time)
}