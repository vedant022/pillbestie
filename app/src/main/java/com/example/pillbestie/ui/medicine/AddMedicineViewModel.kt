package com.example.pillbestie.ui.medicine

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pillbestie.data.Medicine
import com.example.pillbestie.data.MedicineRepository
import com.example.pillbestie.data.SettingsRepository
import com.example.pillbestie.notifications.NotificationScheduler
import com.example.pillbestie.services.DrugInteractionService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// 1. Define a clear UI State for the screen
sealed interface AddMedicineUiState {
    object Idle : AddMedicineUiState
    object Saving : AddMedicineUiState
    object Success : AddMedicineUiState
    data class ShowInteractionWarning(val interactions: List<String>) : AddMedicineUiState
}

class AddMedicineViewModel(
    private val medicineRepository: MedicineRepository,
    private val settingsRepository: SettingsRepository,
    private val notificationScheduler: NotificationScheduler
) : ViewModel() {

    private val interactionService = DrugInteractionService()

    private val _uiState = MutableStateFlow<AddMedicineUiState>(AddMedicineUiState.Idle)
    val uiState: StateFlow<AddMedicineUiState> = _uiState.asStateFlow()

    // 2. A single, robust function to handle the entire save process
    fun saveMedicine(medicine: Medicine) {
        viewModelScope.launch {
            _uiState.value = AddMedicineUiState.Saving

            // Check for interactions if the setting is enabled
            if (settingsRepository.drugInteractionCheckEnabled.first()) {
                val interactions = withContext(Dispatchers.IO) {
                    val existingMedicines = medicineRepository.allMedicines.first().map { it.name }
                    val allMedicines = existingMedicines + medicine.name
                    interactionService.getInteractions(allMedicines)
                }

                if (interactions.isNotEmpty()) {
                    _uiState.value = AddMedicineUiState.ShowInteractionWarning(interactions)
                    return@launch // Stop here and wait for user input from the dialog
                }
            }

            // If no interactions found or the check is disabled, proceed to save
            addMedicineAndFinish(medicine)
        }
    }

    // 3. A public function to be called if the user proceeds from the warning dialog
    fun addMedicineAndFinish(medicine: Medicine) {
        viewModelScope.launch {
            medicineRepository.insert(medicine)
            notificationScheduler.schedule(medicine)
            _uiState.value = AddMedicineUiState.Success
        }
    }

    // 4. A function to reset the state when the UI is done (e.g., dialog dismissed)
    fun onDialogDismissed() {
        _uiState.value = AddMedicineUiState.Idle
    }
}