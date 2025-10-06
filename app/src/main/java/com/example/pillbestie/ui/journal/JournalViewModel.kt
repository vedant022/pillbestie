package com.example.pillbestie.ui.journal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pillbestie.data.MedicineRepository
import com.example.pillbestie.data.MoodEntry
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class JournalViewModel(private val medicineRepository: MedicineRepository) : ViewModel() {

    val moodEntries: StateFlow<List<MoodEntry>> = medicineRepository.allMoodEntries.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun addMoodEntry(mood: String) {
        viewModelScope.launch {
            medicineRepository.insert(MoodEntry(mood = mood, timestamp = System.currentTimeMillis()))
        }
    }
}
