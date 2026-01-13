package com.example.habittracker.domain.repository

import com.example.habittracker.data.entity.VacationPeriod
import kotlinx.coroutines.flow.Flow

interface VacationRepository {
    fun getActiveVacation(): Flow<VacationPeriod?>
    fun getAllVacations(): Flow<List<VacationPeriod>>
    suspend fun setVacation(startDate: String, endDate: String): Long
    suspend fun deactivateVacation()
    suspend fun deleteVacation(id: Long)
}
