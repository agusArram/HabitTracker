package com.example.habittracker.domain.repository

import com.example.habittracker.domain.model.DailyLogDomain
import com.example.habittracker.domain.model.HabitDomain
import kotlinx.coroutines.flow.Flow

interface HabitRepository {
    fun getAllHabits(): Flow<List<HabitDomain>>

    suspend fun getHabitById(id: Long): HabitDomain?

    suspend fun insertHabit(habit: HabitDomain): Long

    suspend fun updateHabit(habit: HabitDomain)

    suspend fun deleteHabit(habit: HabitDomain)

    suspend fun updateHabitsOrder(habits: List<HabitDomain>)

    fun getLogsInRange(startDate: String, endDate: String): Flow<List<DailyLogDomain>>

    suspend fun getLogForDate(habitId: Long, date: String): DailyLogDomain?

    suspend fun getAllLogsForHabit(habitId: Long): List<DailyLogDomain>

    suspend fun toggleDailyLog(habitId: Long, date: String)
}
