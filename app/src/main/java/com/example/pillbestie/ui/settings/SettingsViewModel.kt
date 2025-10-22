package com.example.pillbestie.ui.settings

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pillbestie.data.MedicineRepository
import com.example.pillbestie.data.SettingsRepository
import com.example.pillbestie.data.TakenAction
import com.example.pillbestie.data.Personality
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
    private val medicineRepository: MedicineRepository
) : ViewModel() {

    val takenAction: Flow<TakenAction> = settingsRepository.takenAction
    val personality: Flow<Personality> = settingsRepository.personality
    val profileName: Flow<String> = settingsRepository.profileName
    val reminderFrequency: Flow<Int> = settingsRepository.reminderFrequency
    val vibrationEnabled: Flow<Boolean> = settingsRepository.vibrationEnabled
    val drugInteractionCheckEnabled: Flow<Boolean> = settingsRepository.drugInteractionCheckEnabled

    fun setTakenAction(action: TakenAction) {
        viewModelScope.launch {
            settingsRepository.setTakenAction(action)
        }
    }

    fun setPersonality(personality: Personality) {
        viewModelScope.launch {
            settingsRepository.setPersonality(personality)
        }
    }

    fun setProfileName(name: String) {
        viewModelScope.launch {
            settingsRepository.setProfileName(name)
        }
    }

    fun setReminderFrequency(frequency: Int) {
        viewModelScope.launch {
            settingsRepository.setReminderFrequency(frequency)
        }
    }

    fun setVibrationEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setVibrationEnabled(enabled)
        }
    }

    fun setDrugInteractionCheckEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setDrugInteractionCheckEnabled(enabled)
        }
    }

    fun backupData(uri: Uri, context: Context) {
        // To be implemented
    }

    fun restoreData(uri: Uri, context: Context) {
        // To be implemented
    }

    fun exportMedsToCsv(uri: Uri, context: Context) {
        // To be implemented
    }

    fun clearAllData() {
        viewModelScope.launch {
            // This will be implemented in a future update
        }
    }
}
