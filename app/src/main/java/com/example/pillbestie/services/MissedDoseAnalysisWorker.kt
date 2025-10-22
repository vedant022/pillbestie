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
                val recentDose = doseLogs.maxByOrNull { it.scheduledTime }

                if (recentDose != null && recentDose.status != "TAKEN" && System.currentTimeMillis() - recentDose.scheduledTime > 2 * 60 * 60 * 1000) {
                    val missedDoseLog = recentDose.copy(
                        wasMissed = true,
                        status = "MISSED"
                    )
                    repository.updateDoseLog(missedDoseLog)

                    scheduler.scheduleSimpleNotification(
                        title = "Missed Dose: ${medicine.name}", 
                        message = "It's too late to take your dose. It has been logged as missed.",
                        notificationId = medicine.id + 1000
                    )
                }

                val missedDoses = doseLogs.filter { it.wasMissed }
                if (missedDoses.size > 3) {
                    for (i in 1..reminderFrequency) {
                        medicine.times.forEach { time ->
                            val earlyReminderTime = time - (i * 30 * 60 * 1000)
                            val earlyMedicine = medicine.copy(times = listOf(earlyReminderTime), name = "Upcoming Dose: ${medicine.name}", dosage = "Don't forget to take your ${medicine.dosage} soon!")
                            scheduler.schedule(earlyMedicine)
                        }
                    }
                }
            }
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }
}
