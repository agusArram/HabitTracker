package com.example.habittracker.data.repository

import com.example.habittracker.data.dao.VacationDao
import com.example.habittracker.data.entity.VacationPeriod
import com.example.habittracker.domain.repository.VacationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class VacationRepositoryImpl @Inject constructor(
    private val vacationDao: VacationDao
) : VacationRepository {
    
    override fun getActiveVacation(): Flow<VacationPeriod?> {
        return vacationDao.getActiveVacation()
    }
    
    override fun getAllVacations(): Flow<List<VacationPeriod>> {
        return vacationDao.getAllVacations()
    }
    
    override suspend fun setVacation(startDate: String, endDate: String): Long {
        // Deactivate any existing vacation first
        vacationDao.deactivateAll()
        // Insert new active vacation
        return vacationDao.insert(
            VacationPeriod(
                startDate = startDate,
                endDate = endDate,
                isActive = true
            )
        )
    }
    
    override suspend fun deactivateVacation() {
        vacationDao.deactivateAll()
    }
    
    override suspend fun deleteVacation(id: Long) {
        vacationDao.delete(id)
    }
}
