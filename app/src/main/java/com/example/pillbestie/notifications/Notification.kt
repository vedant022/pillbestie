package com.example.pillbestie.notifications

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.pillbestie.data.AppDatabase
import com.example.pillbestie.data.DoseLog
import kotlinx.coroutines.runBlocking

class Notification : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val medicineName = intent.getStringExtra("title") ?: "Medication Due"
        val dosage = intent.getStringExtra("message") ?: ""
        val medicineId = intent.getIntExtra("medicineId", -1)

        val snoozeIntent = Intent(context, NotificationActionReceiver::class.java).apply {
            action = "SNOOZE"
            putExtra("medicineId", medicineId)
            putExtra("title", medicineName)
            putExtra("message", dosage)
        }
        val snoozePendingIntent = PendingIntent.getBroadcast(context, medicineId, snoozeIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val skipIntent = Intent(context, NotificationActionReceiver::class.java).apply {
            action = "SKIP"
            putExtra("medicineId", medicineId)
        }
        val skipPendingIntent = PendingIntent.getBroadcast(context, medicineId + 1, skipIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val notification = NotificationCompat.Builder(context, "notification")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(medicineName)
            .setContentText(dosage)
            .addAction(0, "Snooze for 15 mins", snoozePendingIntent)
            .addAction(0, "Skip this dose", skipPendingIntent)
            .build()
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(medicineId, notification)
    }
}

class NotificationActionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val medicineId = intent.getIntExtra("medicineId", -1)
        val action = intent.action

        val database = AppDatabase.getDatabase(context)
        val doseLogDao = database.doseLogDao()

        when (action) {
            "SNOOZE" -> {
                val scheduler = NotificationScheduler(context)
                val snoozeTime = System.currentTimeMillis() + 15 * 60 * 1000 // 15 minutes
                scheduler.scheduleNotification(intent.getStringExtra("title")!!, intent.getStringExtra("message")!!, snoozeTime)

                runBlocking {
                    val doseLog = DoseLog(medicineId = medicineId, scheduledTime = System.currentTimeMillis(), status = "SNOOZED", wasMissed = false)
                    doseLogDao.insert(doseLog)
                }
            }
            "SKIP" -> {
                runBlocking {
                    val doseLog = DoseLog(medicineId = medicineId, scheduledTime = System.currentTimeMillis(), status = "SKIPPED", wasMissed = true)
                    doseLogDao.insert(doseLog)
                }
            }
        }

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.cancel(medicineId)
    }
}
