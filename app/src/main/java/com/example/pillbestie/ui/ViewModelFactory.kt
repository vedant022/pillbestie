package com.example.pillbestie.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.pillbestie.data.AppDatabase
import com.example.pillbestie.data.Injection
import com.example.pillbestie.data.SettingsRepository
import com.example.pillbestie.ui.analytics.AnalyticsViewModel
import com.example.pillbestie.ui.home.HomeViewModel
import com.example.pillbestie.ui.journal.AddEditJournalViewModel
import com.example.pillbestie.ui.journal.JournalViewModel
import com.example.pillbestie.ui.medicine.AddMedicineViewModel
import com.example.pillbestie.ui.scan.ScanPillViewModel
import com.example.pillbestie.ui.settings.SettingsViewModel
import com.example.pillbestie.ui.voice.VoiceChatViewModel

class ViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddMedicineViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AddMedicineViewModel(Injection.provideMedicineRepository(context), SettingsRepository(context)) as T
        }
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(Injection.provideMedicineRepository(context), SettingsRepository(context), context.applicationContext as Application) as T
        }
        if (modelClass.isAssignableFrom(AnalyticsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AnalyticsViewModel(Injection.provideMedicineRepository(context)) as T
        }
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SettingsViewModel(SettingsRepository(context), AppDatabase.getDatabase(context)) as T
        }
        if (modelClass.isAssignableFrom(JournalViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return JournalViewModel(Injection.provideMedicineRepository(context)) as T
        }
        if (modelClass.isAssignableFrom(VoiceChatViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return VoiceChatViewModel(context.applicationContext as Application) as T
        }
        if (modelClass.isAssignableFrom(ScanPillViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ScanPillViewModel(context.applicationContext as Application) as T
        }
        if (modelClass.isAssignableFrom(AddEditJournalViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AddEditJournalViewModel(Injection.provideMedicineRepository(context)) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
