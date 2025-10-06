package com.example.pillbestie.ui.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pillbestie.data.DoseLog
import com.example.pillbestie.data.Medicine
import com.example.pillbestie.data.MedicineRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class AnalyticsViewModel(private val medicineRepository: MedicineRepository) : ViewModel() {

    val analyticsData: StateFlow<AnalyticsData> = combine(
        medicineRepository.allMedicines,
        // This is a simplified approach. In a real app, you would want to
        // fetch all dose logs and then process them.
        medicineRepository.getDoseLogsForMedicine(1) // Placeholder for a real implementation
    ) { medicines, doseLogs ->
        val totalDoses = medicines.size
        val takenDoses = doseLogs.count { it.takenTime != null }
        val adherence = if (totalDoses > 0) (takenDoses.toFloat() / totalDoses) * 100 else 0f

        AnalyticsData(
            totalDoses = totalDoses,
            takenDoses = takenDoses,
            adherence = adherence,
            streak = calculateStreak(doseLogs)
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AnalyticsData()
    )

    private fun calculateStreak(doseLogs: List<DoseLog>): Int {
        // Placeholder for streak calculation logic
        return 0
    }
}

data class AnalyticsData(
    val totalDoses: Int = 0,
    val takenDoses: Int = 0,
    val adherence: Float = 0f,
    val streak: Int = 0
)
