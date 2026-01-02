package com.example.habittracker.domain.model

data class DailyLogDomain(
    val id: Long = 0,
    val habitId: Long,
    val date: String,
    val completed: Boolean
)
