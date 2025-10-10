package com.example.pillbestie.ui.medicine

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pillbestie.data.Medicine
import com.example.pillbestie.data.MedicineRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class EditMedicineViewModel(private val medicineRepository: MedicineRepository) : ViewModel() {

    fun getMedicine(medicineId: Int): Flow<Medicine> {
        return medicineRepository.getMedicine(medicineId)
    }

    fun updateMedicine(medicine: Medicine, onComplete: () -> Unit) {
        viewModelScope.launch {
            medicineRepository.updateMedicine(medicine)
            onComplete()
        }
    }
}