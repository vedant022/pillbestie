package com.example.pillbestie.services

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.pillbestie.data.Injection
import com.example.pillbestie.data.SettingsRepository
import com.example.pillbestie.notifications.NotificationScheduler
import kotlinx.coroutines.flow.first

class MissedDoseAnalysisWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val repository = Injection.provideMedicineRepository(applicationContext)
        val settingsRepository = SettingsRepository(applicationContext)
        val scheduler = NotificationScheduler(applicationContext)
        val reminderFrequency = settingsRepository.reminderFrequency.first()

        return try {
            val medicines = repository.allMedicines.first()
            for (medicine in medicines) {
                val doseLogs = repository.getDoseLogsForMedicine(medicine.id).first()
                val missedDoses = doseLogs.filter { it.wasMissed }

                if (missedDoses.size > 3) { // Simple threshold
                    for (i in 1..reminderFrequency) {
                        val earlyReminderTime = medicine.timeInMillis - (i * 30 * 60 * 1000)
                        scheduler.scheduleNotification(
                            title = "Upcoming Dose: ${medicine.name}",
                            message = "Don't forget to take your ${medicine.dosage} soon!",
                            timeInMillis = earlyReminderTime
                        )
                    }
                }
            }
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }
}
