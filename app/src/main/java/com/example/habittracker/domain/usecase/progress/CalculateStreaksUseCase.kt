package com.example.habittracker.domain.usecase.progress

import com.example.habittracker.domain.repository.HabitRepository
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class CalculateStreaksUseCase @Inject constructor(
    private val repository: HabitRepository
) {
    data class StreakResult(
        val currentStreak: Int,
        val bestStreak: Int
    )

    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    suspend operator fun invoke(habitId: Long): StreakResult {
        val allLogs = repository.getAllLogsForHabit(habitId)
            .filter { it.completed }
            .map { LocalDate.parse(it.date, dateFormatter) }
            .sortedDescending()

        if (allLogs.isEmpty()) return StreakResult(0, 0)

        // Calculate current streak (from today backwards)
        var currentStreak = 0
        var checkDate = LocalDate.now()

        for (logDate in allLogs) {
            if (logDate == checkDate) {
                currentStreak++
                checkDate = checkDate.minusDays(1)
            } else if (logDate < checkDate) {
                // There's a gap, streak ends
                break
            }
        }

        // Calculate best historical streak
        var bestStreak = 0
        var tempStreak = 1

        for (i in 0 until allLogs.size - 1) {
            val diff = allLogs[i].toEpochDay() - allLogs[i + 1].toEpochDay()
            if (diff == 1L) {
                tempStreak++
                bestStreak = maxOf(bestStreak, tempStreak)
            } else {
                tempStreak = 1
            }
        }
        bestStreak = maxOf(bestStreak, tempStreak, currentStreak)

        return StreakResult(currentStreak, bestStreak)
    }
}
