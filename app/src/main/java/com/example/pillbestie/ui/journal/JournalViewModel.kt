package com.example.pillbestie.ui.journal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pillbestie.data.JournalEntry
import com.example.pillbestie.data.MedicineRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class JournalViewModel(private val medicineRepository: MedicineRepository) : ViewModel() {

    val journalEntries: StateFlow<List<JournalEntry>> = medicineRepository.allJournalEntries.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )
}
