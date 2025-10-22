package com.example.pillbestie.ui.medicine

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pillbestie.data.Medicine
import com.example.pillbestie.data.MedicineRepository
import com.example.pillbestie.notifications.NotificationScheduler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class EditMedicineViewModel(
    private val medicineRepository: MedicineRepository,
    private val notificationScheduler: NotificationScheduler
) : ViewModel() {

    fun getMedicine(medicineId: Int): Flow<Medicine> {
        return medicineRepository.getMedicine(medicineId)
    }

    fun updateMedicine(updatedMedicine: Medicine, onComplete: () -> Unit) {
        viewModelScope.launch {
            // First, get the original medicine state from the database
            val originalMedicine = medicineRepository.getMedicine(updatedMedicine.id).first()

            // Cancel all alarms associated with the original schedule
            notificationScheduler.cancel(originalMedicine)

            // Update the medicine in the database with the new schedule
            medicineRepository.updateMedicine(updatedMedicine)

            // Schedule all new alarms with the updated schedule
            notificationScheduler.schedule(updatedMedicine)
            
            onComplete()
        }
    }
}
