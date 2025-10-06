package com.example.pillbestie.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pillbestie.data.DoseLog
import com.example.pillbestie.data.Medicine
import com.example.pillbestie.data.MedicineRepository
import com.example.pillbestie.data.Personality
import com.example.pillbestie.data.SettingsRepository
import com.example.pillbestie.data.TakenAction
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(private val medicineRepository: MedicineRepository, private val settingsRepository: SettingsRepository) : ViewModel() {

    val medicines: StateFlow<List<Medicine>> = medicineRepository.allMedicines.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val takenAction: StateFlow<TakenAction> = settingsRepository.takenAction.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TakenAction.QUICK_TAP
    )

    val personality: StateFlow<Personality> = settingsRepository.personality.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = Personality.CARING
    )

    val profileName: StateFlow<String> = settingsRepository.profileName.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ""
    )

    fun markDoseAsTaken(medicine: Medicine) {
        viewModelScope.launch {
            val doseLog = DoseLog(
                medicineId = medicine.id,
                scheduledTime = medicine.timeInMillis,
                takenTime = System.currentTimeMillis(),
                wasMissed = false
            )
            medicineRepository.insert(doseLog)
        }
    }
}
