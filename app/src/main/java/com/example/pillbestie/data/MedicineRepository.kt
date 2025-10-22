package com.example.pillbestie.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class MedicineRepository(private val medicineDao: MedicineDao, private val doseLogDao: DoseLogDao) {
    val allMedicines: Flow<List<Medicine>> = medicineDao.getAllMedicines()
    val allDoseLogs: Flow<List<DoseLog>> = doseLogDao.getAllDoseLogs()

    suspend fun insert(medicine: Medicine) {
        medicineDao.insert(medicine)
    }

    suspend fun updateMedicine(medicine: Medicine) {
        medicineDao.update(medicine)
    }

    suspend fun delete(medicine: Medicine) {
        medicineDao.delete(medicine)
    }

    fun getMedicine(medicineId: Int): Flow<Medicine> {
        return medicineDao.getMedicine(medicineId)
    }

    fun getDoseLogsForMedicine(medicineId: Int): Flow<List<DoseLog>> {
        return doseLogDao.getDoseLogsForMedicine(medicineId)
    }

    suspend fun insert(doseLog: DoseLog) {
        doseLogDao.insert(doseLog)
    }

    suspend fun updateDoseLog(doseLog: DoseLog) {
        doseLogDao.update(doseLog)
    }

    suspend fun isImageHashUnique(imageHash: String): Boolean {
        return doseLogDao.isImageHashUnique(imageHash) == 0
    }

    suspend fun getNextUpcomingMedicine(medicineId: Int): Medicine? {
        val medicine = medicineDao.getMedicine(medicineId).first()
        val currentTime = System.currentTimeMillis()
        var nextTime = Long.MAX_VALUE

        medicine.times.forEach { time ->
            if (time > currentTime && time < nextTime) {
                nextTime = time
            }
        }

        return if (nextTime != Long.MAX_VALUE) {
            medicine.copy(times = listOf(nextTime))
        } else {
            null
        }
    }
    
suspend fun getNextUpcomingMedicine(): Medicine? {
        val allMedicines = allMedicines.first()
        val currentTime = System.currentTimeMillis()
        var nextMedicine: Medicine? = null
        var nextTime = Long.MAX_VALUE

        allMedicines.forEach { medicine ->
            medicine.times.forEach { time ->
                if (time > currentTime && time < nextTime) {
                    nextTime = time
                    nextMedicine = medicine
                }
            }
        }

        return nextMedicine?.let {
            it.copy(times = listOf(nextTime))
        }
    }
}
