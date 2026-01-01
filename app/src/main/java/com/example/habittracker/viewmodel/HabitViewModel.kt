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
    val logs: Map<String, Boolean>, // date -> completed
    val currentStreak: Int = 0, // Racha actual
    val bestStreak: Int = 0 // Mejor racha
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

                // Calcular rachas
                val streaks = calculateStreaks(habit.id)

                HabitWithProgress(
                    habit = habit,
                    logs = logsMap,
                    currentStreak = streaks.first,
                    bestStreak = streaks.second
                )
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private suspend fun calculateStreaks(habitId: Long): Pair<Int, Int> {
        val allLogs = repository.getAllLogsForHabit(habitId)
            .filter { it.completed }
            .map { LocalDate.parse(it.date, dateFormatter) }
            .sortedDescending()

        if (allLogs.isEmpty()) return Pair(0, 0)

        // Calcular racha actual (desde hoy hacia atrás)
        var currentStreak = 0
        var checkDate = LocalDate.now()

        for (logDate in allLogs) {
            if (logDate == checkDate) {
                currentStreak++
                checkDate = checkDate.minusDays(1)
            } else if (logDate < checkDate) {
                // Hay un hueco, termina la racha
                break
            }
        }

        // Calcular mejor racha histórica
        var bestStreak = 0
        var tempStreak = 1

        for (i in 0 until allLogs.size - 1) {
            val diff = allLogs[i].toEpochDay() - allLogs[i + 1].toEpochDay()
            if (diff == 1L) {
                tempStreak++
                bestStreak = maxOf(bestStreak, tempStreak)
            } else {
                tempStreak = 1
            }
        }
        bestStreak = maxOf(bestStreak, tempStreak, currentStreak)

        return Pair(currentStreak, bestStreak)
    }

    val weekProgress: StateFlow<WeekProgress> = habitsWithProgress.map { habits ->
        if (habits.isEmpty()) {
            WeekProgress(0, 0, 0f)
        } else {
            // Contar solo los días activos de cada hábito
            val totalPossible = habits.sumOf { habitWithProgress ->
                habitWithProgress.habit.weekDays.count { it == '1' }
            }
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
                // Contar días posibles basado en los días activos de cada hábito
                val totalPossible = habits.sumOf { habit ->
                    var count = 0
                    var currentDate = monthStart
                    while (!currentDate.isAfter(monthEnd)) {
                        val dayOfWeek = currentDate.dayOfWeek.value % 7 // 0=Lun, 6=Dom
                        if (habit.weekDays.getOrNull(dayOfWeek) == '1') {
                            count++
                        }
                        currentDate = currentDate.plusDays(1)
                    }
                    count
                }
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

    fun addHabit(name: String, emoji: String, category: String, color: String, weekDays: String = "1111111") {
        viewModelScope.launch {
            repository.insertHabit(
                Habit(
                    name = name,
                    emoji = emoji,
                    category = category,
                    color = color,
                    weekDays = weekDays
                )
            )
        }
    }

    fun deleteHabit(habit: Habit) {
        viewModelScope.launch {
            repository.deleteHabit(habit)
        }
    }

    fun updateHabit(habit: Habit) {
        viewModelScope.launch {
            repository.updateHabit(habit)
        }
    }

    fun updateHabitsOrder(habits: List<Habit>) {
        viewModelScope.launch {
            repository.updateHabitsOrder(habits)
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
