package com.example.pillbestie.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.catch
import java.io.IOException

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsRepository(private val context: Context) {

    private object PreferencesKeys {
        val TAKEN_ACTION = stringPreferencesKey("taken_action")
        val PERSONALITY = stringPreferencesKey("personality")
        val PROFILE_NAME = stringPreferencesKey("profile_name")
        val REMINDER_FREQUENCY = intPreferencesKey("reminder_frequency")
        val VIBRATION_ENABLED = booleanPreferencesKey("vibration_enabled")
        val DRUG_INTERACTION_CHECK_ENABLED = booleanPreferencesKey("drug_interaction_check_enabled")
    }

    val takenAction: Flow<TakenAction> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map {
        try {
            TakenAction.valueOf(it[PreferencesKeys.TAKEN_ACTION] ?: TakenAction.QUICK_TAP.name)
        } catch (e: IllegalArgumentException) {
            TakenAction.QUICK_TAP
        }
    }

    suspend fun setTakenAction(takenAction: TakenAction) {
        context.dataStore.edit { it[PreferencesKeys.TAKEN_ACTION] = takenAction.name }
    }

    val personality: Flow<Personality> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map {
        try {
            Personality.valueOf(it[PreferencesKeys.PERSONALITY] ?: Personality.CARING.name)
        } catch (e: IllegalArgumentException) {
            Personality.CARING
        }
    }

    suspend fun setPersonality(personality: Personality) {
        context.dataStore.edit { it[PreferencesKeys.PERSONALITY] = personality.name }
    }

    val profileName: Flow<String> = context.dataStore.data.map {
        it[PreferencesKeys.PROFILE_NAME] ?: ""
    }

    suspend fun setProfileName(name: String) {
        context.dataStore.edit { it[PreferencesKeys.PROFILE_NAME] = name }
    }

    val reminderFrequency: Flow<Int> = context.dataStore.data.map {
        it[PreferencesKeys.REMINDER_FREQUENCY] ?: 1
    }

    suspend fun setReminderFrequency(frequency: Int) {
        context.dataStore.edit { it[PreferencesKeys.REMINDER_FREQUENCY] = frequency }
    }

    val vibrationEnabled: Flow<Boolean> = context.dataStore.data.map {
        it[PreferencesKeys.VIBRATION_ENABLED] ?: true
    }

    suspend fun setVibrationEnabled(enabled: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.VIBRATION_ENABLED] = enabled }
    }

    val drugInteractionCheckEnabled: Flow<Boolean> = context.dataStore.data.map {
        it[PreferencesKeys.DRUG_INTERACTION_CHECK_ENABLED] ?: true
    }

    suspend fun setDrugInteractionCheckEnabled(enabled: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.DRUG_INTERACTION_CHECK_ENABLED] = enabled }
    }
}
