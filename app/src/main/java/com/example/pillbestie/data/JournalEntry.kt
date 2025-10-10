package com.example.pillbestie.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "journal_entries")
data class JournalEntry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val text: String,
    val mood: String, // Storing emoji as a String
    val imageUri: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)
