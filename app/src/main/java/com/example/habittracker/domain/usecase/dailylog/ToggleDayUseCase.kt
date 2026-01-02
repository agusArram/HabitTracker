package com.example.habittracker.domain.usecase.dailylog

import com.example.habittracker.domain.repository.HabitRepository
import javax.inject.Inject

class ToggleDayUseCase @Inject constructor(
    private val repository: HabitRepository
) {
    suspend operator fun invoke(habitId: Long, date: String) {
        repository.toggleDailyLog(habitId, date)
    }
}
