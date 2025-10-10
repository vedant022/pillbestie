package com.example.pillbestie.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.example.pillbestie.data.Injection
import com.example.pillbestie.notifications.NotificationScheduler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MissedDoseAnalysisService : Service() {

    private val repository by lazy { Injection.provideMedicineRepository(this) }
    private val scheduler by lazy { NotificationScheduler(this) }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        analyzeMissedDoses()
        return START_NOT_STICKY
    }

    private fun analyzeMissedDoses() {
        CoroutineScope(Dispatchers.IO).launch {
            val medicines = repository.allMedicines.first()
            for (medicine in medicines) {
                val doseLogs = repository.getDoseLogsForMedicine(medicine.id).first()
                val missedDoses = doseLogs.filter { it.wasMissed }

                if (missedDoses.size > 3) { // Simple threshold
                    val earlyReminderTime = medicine.timeInMillis - (30 * 60 * 1000)
                    scheduler.scheduleNotification(
                        title = "Upcoming Dose: ${medicine.name}",
                        message = "Don't forget to take your ${medicine.dosage} soon!",
                        timeInMillis = earlyReminderTime
                    )
                }
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
