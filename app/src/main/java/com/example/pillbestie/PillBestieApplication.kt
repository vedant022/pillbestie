package com.example.pillbestie

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.pillbestie.services.MissedDoseAnalysisWorker
import java.util.concurrent.TimeUnit

class PillBestieApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        setupMissedDoseAnalysisWorker()
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // Create a new, high-priority channel specifically for alarms.
            // A new ID is used to ensure the OS applies the new settings.
            val alarmChannel = NotificationChannel(
                "medication_alarms_v2", // New unique ID
                "Medication Alarms",
                NotificationManager.IMPORTANCE_MAX // The highest priority for immediate, full-screen display.
            ).apply {
                description = "Time-critical medication alarms"
                setSound(
                    RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM),
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                setBypassDnd(true)
                enableVibration(true)
            }

            // Channel for non-urgent reminders
            val reminderChannel = NotificationChannel(
                "medication_reminders",
                "Medication Reminders",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Non-urgent reminders, such as for missed doses."
            }
            
            notificationManager.createNotificationChannel(alarmChannel)
            notificationManager.createNotificationChannel(reminderChannel)
        }
    }

    private fun setupMissedDoseAnalysisWorker() {
        val workRequest = PeriodicWorkRequestBuilder<MissedDoseAnalysisWorker>(
            repeatInterval = 6, // Run every 6 hours
            repeatIntervalTimeUnit = TimeUnit.HOURS
        ).build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "missed-dose-analysis",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }
}
