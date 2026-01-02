package com.example.habittracker.domain.usecase.habit

import com.example.habittracker.domain.model.HabitWithProgress
import com.example.habittracker.domain.repository.HabitRepository
import com.example.habittracker.domain.usecase.progress.CalculateStreaksUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class GetAllHabitsUseCase @Inject constructor(
    private val repository: HabitRepository,
    private val calculateStreaksUseCase: CalculateStreaksUseCase
) {
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    operator fun invoke(weekStart: LocalDate): Flow<List<HabitWithProgress>> {
        val weekEnd = weekStart.plusDays(6)

        return combine(
            repository.getAllHabits(),
            repository.getLogsInRange(
                weekStart.format(dateFormatter),
                weekEnd.format(dateFormatter)
            )
        ) { habits, weekLogs ->
            val logsByHabit = weekLogs.groupBy { it.habitId }

            habits.map { habit ->
                val habitLogs = logsByHabit[habit.id].orEmpty()
                val logsMap = habitLogs.associate { it.date to it.completed }

                // Calculate streaks
                val streaks = calculateStreaksUseCase(habit.id)

                HabitWithProgress(
                    habit = habit,
                    logs = logsMap,
                    currentStreak = streaks.currentStreak,
                    bestStreak = streaks.bestStreak
                )
            }
        }
    }
}
