package com.example.habittracker.presentation.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
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
    onReorder: (List<HabitDomain>) -> Unit,
    isReorderMode: Boolean = false
) {
    val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    var habitsList by remember(habits) { mutableStateOf(habits) }
    var draggedIndex by remember { mutableStateOf<Int?>(null) }
    var targetIndex by remember { mutableStateOf<Int?>(null) }
    
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
            state = rememberLazyListState(),
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            itemsIndexed(
                items = habitsList,
                key = { _, item -> item.habit.id }
            ) { index, habitWithProgress ->
                var showEditDialog by remember { mutableStateOf(false) }
                val isDragging = draggedIndex == index
                val elevation by animateDpAsState(
                    targetValue = if (isDragging) 8.dp else 1.dp,
                    label = "elevation"
                )

                Box(
                    modifier = Modifier
                        .shadow(elevation)
                        .then(
                            if (isReorderMode) {
                                Modifier.pointerInput(Unit) {
                                    var dragAmount = 0f
                                    var isDraggingActive = false
                                    
                                    detectDragGesturesAfterLongPress(
                                        onDragStart = {
                                            draggedIndex = index
                                            isDraggingActive = true
                                        },
                                        onDrag = { change, dragDelta ->
                                            if (!isDraggingActive) return@detectDragGesturesAfterLongPress
                                            
                                            change.consume()
                                            dragAmount += dragDelta.y
                                            
                                            // Calculate target index based on drag amount
                                            val itemHeight = 80f // approximate height
                                            val offset = (dragAmount / itemHeight).toInt()
                                            val newTargetIndex = (index + offset).coerceIn(0, habitsList.size - 1)
                                            
                                            if (newTargetIndex != index && targetIndex != newTargetIndex) {
                                                targetIndex = newTargetIndex
                                                
                                                // Swap items
                                                if (draggedIndex != null && targetIndex != null && draggedIndex != targetIndex) {
                                                    habitsList = habitsList.toMutableList().apply {
                                                        val draggedItem = get(draggedIndex!!)
                                                        removeAt(draggedIndex!!)
                                                        add(targetIndex!!, draggedItem)
                                                    }
                                                    draggedIndex = targetIndex
                                                    dragAmount = 0f
                                                }
                                            }
                                        },
                                        onDragEnd = {
                                            isDraggingActive = false
                                            draggedIndex = null
                                            targetIndex = null
                                        },
                                        onDragCancel = {
                                            isDraggingActive = false
                                            draggedIndex = null
                                            targetIndex = null
                                        }
                                    )
                                }
                            } else Modifier
                        )
                ) {
                    HabitRow(
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
                        isReorderMode = isReorderMode
                    )
                }

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
