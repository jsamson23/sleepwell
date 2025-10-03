package com.example.morningfocusalarm.ui.screens.lockscreen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.morningfocusalarm.ui.theme.MorningFocusAlarmTheme
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

class LockScreenActivity : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val unlockTime = intent.getLongExtra(EXTRA_UNLOCK_TIME, 0L)
        
        setContent {
            MorningFocusAlarmTheme {
                LockScreenContent(
                    unlockTime = unlockTime,
                    onTimeout = { finish() }
                )
            }
        }
    }
    
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        // Prevent back button from dismissing
    }
    
    companion object {
        const val EXTRA_UNLOCK_TIME = "unlock_time"
    }
}

@Composable
fun LockScreenContent(
    unlockTime: Long,
    onTimeout: () -> Unit
) {
    var currentTime by remember { mutableStateOf(System.currentTimeMillis()) }
    
    LaunchedEffect(Unit) {
        while (currentTime < unlockTime) {
            delay(1000)
            currentTime = System.currentTimeMillis()
        }
        onTimeout()
    }
    
    val remainingMillis = (unlockTime - currentTime).coerceAtLeast(0)
    val remainingMinutes = (remainingMillis / 60000).toInt()
    val remainingSeconds = ((remainingMillis % 60000) / 1000).toInt()
    
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "🔒",
                style = MaterialTheme.typography.displayLarge,
                modifier = Modifier.padding(bottom = 24.dp)
            )
            
            Text(
                text = "App Locked",
                style = MaterialTheme.typography.headlineLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            Text(
                text = "This app is locked by Morning Focus Alarm",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 32.dp)
            )
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Time Remaining",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    Text(
                        text = String.format("%02d:%02d", remainingMinutes, remainingSeconds),
                        style = MaterialTheme.typography.displayMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    Divider(modifier = Modifier.padding(vertical = 16.dp))
                    
                    Text(
                        text = "Unlocks at",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Text(
                        text = formatDateTime(unlockTime),
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Text(
                text = "Stay focused on your morning routine! 🌅",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun formatDateTime(timeMillis: Long): String {
    val formatter = SimpleDateFormat("MMM dd, h:mm a", Locale.getDefault())
    return formatter.format(Date(timeMillis))
}