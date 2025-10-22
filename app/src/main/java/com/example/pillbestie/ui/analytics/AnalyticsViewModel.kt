package com.example.pillbestie.ui.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pillbestie.data.DoseLog
import com.example.pillbestie.data.MedicineRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.util.Calendar
import java.util.concurrent.TimeUnit

class AnalyticsViewModel(medicineRepository: MedicineRepository) : ViewModel() {

    val analyticsData: StateFlow<AnalyticsData> = medicineRepository.allDoseLogs
        .map { doseLogs ->
            // Use logs from the last 30 days for relevance
            val thirtyDaysAgo = Calendar.getInstance().apply {
                add(Calendar.DAY_OF_YEAR, -30)
            }.timeInMillis

            val recentDoseLogs = doseLogs.filter { it.scheduledTime >= thirtyDaysAgo }

            val takenCount = recentDoseLogs.count { it.status == "TAKEN" }
            val skippedCount = recentDoseLogs.count { it.status == "SKIPPED" }
            val missedCount = recentDoseLogs.count { it.status == "MISSED" }

            val totalLoggedDoses = takenCount + skippedCount + missedCount
            val adherence = if (totalLoggedDoses > 0) {
                (takenCount.toFloat() / totalLoggedDoses) * 100
            } else {
                0f
            }

            AnalyticsData(
                adherence = adherence,
                streak = calculateStreak(doseLogs),
                takenCount = takenCount,
                skippedCount = skippedCount,
                missedCount = missedCount
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AnalyticsData()
        )

    private fun calculateStreak(doseLogs: List<DoseLog>): Int {
        if (doseLogs.isEmpty()) return 0

        // Get unique days where at least one dose was taken
        val takenDays = doseLogs
            .filter { it.status == "TAKEN" }
            .map { log ->
                Calendar.getInstance().apply { timeInMillis = log.scheduledTime }.apply {
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }.timeInMillis
            }
            .toSet()

        if (takenDays.isEmpty()) return 0

        var streak = 0
        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        var currentDay = today.clone() as Calendar

        // If nothing was taken today, streak must start from yesterday
        if (!takenDays.contains(currentDay.timeInMillis)) {
            currentDay.add(Calendar.DAY_OF_YEAR, -1)
        }

        // Now, count backwards for consecutive days
        while (takenDays.contains(currentDay.timeInMillis)) {
            streak++
            currentDay.add(Calendar.DAY_OF_YEAR, -1)
        }

        return streak
    }
}

data class AnalyticsData(
    val adherence: Float = 0f,
    val streak: Int = 0,
    val takenCount: Int = 0,
    val skippedCount: Int = 0,
    val missedCount: Int = 0
)
