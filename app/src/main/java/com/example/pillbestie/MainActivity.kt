package com.example.pillbestie

import android.Manifest
import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.pillbestie.ui.MainScreen
import com.example.pillbestie.ui.theme.PillBestieTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            PillBestieTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    PermissionWorkflow()
                }
            }
        }
    }

    @Composable
    private fun PermissionWorkflow() {
        var hasExactAlarmPermission by remember { mutableStateOf(false) }
        var hasNotificationPermission by remember { mutableStateOf(false) }

        // Check initial permission status on launch.
        LaunchedEffect(Unit) {
            hasExactAlarmPermission = checkExactAlarmPermission()
            hasNotificationPermission = checkNotificationPermission()
        }

        val alarmSettingsLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult(),
            onResult = { hasExactAlarmPermission = checkExactAlarmPermission() }
        )
        val notificationPermissionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission(),
            onResult = { isGranted -> hasNotificationPermission = isGranted }
        )

        when {
            // On Android 12+, we need the "Alarms & Reminders" permission.
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !hasExactAlarmPermission -> {
                PermissionRationaleScreen(
                    title = "Critical Permission Required",
                    text = "Pill Bestie needs the \"Alarms & Reminders\" permission to schedule your doses correctly.",
                    buttonText = "Open Settings"
                ) {
                    val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                        data = Uri.fromParts("package", packageName, null)
                    }
                    alarmSettingsLauncher.launch(intent)
                }
            }

            // On Android 13+, we need the "Post Notifications" permission.
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !hasNotificationPermission -> {
                PermissionRationaleScreen(
                    title = "Permission Required",
                    text = "To show reminders, Pill Bestie needs permission to send notifications.",
                    buttonText = "Grant Permission"
                ) {
                    notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }

            // Once all essential permissions are granted, show the main app.
            else -> {
                MainScreen()
            }
        }
    }

    private fun checkExactAlarmPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.canScheduleExactAlarms()
        } else { true }
    }

    private fun checkNotificationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        } else { true }
    }
}

@Composable
private fun PermissionRationaleScreen(
    title: String,
    text: String,
    buttonText: String,
    onButtonClicked: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = title, style = MaterialTheme.typography.headlineSmall, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = text, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onButtonClicked) {
            Text(buttonText)
        }
    }
}
