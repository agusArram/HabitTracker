package com.example.habittracker.domain.model

data class MonthProgress(
    val totalDays: Int,
    val completedDays: Int,
    val percentage: Float
)
