package com.example.pillbestie.ui.settings

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pillbestie.data.AppDatabase
import com.example.pillbestie.data.Personality
import com.example.pillbestie.data.SettingsRepository
import com.example.pillbestie.data.TakenAction
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
    private val db: AppDatabase
) : ViewModel() {

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

    val drugInteractionCheckEnabled: StateFlow<Boolean> = settingsRepository.drugInteractionCheckEnabled.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = true
    )

    fun setDrugInteractionCheckEnabled(enabled: Boolean) {
        viewModelScope.launch { settingsRepository.setDrugInteractionCheckEnabled(enabled) }
    }

    fun backupData(context: Context, uri: Uri) {
        viewModelScope.launch {
            try {
                val dbFile = context.getDatabasePath("pill_bestie_database")
                context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                    FileInputStream(dbFile).use { inputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }
            } catch (e: IOException) {
                // Handle exception
            }
        }
    }

    fun restoreData(context: Context, uri: Uri) {
        viewModelScope.launch {
            try {
                val dbFile = context.getDatabasePath("pill_bestie_database")
                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    FileOutputStream(dbFile).use { outputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }
            } catch (e: IOException) {
                // Handle exception
            }
        }
    }

    fun exportDataToCsv(context: Context, uri: Uri) {
        viewModelScope.launch {
            try {
                val doseLogs = db.doseLogDao().getAllDoseLogs().first()
                val journalEntries = db.journalEntryDao().getAllEntries().first()

                context.contentResolver.openOutputStream(uri)?.bufferedWriter().use { writer ->
                    writer?.write("Type,Date,Details\n")
                    doseLogs.forEach {
                        writer?.write("Dose,${it.scheduledTime},Medicine ID: ${it.medicineId} - Status: ${it.status}\n")
                    }
                    journalEntries.forEach {
                        writer?.write("Journal,${it.timestamp},${it.text}\n")
                    }
                }
            } catch (e: IOException) {
                // Handle exception
            }
        }
    }
}