package com.example.pillbestie.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MedicineDao {
    @Insert
    suspend fun insert(medicine: Medicine)

    @Query("SELECT * FROM medicines")
    fun getAllMedicines(): Flow<List<Medicine>>
}
