package com.example.pillbestie.data

import android.content.Context
import com.example.pillbestie.notifications.NotificationScheduler

object Injection {
    fun provideMedicineRepository(context: Context): MedicineRepository {
        val database = AppDatabase.getDatabase(context)
        return MedicineRepository(database.medicineDao(), database.doseLogDao())
    }

    fun provideSettingsRepository(context: Context): SettingsRepository {
        return SettingsRepository(context)
    }

    fun provideNotificationScheduler(context: Context): NotificationScheduler {
        return NotificationScheduler(context)
    }
}
