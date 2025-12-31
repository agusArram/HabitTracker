package com.example.habittracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habittracker.data.entity.DailyLog
import com.example.habittracker.data.entity.Habit
import com.example.habittracker.data.repository.HabitRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters

data class HabitWithProgress(
    val habit: Habit,
    val logs: Map<String, Boolean> // date -> completed
)

data class MonthProgress(
    val totalDays: Int,
    val completedDays: Int,
    val percentage: Float
)

data class WeekProgress(
    val totalDays: Int,
    val completedDays: Int,
    val percentage: Float
)

@OptIn(ExperimentalCoroutinesApi::class)
class HabitViewModel(
    private val repository: HabitRepository
) : ViewModel() {

    private val _currentMonth = MutableStateFlow(YearMonth.now())
    val currentMonth: StateFlow<YearMonth> = _currentMonth.asStateFlow()

    private val _currentWeekStart = MutableStateFlow(LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)))
    val currentWeekStart: StateFlow<LocalDate> = _currentWeekStart.asStateFlow()

    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    val habitsWithProgress: StateFlow<List<HabitWithProgress>> = currentWeekStart.flatMapLatest { weekStart ->
        val weekEnd = weekStart.plusDays(6)
        combine(
            repository.getAllHabits(),
            repository.getAllLogsInRange(
                weekStart.format(dateFormatter),
                weekEnd.format(dateFormatter)
            )
        ) { habits, weekLogs ->
            val logsByHabit = weekLogs.groupBy { it.habitId }
            habits.map { habit ->
                val habitLogs = logsByHabit[habit.id].orEmpty()
                val logsMap = habitLogs.associate { it.date to it.completed }
                HabitWithProgress(habit, logsMap)
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val weekProgress: StateFlow<WeekProgress> = habitsWithProgress.map { habits ->
        if (habits.isEmpty()) {
            WeekProgress(0, 0, 0f)
        } else {
            val totalPossible = habits.size * 7 // 7 días por semana
            val completed = habits.sumOf { habit ->
                habit.logs.count { it.value }
            }
            WeekProgress(
                totalDays = totalPossible,
                completedDays = completed,
                percentage = if (totalPossible > 0) (completed.toFloat() / totalPossible * 100) else 0f
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = WeekProgress(0, 0, 0f)
    )

    val monthProgress: StateFlow<MonthProgress> = currentMonth.flatMapLatest { month ->
        val monthStart = month.atDay(1)
        val monthEnd = month.atEndOfMonth()
        combine(
            repository.getAllHabits(),
            repository.getAllLogsInRange(
                monthStart.format(dateFormatter),
                monthEnd.format(dateFormatter)
            )
        ) { habits, monthLogs ->
            if (habits.isEmpty()) {
                MonthProgress(0, 0, 0f)
            } else {
                val totalDays = month.lengthOfMonth()
                val totalPossible = habits.size * totalDays
                val completed = monthLogs.count { it.completed }

                MonthProgress(
                    totalDays = totalPossible,
                    completedDays = completed,
                    percentage = if (totalPossible > 0) (completed.toFloat() / totalPossible * 100) else 0f
                )
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = MonthProgress(0, 0, 0f)
    )

    fun addHabit(name: String, emoji: String) {
        viewModelScope.launch {
            repository.insertHabit(Habit(name = name, emoji = emoji))
        }
    }

    fun deleteHabit(habit: Habit) {
        viewModelScope.launch {
            repository.deleteHabit(habit)
        }
    }

    fun toggleDay(habitId: Long, date: String) {
        viewModelScope.launch {
            repository.toggleDailyLog(habitId, date)
        }
    }

    fun nextWeek() {
        _currentWeekStart.value = _currentWeekStart.value.plusWeeks(1)
        // Actualizar el mes si cambiamos de mes
        val newMonth = YearMonth.from(_currentWeekStart.value)
        if (newMonth != _currentMonth.value) {
            _currentMonth.value = newMonth
        }
    }

    fun previousWeek() {
        _currentWeekStart.value = _currentWeekStart.value.minusWeeks(1)
        // Actualizar el mes si cambiamos de mes
        val newMonth = YearMonth.from(_currentWeekStart.value)
        if (newMonth != _currentMonth.value) {
            _currentMonth.value = newMonth
        }
    }

    fun nextMonth() {
        _currentMonth.value = _currentMonth.value.plusMonths(1)
    }

    fun previousMonth() {
        _currentMonth.value = _currentMonth.value.minusMonths(1)
    }

    fun getDaysInWeek(): List<LocalDate> {
        val weekStart = _currentWeekStart.value
        return (0..6).map { dayOffset ->
            weekStart.plusDays(dayOffset.toLong())
        }
    }
}
