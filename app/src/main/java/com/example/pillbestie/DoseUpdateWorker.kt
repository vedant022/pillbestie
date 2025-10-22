package com.example.pillbestie

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.pillbestie.data.DoseLog
import com.example.pillbestie.data.Injection
import com.example.pillbestie.data.Medicine
import com.example.pillbestie.notifications.NotificationScheduler
import com.google.gson.Gson

class DoseUpdateWorker(appContext: Context, workerParams: WorkerParameters) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val medicineJson = inputData.getString(KEY_MEDICINE_JSON) ?: return Result.failure()
        val action = inputData.getString(KEY_ACTION) ?: return Result.failure()
        
        val medicine = Gson().fromJson(medicineJson, Medicine::class.java)
        val repository = Injection.provideMedicineRepository(applicationContext)
        val scheduler = NotificationScheduler(applicationContext)

        when (action) {
            ACTION_TAKE -> {
                val doseLog = DoseLog(
                    medicineId = medicine.id,
                    scheduledTime = medicine.times.first(),
                    status = "TAKEN",
                    wasMissed = false,
                    takenTime = System.currentTimeMillis()
                )
                repository.insert(doseLog)

                medicine.pillsRemaining?.let { currentPills ->
                    val dosageAmount = medicine.dosage.toIntOrNull() ?: 1
                    val newPillCount = currentPills - dosageAmount
                    val updatedMedicine = medicine.copy(pillsRemaining = newPillCount)
                    repository.updateMedicine(updatedMedicine)

                    medicine.remindBeforeDays?.let { remindDays ->
                        if (newPillCount <= (remindDays * medicine.timesPerDay)) {
                            scheduler.scheduleSimpleNotification(
                                title = "Refill Reminder",
                                message = "You are running low on ${medicine.name}. You have approximately $newPillCount pills left.",
                                notificationId = medicine.id + 2000
                            )
                        }
                    }
                }
            }
            ACTION_SNOOZE -> {
                val snoozeTime = System.currentTimeMillis() + 15 * 60 * 1000
                val snoozedMedicine = medicine.copy(times = listOf(snoozeTime))
                scheduler.schedule(snoozedMedicine)
            }
        }

        // Dismiss the original notification
        val notificationId = inputData.getInt(KEY_NOTIFICATION_ID, 0)
        if (notificationId != 0) {
            val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
            notificationManager.cancel(notificationId)
        }

        return Result.success()
    }

    companion object {
        const val KEY_MEDICINE_JSON = "key_medicine_json"
        const val KEY_ACTION = "key_action"
        const val KEY_NOTIFICATION_ID = "key_notification_id"
        const val ACTION_TAKE = "action_take"
        const val ACTION_SNOOZE = "action_snooze"
    }
}
