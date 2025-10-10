package com.example.pillbestie.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "medicines")
data class Medicine(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val dosage: String,
    val timeInMillis: Long,
    val frequency: String = "Daily",
    val timesPerDay: Int = 1,
    val pillsRemaining: Int? = null,
    val remindBeforeDays: Int? = null
)
