package com.example.pillbestie.data

/**
 * A data class that represents the complete UI state for a single medicine item on the home screen.
 */
data class MedicineUIData(
    val medicine: Medicine,
    val isTakenToday: Boolean
)
