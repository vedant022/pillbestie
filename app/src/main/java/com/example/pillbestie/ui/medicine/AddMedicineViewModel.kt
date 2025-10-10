package com.example.pillbestie.ui.medicine

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pillbestie.data.Medicine
import com.example.pillbestie.data.MedicineRepository
import com.example.pillbestie.data.SettingsRepository
import com.example.pillbestie.services.DrugInteractionService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddMedicineViewModel(private val medicineRepository: MedicineRepository, private val settingsRepository: SettingsRepository) : ViewModel() {

    private val interactionService = DrugInteractionService()

    fun addMedicine(medicine: Medicine, onComplete: () -> Unit) {
        viewModelScope.launch {
            medicineRepository.insert(medicine)
            onComplete()
        }
    }

    suspend fun checkInteractions(newMedicineName: String): List<String> {
        if (!settingsRepository.drugInteractionCheckEnabled.first()) {
            return emptyList()
        }
        val existingMedicines = medicineRepository.allMedicines.first().map { it.name }
        val allMedicines = existingMedicines + newMedicineName
        return withContext(Dispatchers.IO) {
            interactionService.getInteractions(allMedicines)
        }
    }
}
