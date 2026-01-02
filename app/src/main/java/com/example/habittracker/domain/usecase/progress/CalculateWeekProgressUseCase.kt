package com.example.habittracker.domain.usecase.progress

import com.example.habittracker.domain.model.HabitWithProgress
import com.example.habittracker.domain.model.WeekProgress
import javax.inject.Inject

class CalculateWeekProgressUseCase @Inject constructor() {
    operator fun invoke(habits: List<HabitWithProgress>): WeekProgress {
        if (habits.isEmpty()) {
            return WeekProgress(0, 0, 0f)
        }

        // Count only active days for each habit
        val totalPossible = habits.sumOf { habitWithProgress ->
            val weekDays = habitWithProgress.habit.weekDays
            listOf(
                weekDays.monday,
                weekDays.tuesday,
                weekDays.wednesday,
                weekDays.thursday,
                weekDays.friday,
                weekDays.saturday,
                weekDays.sunday
            ).count { it }
        }

        val completed = habits.sumOf { habit ->
            habit.logs.count { it.value }
        }

        return WeekProgress(
            totalDays = totalPossible,
            completedDays = completed,
            percentage = if (totalPossible > 0) (completed.toFloat() / totalPossible * 100) else 0f
        )
    }
}
