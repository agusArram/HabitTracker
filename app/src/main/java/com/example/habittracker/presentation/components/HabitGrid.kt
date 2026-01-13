package com.example.habittracker.presentation.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import com.example.habittracker.domain.model.HabitDomain
import com.example.habittracker.domain.model.HabitWithProgress
import com.example.habittracker.presentation.dialog.EditHabitDialog
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun HabitGrid(
    habits: List<HabitWithProgress>,
    daysInWeek: List<LocalDate>,
    onDayClick: (Long, String) -> Unit,
    onDeleteHabit: (HabitDomain) -> Unit,
    onEditHabit: (HabitDomain) -> Unit,
    onReorder: (List<HabitDomain>) -> Unit,
    isReorderMode: Boolean = false
) {
    val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    var habitsList by remember(habits) { mutableStateOf(habits) }
    val hapticFeedback = LocalHapticFeedback.current
    
    val lazyListState = rememberLazyListState()
    val reorderableLazyListState = rememberReorderableLazyListState(lazyListState) { from, to ->
        habitsList = habitsList.toMutableList().apply {
            add(to.index, removeAt(from.index))
        }
        hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
    }
    
    LaunchedEffect(isReorderMode) {
        if (!isReorderMode && habitsList != habits) {
            // User exited reorder mode, save the new order
            onReorder(habitsList.map { it.habit })
        }
    }
    
    LaunchedEffect(habits) {
        if (!isReorderMode) {
            habitsList = habits
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp)
    ) {
        DayHeaders(daysInWeek)

        LazyColumn(
            state = lazyListState,
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(
                items = habitsList,
                key = { it.habit.id }
            ) { habitWithProgress ->
                ReorderableItem(
                    state = reorderableLazyListState,
                    key = habitWithProgress.habit.id,
                    enabled = isReorderMode
                ) { isDragging ->
                    var showEditDialog by remember { mutableStateOf(false) }
                    
                    val elevation by animateDpAsState(
                        targetValue = if (isDragging) 8.dp else 0.dp,
                        label = "dragElevation"
                    )

                    HabitRowWithDragHandle(
                        scope = this,
                        habit = habitWithProgress.habit,
                        daysInWeek = daysInWeek,
                        logs = habitWithProgress.logs,
                        currentStreak = habitWithProgress.currentStreak,
                        bestStreak = habitWithProgress.bestStreak,
                        onDayClick = { date ->
                            if (!isReorderMode) {
                                onDayClick(habitWithProgress.habit.id, date.format(dateFormatter))
                            }
                        },
                        onDelete = { onDeleteHabit(habitWithProgress.habit) },
                        onLongClick = { 
                            if (!isReorderMode) {
                                showEditDialog = true
                            }
                        },
                        isDragging = isDragging,
                        isReorderMode = isReorderMode,
                        elevation = elevation,
                        onDragStarted = {
                            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                        },
                        onDragStopped = {
                            hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        }
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
}
