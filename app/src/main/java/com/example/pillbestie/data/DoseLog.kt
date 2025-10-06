package com.example.pillbestie.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "dose_logs")
data class DoseLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val medicineId: Int,
    val scheduledTime: Long,
    val takenTime: Long? = null,
    val wasMissed: Boolean,
    val imageHash: String? = null
)
