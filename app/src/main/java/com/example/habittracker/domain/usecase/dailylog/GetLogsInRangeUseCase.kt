package com.example.habittracker.domain.usecase.dailylog

import com.example.habittracker.domain.model.DailyLogDomain
import com.example.habittracker.domain.repository.HabitRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetLogsInRangeUseCase @Inject constructor(
    private val repository: HabitRepository
) {
    operator fun invoke(startDate: String, endDate: String): Flow<List<DailyLogDomain>> {
        return repository.getLogsInRange(startDate, endDate)
    }
}
