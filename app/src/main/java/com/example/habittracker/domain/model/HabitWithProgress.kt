package com.example.habittracker.domain.model

data class HabitWithProgress(
    val habit: HabitDomain,
    val logs: Map<String, Boolean>,
    val currentStreak: Int = 0,
    val bestStreak: Int = 0
)
