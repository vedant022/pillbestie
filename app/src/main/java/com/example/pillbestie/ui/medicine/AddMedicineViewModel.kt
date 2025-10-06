package com.example.pillbestie.ui.medicine

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pillbestie.data.Medicine
import com.example.pillbestie.data.MedicineRepository
import kotlinx.coroutines.launch

class AddMedicineViewModel(private val medicineRepository: MedicineRepository) : ViewModel() {
    fun addMedicine(medicine: Medicine) {
        viewModelScope.launch {
            medicineRepository.insert(medicine)
        }
    }
}
