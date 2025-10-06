package com.example.pillbestie.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pillbestie.data.Personality
import com.example.pillbestie.data.SettingsRepository
import com.example.pillbestie.data.TakenAction
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(private val settingsRepository: SettingsRepository) : ViewModel() {

    val takenAction: StateFlow<TakenAction> = settingsRepository.takenAction.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TakenAction.QUICK_TAP
    )

    fun setTakenAction(takenAction: TakenAction) {
        viewModelScope.launch { settingsRepository.setTakenAction(takenAction) }
    }

    val personality: StateFlow<Personality> = settingsRepository.personality.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = Personality.CARING
    )

    fun setPersonality(personality: Personality) {
        viewModelScope.launch { settingsRepository.setPersonality(personality) }
    }

    val profileName: StateFlow<String> = settingsRepository.profileName.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ""
    )

    fun setProfileName(name: String) {
        viewModelScope.launch { settingsRepository.setProfileName(name) }
    }

    val reminderFrequency: StateFlow<Int> = settingsRepository.reminderFrequency.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 1
    )

    fun setReminderFrequency(frequency: Int) {
        viewModelScope.launch { settingsRepository.setReminderFrequency(frequency) }
    }

    val vibrationEnabled: StateFlow<Boolean> = settingsRepository.vibrationEnabled.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = true
    )

    fun setVibrationEnabled(enabled: Boolean) {
        viewModelScope.launch { settingsRepository.setVibrationEnabled(enabled) }
    }
}
