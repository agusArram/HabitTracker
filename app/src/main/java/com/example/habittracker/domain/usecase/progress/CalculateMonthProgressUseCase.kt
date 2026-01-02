package com.example.habittracker.domain.usecase.progress

import com.example.habittracker.domain.model.DailyLogDomain
import com.example.habittracker.domain.model.HabitDomain
import com.example.habittracker.domain.model.MonthProgress
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject

class CalculateMonthProgressUseCase @Inject constructor() {
    operator fun invoke(
        habits: List<HabitDomain>,
        monthLogs: List<DailyLogDomain>,
        month: YearMonth
    ): MonthProgress {
        if (habits.isEmpty()) {
            return MonthProgress(0, 0, 0f)
        }

        val monthStart = month.atDay(1)
        val monthEnd = month.atEndOfMonth()

        // Count possible days based on active days for each habit
        val totalPossible = habits.sumOf { habit ->
            var count = 0
            var currentDate = monthStart
            while (!currentDate.isAfter(monthEnd)) {
                val dayOfWeek = currentDate.dayOfWeek.value % 7 // 0=Mon, 6=Sun
                val isActive = when (dayOfWeek) {
                    0 -> habit.weekDays.monday
                    1 -> habit.weekDays.tuesday
                    2 -> habit.weekDays.wednesday
                    3 -> habit.weekDays.thursday
                    4 -> habit.weekDays.friday
                    5 -> habit.weekDays.saturday
                    else -> habit.weekDays.sunday
                }
                if (isActive) count++
                currentDate = currentDate.plusDays(1)
            }
            count
        }

        val completed = monthLogs.count { it.completed }

        return MonthProgress(
            totalDays = totalPossible,
            completedDays = completed,
            percentage = if (totalPossible > 0) (completed.toFloat() / totalPossible * 100) else 0f
        )
    }
}
