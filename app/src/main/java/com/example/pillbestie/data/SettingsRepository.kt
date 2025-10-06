package com.example.pillbestie.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsRepository(private val context: Context) {

    private val takenActionKey = stringPreferencesKey("taken_action")
    private val personalityKey = stringPreferencesKey("personality")
    private val profileNameKey = stringPreferencesKey("profile_name")
    private val reminderFrequencyKey = intPreferencesKey("reminder_frequency")
    private val vibrationEnabledKey = booleanPreferencesKey("vibration_enabled")

    val takenAction: Flow<TakenAction> = context.dataStore.data.map {
        TakenAction.valueOf(it[takenActionKey] ?: TakenAction.QUICK_TAP.name)
    }

    suspend fun setTakenAction(takenAction: TakenAction) {
        context.dataStore.edit { it[takenActionKey] = takenAction.name }
    }

    val personality: Flow<Personality> = context.dataStore.data.map {
        Personality.valueOf(it[personalityKey] ?: Personality.CARING.name)
    }

    suspend fun setPersonality(personality: Personality) {
        context.dataStore.edit { it[personalityKey] = personality.name }
    }

    val profileName: Flow<String> = context.dataStore.data.map {
        it[profileNameKey] ?: ""
    }

    suspend fun setProfileName(name: String) {
        context.dataStore.edit { it[profileNameKey] = name }
    }

    val reminderFrequency: Flow<Int> = context.dataStore.data.map {
        it[reminderFrequencyKey] ?: 1
    }

    suspend fun setReminderFrequency(frequency: Int) {
        context.dataStore.edit { it[reminderFrequencyKey] = frequency }
    }

    val vibrationEnabled: Flow<Boolean> = context.dataStore.data.map {
        it[vibrationEnabledKey] ?: true
    }

    suspend fun setVibrationEnabled(enabled: Boolean) {
        context.dataStore.edit { it[vibrationEnabledKey] = enabled }
    }
}

enum class TakenAction {
    QUICK_TAP,
    PHOTO_MODE
}

enum class Personality {
    CARING,
    SARCASTIC,
    CHAOTIC
}
