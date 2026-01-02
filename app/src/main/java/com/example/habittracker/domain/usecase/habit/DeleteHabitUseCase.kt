package com.example.habittracker.domain.usecase.habit

import com.example.habittracker.domain.model.HabitDomain
import com.example.habittracker.domain.repository.HabitRepository
import javax.inject.Inject

class DeleteHabitUseCase @Inject constructor(
    private val repository: HabitRepository
) {
    suspend operator fun invoke(habit: HabitDomain) {
        repository.deleteHabit(habit)
    }
}
