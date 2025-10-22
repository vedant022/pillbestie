package com.example.pillbestie

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pillbestie.data.DoseLog
import com.example.pillbestie.data.Medicine
import com.example.pillbestie.data.MedicineRepository
import com.example.pillbestie.notifications.NotificationScheduler
import kotlinx.coroutines.launch

class AlarmViewModel(
    private val medicineRepository: MedicineRepository,
    private val notificationScheduler: NotificationScheduler
) : ViewModel() {

    fun onDoseTaken(medicine: Medicine) {
        viewModelScope.launch {
            val doseLog = DoseLog(
                medicineId = medicine.id,
                scheduledTime = medicine.times.first(),
                status = "TAKEN",
                wasMissed = false,
                takenTime = System.currentTimeMillis()
            )
            medicineRepository.insert(doseLog)
        }
    }

    fun onDoseSnoozed(medicine: Medicine) {
        viewModelScope.launch {
            // Log the snooze action
            val doseLog = DoseLog(
                medicineId = medicine.id,
                scheduledTime = medicine.times.first(),
                status = "SNOOZED",
                wasMissed = false,
                takenTime = System.currentTimeMillis() // This was missing
            )
            medicineRepository.insert(doseLog)

            // Schedule a new alarm for 15 minutes later
            val snoozeTime = System.currentTimeMillis() + 15 * 60 * 1000
            val snoozedMedicine = medicine.copy(times = listOf(snoozeTime))
            notificationScheduler.schedule(snoozedMedicine)
        }
    }

    fun onDoseSkipped(medicine: Medicine) {
        viewModelScope.launch {
            val doseLog = DoseLog(
                medicineId = medicine.id,
                scheduledTime = medicine.times.first(),
                status = "SKIPPED",
                wasMissed = true,
                takenTime = 0 // This was missing
            )
            medicineRepository.insert(doseLog)
        }
    }
}
