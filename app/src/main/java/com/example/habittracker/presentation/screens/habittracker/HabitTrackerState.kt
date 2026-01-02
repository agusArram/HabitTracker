package com.example.habittracker.presentation.screens.habittracker

import com.example.habittracker.domain.model.HabitWithProgress
import com.example.habittracker.domain.model.MonthProgress
import com.example.habittracker.domain.model.WeekProgress
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.TemporalAdjusters

data class HabitTrackerState(
    val habits: List<HabitWithProgress> = emptyList(),
    val weekProgress: WeekProgress = WeekProgress(0, 0, 0f),
    val monthProgress: MonthProgress = MonthProgress(0, 0, 0f),
    val currentMonth: YearMonth = YearMonth.now(),
    val currentWeekStart: LocalDate = LocalDate.now().with(
        TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)
    ),
    val daysInWeek: List<LocalDate> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val showAddDialog: Boolean = false
)
