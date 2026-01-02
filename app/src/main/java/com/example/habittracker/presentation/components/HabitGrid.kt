package com.example.habittracker.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.habittracker.domain.model.HabitDomain
import com.example.habittracker.domain.model.HabitWithProgress
import com.example.habittracker.presentation.dialog.EditHabitDialog
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun HabitGrid(
    habits: List<HabitWithProgress>,
    daysInWeek: List<LocalDate>,
    onDayClick: (Long, String) -> Unit,
    onDeleteHabit: (HabitDomain) -> Unit,
    onEditHabit: (HabitDomain) -> Unit,
    onReorder: (List<HabitDomain>) -> Unit
) {
    val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp)
    ) {
        DayHeaders(daysInWeek)

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(habits, key = { it.habit.id }) { habitWithProgress ->
                var showEditDialog by remember { mutableStateOf(false) }

                HabitRow(
                    habit = habitWithProgress.habit,
                    daysInWeek = daysInWeek,
                    logs = habitWithProgress.logs,
                    currentStreak = habitWithProgress.currentStreak,
                    bestStreak = habitWithProgress.bestStreak,
                    onDayClick = { date ->
                        onDayClick(habitWithProgress.habit.id, date.format(dateFormatter))
                    },
                    onDelete = { onDeleteHabit(habitWithProgress.habit) },
                    onLongClick = { showEditDialog = true },
                    isDragging = false
                )

                if (showEditDialog) {
                    EditHabitDialog(
                        habit = habitWithProgress.habit,
                        onDismiss = { showEditDialog = false },
                        onConfirm = { updatedHabit ->
                            onEditHabit(updatedHabit)
                            showEditDialog = false
                        }
                    )
                }
            }
        }
    }
}
