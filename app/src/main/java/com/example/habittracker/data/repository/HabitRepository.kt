package com.example.habittracker.data.repository

import com.example.habittracker.data.dao.DailyLogDao
import com.example.habittracker.data.dao.HabitDao
import com.example.habittracker.data.entity.DailyLog
import com.example.habittracker.data.entity.Habit
import kotlinx.coroutines.flow.Flow

class HabitRepository(
    private val habitDao: HabitDao,
    private val dailyLogDao: DailyLogDao
) {
    fun getAllHabits(): Flow<List<Habit>> = habitDao.getAllHabits()

    suspend fun insertHabit(habit: Habit): Long = habitDao.insertHabit(habit)

    suspend fun deleteHabit(habit: Habit) = habitDao.deleteHabit(habit)

    fun getLogsForHabitInRange(habitId: Long, startDate: String, endDate: String): Flow<List<DailyLog>> =
        dailyLogDao.getLogsForHabitInRange(habitId, startDate, endDate)

    fun getAllLogsInRange(startDate: String, endDate: String): Flow<List<DailyLog>> =
        dailyLogDao.getAllLogsInRange(startDate, endDate)

    suspend fun toggleDailyLog(habitId: Long, date: String) {
        val existingLog = dailyLogDao.getLogForDate(habitId, date)
        if (existingLog != null) {
            dailyLogDao.updateLog(existingLog.copy(completed = !existingLog.completed))
        } else {
            dailyLogDao.insertLog(DailyLog(habitId = habitId, date = date, completed = true))
        }
    }

    suspend fun getLogForDate(habitId: Long, date: String): DailyLog? =
        dailyLogDao.getLogForDate(habitId, date)

    suspend fun getAllLogsForHabit(habitId: Long): List<DailyLog> =
        dailyLogDao.getAllLogsForHabit(habitId)

    suspend fun updateHabit(habit: Habit) = habitDao.updateHabit(habit)

    suspend fun updateHabitsOrder(habits: List<Habit>) {
        habits.forEachIndexed { index, habit ->
            habitDao.updateHabit(habit.copy(orderPosition = index))
        }
    }
}
