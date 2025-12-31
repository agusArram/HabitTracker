package com.example.habittracker.data.dao

import androidx.room.*
import com.example.habittracker.data.entity.DailyLog
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyLogDao {
    @Query("SELECT * FROM daily_logs WHERE habitId = :habitId AND date BETWEEN :startDate AND :endDate ORDER BY date ASC")
    fun getLogsForHabitInRange(habitId: Long, startDate: String, endDate: String): Flow<List<DailyLog>>

    @Query("SELECT * FROM daily_logs WHERE date BETWEEN :startDate AND :endDate ORDER BY date ASC")
    fun getAllLogsInRange(startDate: String, endDate: String): Flow<List<DailyLog>>

    @Query("SELECT * FROM daily_logs WHERE habitId = :habitId AND date = :date")
    suspend fun getLogForDate(habitId: Long, date: String): DailyLog?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: DailyLog)

    @Update
    suspend fun updateLog(log: DailyLog)

    @Delete
    suspend fun deleteLog(log: DailyLog)

    @Query("DELETE FROM daily_logs WHERE habitId = :habitId")
    suspend fun deleteLogsForHabit(habitId: Long)

    @Query("SELECT * FROM daily_logs WHERE habitId = :habitId ORDER BY date DESC")
    suspend fun getAllLogsForHabit(habitId: Long): List<DailyLog>
}
