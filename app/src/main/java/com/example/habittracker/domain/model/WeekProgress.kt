package com.example.habittracker.domain.model

data class WeekProgress(
    val totalDays: Int,
    val completedDays: Int,
    val percentage: Float
)
