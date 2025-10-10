package com.example.pillbestie.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface DoseLogDao {
    @Insert
    suspend fun insert(doseLog: DoseLog)

    @Update
    suspend fun update(doseLog: DoseLog)

    @Query("SELECT * FROM dose_logs WHERE medicineId = :medicineId")
    fun getDoseLogsForMedicine(medicineId: Int): Flow<List<DoseLog>>

    @Query("SELECT * FROM dose_logs")
    fun getAllDoseLogs(): Flow<List<DoseLog>>

    @Query("SELECT COUNT(*) FROM dose_logs WHERE imageHash = :imageHash")
    suspend fun isImageHashUnique(imageHash: String): Int
}
