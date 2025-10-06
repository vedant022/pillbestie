package com.example.pillbestie.data

import kotlinx.coroutines.flow.Flow

class MedicineRepository(private val medicineDao: MedicineDao, private val doseLogDao: DoseLogDao, private val moodEntryDao: MoodEntryDao) {
    val allMedicines: Flow<List<Medicine>> = medicineDao.getAllMedicines()
    val allMoodEntries: Flow<List<MoodEntry>> = moodEntryDao.getAllMoodEntries()

    suspend fun insert(medicine: Medicine) {
        medicineDao.insert(medicine)
    }

    fun getDoseLogsForMedicine(medicineId: Int): Flow<List<DoseLog>> {
        return doseLogDao.getDoseLogsForMedicine(medicineId)
    }

    suspend fun insert(doseLog: DoseLog) {
        doseLogDao.insert(doseLog)
    }

    suspend fun insert(moodEntry: MoodEntry) {
        moodEntryDao.insert(moodEntry)
    }

    suspend fun isImageHashUnique(imageHash: String): Boolean {
        return doseLogDao.isImageHashUnique(imageHash) == 0
    }
}
