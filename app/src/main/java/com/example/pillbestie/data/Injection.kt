package com.example.pillbestie.data

import android.content.Context

object Injection {
    fun provideMedicineRepository(context: Context): MedicineRepository {
        val database = AppDatabase.getDatabase(context)
        return MedicineRepository(database.medicineDao(), database.doseLogDao(), database.journalEntryDao())
    }
}
