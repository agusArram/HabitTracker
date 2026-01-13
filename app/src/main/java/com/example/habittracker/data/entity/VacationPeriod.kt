package com.example.habittracker.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vacation_periods")
data class VacationPeriod(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val startDate: String,  // yyyy-MM-dd format
    val endDate: String,    // yyyy-MM-dd format
    val isActive: Boolean = true
)
