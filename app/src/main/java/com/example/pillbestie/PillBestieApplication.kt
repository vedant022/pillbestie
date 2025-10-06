package com.example.pillbestie

import android.app.Application
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.pillbestie.services.MissedDoseAnalysisWorker
import java.util.concurrent.TimeUnit

class PillBestieApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        setupMissedDoseAnalysisWorker()
    }

    private fun setupMissedDoseAnalysisWorker() {
        val workRequest = PeriodicWorkRequestBuilder<MissedDoseAnalysisWorker>(
            repeatInterval = 6, // Run every 6 hours
            repeatIntervalTimeUnit = TimeUnit.HOURS
        ).build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "missed-dose-analysis",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }
}
