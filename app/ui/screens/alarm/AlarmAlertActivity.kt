package com.example.morningfocusalarm.ui.screens.alarm

import android.media.RingtoneManager
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
import java.text.SimpleDateFormat
import java.util.*

class AlarmAlertActivity : ComponentActivity() {
    
    private var ringtone: android.media.Ringtone? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Play alarm sound
        try {
            val alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            ringtone = RingtoneManager.getRingtone(this, alarmUri)
            ringtone?.play()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        
        setContent {
            MorningFocusAlarmTheme {
                AlarmAlertContent(
                    onDismiss = { 
                        stopRingtone()
                        finish()
                    }
                )
            }
        }
    }
    
    private fun stopRingtone() {
        ringtone?.stop()
        ringtone = null
    }
    
    override fun onDestroy() {
        stopRingtone()
        super.onDestroy()
    }
}

@Composable
fun AlarmAlertContent(onDismiss: () -> Unit) {
    val currentTime = remember {
        SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date())
    }
    
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
                text = "🌅",
                style = MaterialTheme.typography.displayLarge,
                modifier = Modifier.padding(bottom = 24.dp)
            )
            
            Text(
                text = currentTime,
                style = MaterialTheme.typography.displayLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            Text(
                text = "Good Morning!",
                style = MaterialTheme.typography.headlineLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 32.dp)
            )
            
            Text(
                text = "Your focus lockout period will begin when you dismiss this alarm.",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 48.dp)
            )
            
            Button(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text(
                    text = "Dismiss Alarm",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}
