package com.example.habittracker.domain.usecase.habit

import com.example.habittracker.domain.model.HabitDomain
import com.example.habittracker.domain.repository.HabitRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class GetHabitsWithMonthLogsUseCase @Inject constructor(
    private val repository: HabitRepository
) {
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    operator fun invoke(month: YearMonth): Flow<List<Pair<HabitDomain, Map<String, Boolean>>>> {
        val monthStart = month.atDay(1)
        val monthEnd = month.atEndOfMonth()

        return combine(
            repository.getAllHabits(),
            repository.getLogsInRange(
                monthStart.format(dateFormatter),
                monthEnd.format(dateFormatter)
            )
        ) { habits, monthLogs ->
            val logsByHabit = monthLogs.groupBy { it.habitId }

            habits.map { habit ->
                val habitLogs = logsByHabit[habit.id].orEmpty()
                val logsMap = habitLogs.associate { it.date to it.completed }
                habit to logsMap
            }
        }
    }
}
