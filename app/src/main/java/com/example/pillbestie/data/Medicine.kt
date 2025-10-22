package com.example.pillbestie.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "medicines")
data class Medicine(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val dosage: String,
    val quantityPerDose: Int = 1,
    val times: List<Long> = emptyList(),
    val frequency: String = "Daily",
    val timesPerDay: Int = 1,
    val pillsRemaining: Int? = null,
    val remindBeforeDays: Int? = null,
    val durationValue: Int? = null,
    val durationUnit: String? = null
) : Parcelable
