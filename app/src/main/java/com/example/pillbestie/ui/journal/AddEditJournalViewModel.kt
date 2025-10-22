package com.example.pillbestie.ui.journal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pillbestie.data.DoseLog
import com.example.pillbestie.data.MedicineRepository
import kotlinx.coroutines.launch

class AddEditJournalViewModel(private val medicineRepository: MedicineRepository) : ViewModel() {
    fun saveDoseLog(doseLog: DoseLog) {
        viewModelScope.launch {
            medicineRepository.insert(doseLog)
        }
    }
}
