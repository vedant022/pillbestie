package com.example.pillbestie.ui.journal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pillbestie.data.JournalEntry
import com.example.pillbestie.data.MedicineRepository
import kotlinx.coroutines.launch

class AddEditJournalViewModel(private val repository: MedicineRepository) : ViewModel() {

    fun saveJournalEntry(text: String, mood: String, imageUri: String?) {
        viewModelScope.launch {
            val entry = JournalEntry(text = text, mood = mood, imageUri = imageUri)
            repository.insert(entry)
        }
    }
}
