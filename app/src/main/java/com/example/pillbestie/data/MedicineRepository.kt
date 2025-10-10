package com.example.pillbestie.data

import kotlinx.coroutines.flow.Flow

class MedicineRepository(private val medicineDao: MedicineDao, private val doseLogDao: DoseLogDao, private val journalEntryDao: JournalEntryDao) {
    val allMedicines: Flow<List<Medicine>> = medicineDao.getAllMedicines()
    val allJournalEntries: Flow<List<JournalEntry>> = journalEntryDao.getAllEntries()
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

    suspend fun insert(journalEntry: JournalEntry) {
        journalEntryDao.insert(journalEntry)
    }

    suspend fun isImageHashUnique(imageHash: String): Boolean {
        return doseLogDao.isImageHashUnique(imageHash) == 0
    }
}
