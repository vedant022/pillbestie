package com.example.pillbestie.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.pillbestie.data.AppDatabase
import com.example.pillbestie.data.MedicineRepository
import com.example.pillbestie.data.SettingsRepository
import com.example.pillbestie.notifications.NotificationScheduler
import com.example.pillbestie.ui.home.HomeViewModel
import com.example.pillbestie.ui.journal.AddEditJournalViewModel
import com.example.pillbestie.ui.journal.JournalViewModel
import com.example.pillbestie.ui.medicine.AddMedicineViewModel
import com.example.pillbestie.ui.medicine.MedicineDetailViewModel
import com.example.pillbestie.ui.scan.ScanPillViewModel
import com.example.pillbestie.ui.settings.SettingsViewModel
import com.example.pillbestie.ui.voice.VoiceChatViewModel

class ViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val application = context.applicationContext as Application
        
        val database = AppDatabase.getDatabase(context)
        val medicineRepository = MedicineRepository(database.medicineDao(), database.doseLogDao())
        val settingsRepository = SettingsRepository(context)
        val notificationScheduler = NotificationScheduler(context)

        val viewModel = when {
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel(medicineRepository, settingsRepository, notificationScheduler, application)
            }
            modelClass.isAssignableFrom(AddMedicineViewModel::class.java) -> {
                AddMedicineViewModel(medicineRepository, settingsRepository, notificationScheduler)
            }
            modelClass.isAssignableFrom(MedicineDetailViewModel::class.java) -> {
                MedicineDetailViewModel(medicineRepository)
            }
            modelClass.isAssignableFrom(ScanPillViewModel::class.java) -> {
                ScanPillViewModel(application)
            }
            modelClass.isAssignableFrom(SettingsViewModel::class.java) -> {
                SettingsViewModel(settingsRepository, medicineRepository)
            }
            modelClass.isAssignableFrom(JournalViewModel::class.java) -> {
                JournalViewModel(medicineRepository)
            }
            modelClass.isAssignableFrom(AddEditJournalViewModel::class.java) -> {
                AddEditJournalViewModel(medicineRepository)
            }
            modelClass.isAssignableFrom(VoiceChatViewModel::class.java) -> {
                VoiceChatViewModel(application, medicineRepository, settingsRepository, notificationScheduler)
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }

        return viewModel as T
    }
}
