package com.example.habittracker.presentation.screens.habittracker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habittracker.domain.model.HabitDomain
import com.example.habittracker.domain.usecase.dailylog.ToggleDayUseCase
import com.example.habittracker.domain.usecase.habit.*
import com.example.habittracker.domain.usecase.progress.CalculateMonthProgressUseCase
import com.example.habittracker.domain.usecase.progress.CalculateWeekProgressUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.TemporalAdjusters
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class HabitTrackerViewModel @Inject constructor(
    private val getAllHabitsUseCase: GetAllHabitsUseCase,
    private val addHabitUseCase: AddHabitUseCase,
    private val updateHabitUseCase: UpdateHabitUseCase,
    private val deleteHabitUseCase: DeleteHabitUseCase,
    private val reorderHabitsUseCase: ReorderHabitsUseCase,
    private val toggleDayUseCase: ToggleDayUseCase,
    private val calculateWeekProgressUseCase: CalculateWeekProgressUseCase,
    private val calculateMonthProgressUseCase: CalculateMonthProgressUseCase
) : ViewModel() {

    private val _currentMonth = MutableStateFlow(YearMonth.now())
    private val _currentWeekStart = MutableStateFlow(
        LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
    )
    private val _showAddDialog = MutableStateFlow(false)

    private val habitsWithProgress = _currentWeekStart.flatMapLatest { weekStart ->
        getAllHabitsUseCase(weekStart)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private val weekProgress = habitsWithProgress.map { habits ->
        calculateWeekProgressUseCase(habits)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = com.example.habittracker.domain.model.WeekProgress(0, 0, 0f)
    )

    private val monthProgress = combine(
        _currentMonth,
        habitsWithProgress
    ) { month, habits ->
        // This would need getAllHabits and logs for the month
        // Simplified version for now
        com.example.habittracker.domain.model.MonthProgress(0, 0, 0f)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = com.example.habittracker.domain.model.MonthProgress(0, 0, 0f)
    )

    val state: StateFlow<HabitTrackerState> = combine(
        habitsWithProgress,
        weekProgress,
        monthProgress,
        _currentMonth,
        _currentWeekStart
    ) { habits, weekProg, monthProg, month, weekStart ->
        HabitTrackerState(
            habits = habits,
            weekProgress = weekProg,
            monthProgress = monthProg,
            currentMonth = month,
            currentWeekStart = weekStart,
            daysInWeek = getDaysInWeek(weekStart),
            showAddDialog = false
        )
    }.combine(_showAddDialog) { state, showDialog ->
        state.copy(showAddDialog = showDialog)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HabitTrackerState()
    )

    fun addHabit(habit: HabitDomain) {
        viewModelScope.launch {
            addHabitUseCase(habit)
            _showAddDialog.value = false
        }
    }

    fun updateHabit(habit: HabitDomain) {
        viewModelScope.launch {
            updateHabitUseCase(habit)
        }
    }

    fun deleteHabit(habit: HabitDomain) {
        viewModelScope.launch {
            deleteHabitUseCase(habit)
        }
    }

    fun reorderHabits(habits: List<HabitDomain>) {
        viewModelScope.launch {
            reorderHabitsUseCase(habits)
        }
    }

    fun toggleDay(habitId: Long, date: String) {
        viewModelScope.launch {
            toggleDayUseCase(habitId, date)
        }
    }

    fun nextWeek() {
        val newWeekStart = _currentWeekStart.value.plusWeeks(1)
        _currentWeekStart.value = newWeekStart
        val newMonth = YearMonth.from(newWeekStart)
        if (newMonth != _currentMonth.value) {
            _currentMonth.value = newMonth
        }
    }

    fun previousWeek() {
        val newWeekStart = _currentWeekStart.value.minusWeeks(1)
        _currentWeekStart.value = newWeekStart
        val newMonth = YearMonth.from(newWeekStart)
        if (newMonth != _currentMonth.value) {
            _currentMonth.value = newMonth
        }
    }

    fun showAddDialog() {
        _showAddDialog.value = true
    }

    fun hideAddDialog() {
        _showAddDialog.value = false
    }

    private fun getDaysInWeek(weekStart: LocalDate): List<LocalDate> {
        return (0..6).map { dayOffset ->
            weekStart.plusDays(dayOffset.toLong())
        }
    }
}
