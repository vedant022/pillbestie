package com.example.pillbestie.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "dose_logs")
data class DoseLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val medicineId: Int,
    val scheduledTime: Long,
    val takenTime: Long,
    val wasMissed: Boolean,
    val status: String? = null, // e.g., "snoozed", "taken"
    val notes: String? = null,
    val mood: String? = null,
    val photoUri: String? = null,
    val imageHash: String? = null
)
