package com.example.habittracker.data.repository

import com.example.habittracker.data.dao.DailyLogDao
import com.example.habittracker.data.dao.HabitDao
import com.example.habittracker.data.entity.DailyLog
import com.example.habittracker.data.mapper.DailyLogMapper
import com.example.habittracker.data.mapper.HabitMapper
import com.example.habittracker.domain.model.DailyLogDomain
import com.example.habittracker.domain.model.HabitDomain
import com.example.habittracker.domain.repository.HabitRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HabitRepositoryImpl @Inject constructor(
    private val habitDao: HabitDao,
    private val dailyLogDao: DailyLogDao
) : HabitRepository {

    override fun getAllHabits(): Flow<List<HabitDomain>> =
        habitDao.getAllHabits().map { HabitMapper.toDomainList(it) }

    override suspend fun getHabitById(id: Long): HabitDomain? =
        habitDao.getHabitById(id)?.let { HabitMapper.toDomain(it) }

    override suspend fun insertHabit(habit: HabitDomain): Long =
        habitDao.insertHabit(HabitMapper.toEntity(habit))

    override suspend fun updateHabit(habit: HabitDomain) =
        habitDao.updateHabit(HabitMapper.toEntity(habit))

    override suspend fun deleteHabit(habit: HabitDomain) =
        habitDao.deleteHabit(HabitMapper.toEntity(habit))

    override suspend fun updateHabitsOrder(habits: List<HabitDomain>) {
        habits.forEachIndexed { index, habit ->
            val habitWithPosition = habit.copy(orderPosition = index)
            habitDao.updateHabit(HabitMapper.toEntity(habitWithPosition))
        }
    }

    override fun getLogsInRange(startDate: String, endDate: String): Flow<List<DailyLogDomain>> =
        dailyLogDao.getAllLogsInRange(startDate, endDate).map {
            DailyLogMapper.toDomainList(it)
        }

    override suspend fun getLogForDate(habitId: Long, date: String): DailyLogDomain? =
        dailyLogDao.getLogForDate(habitId, date)?.let { DailyLogMapper.toDomain(it) }

    override suspend fun getAllLogsForHabit(habitId: Long): List<DailyLogDomain> =
        DailyLogMapper.toDomainList(dailyLogDao.getAllLogsForHabit(habitId))

    override suspend fun toggleDailyLog(habitId: Long, date: String) {
        val existingLog = dailyLogDao.getLogForDate(habitId, date)
        if (existingLog != null) {
            dailyLogDao.updateLog(existingLog.copy(completed = !existingLog.completed))
        } else {
            dailyLogDao.insertLog(DailyLog(habitId = habitId, date = date, completed = true))
        }
    }
}
