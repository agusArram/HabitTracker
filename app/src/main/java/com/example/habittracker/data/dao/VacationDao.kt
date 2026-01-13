package com.example.habittracker.data.dao

import androidx.room.*
import com.example.habittracker.data.entity.VacationPeriod
import kotlinx.coroutines.flow.Flow

@Dao
interface VacationDao {
    @Query("SELECT * FROM vacation_periods WHERE isActive = 1 ORDER BY startDate DESC LIMIT 1")
    fun getActiveVacation(): Flow<VacationPeriod?>
    
    @Query("SELECT * FROM vacation_periods ORDER BY startDate DESC")
    fun getAllVacations(): Flow<List<VacationPeriod>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vacation: VacationPeriod): Long
    
    @Update
    suspend fun update(vacation: VacationPeriod)
    
    @Query("UPDATE vacation_periods SET isActive = 0")
    suspend fun deactivateAll()
    
    @Query("DELETE FROM vacation_periods WHERE id = :id")
    suspend fun delete(id: Long)
}
