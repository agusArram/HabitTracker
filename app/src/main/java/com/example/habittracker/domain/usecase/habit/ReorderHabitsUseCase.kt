package com.example.habittracker.domain.usecase.habit

import com.example.habittracker.domain.model.HabitDomain
import com.example.habittracker.domain.repository.HabitRepository
import javax.inject.Inject

class ReorderHabitsUseCase @Inject constructor(
    private val repository: HabitRepository
) {
    suspend operator fun invoke(habits: List<HabitDomain>) {
        repository.updateHabitsOrder(habits)
    }
}
