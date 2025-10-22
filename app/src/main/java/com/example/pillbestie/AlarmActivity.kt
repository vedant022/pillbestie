package com.example.pillbestie

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.pillbestie.data.Medicine
import com.example.pillbestie.notifications.NotificationActionReceiver
import com.google.gson.Gson

class AlarmActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val medicine = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("medicine", Medicine::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra("medicine")
        }

        if (medicine == null) {
            finish()
            return
        }

        setContent {
            AlarmScreen(medicine) { action ->
                val workManager = WorkManager.getInstance(this)
                val medicineJson = Gson().toJson(medicine)

                val workRequest = OneTimeWorkRequestBuilder<DoseUpdateWorker>()
                    .setInputData(Data.Builder()
                        .putString(DoseUpdateWorker.KEY_MEDICINE_JSON, medicineJson)
                        .putString(DoseUpdateWorker.KEY_ACTION, action)
                        .build())
                    .build()

                workManager.enqueue(workRequest)
                finish()
            }
        }
    }
}

@Composable
fun AlarmScreen(medicine: Medicine, onAction: (String) -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Time for your medication!", style = MaterialTheme.typography.headlineLarge)
        Spacer(Modifier.height(16.dp))
        Text("It's time to take ${medicine.name}", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(32.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Button(onClick = { onAction(NotificationActionReceiver.ACTION_TAKE) }) {
                Text("Take")
            }
            Button(onClick = { onAction(NotificationActionReceiver.ACTION_SNOOZE) }) {
                Text("Snooze")
            }
        }
    }
}
