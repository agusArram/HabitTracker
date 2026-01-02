package com.example.habittracker.presentation.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.habittracker.R
import com.example.habittracker.domain.model.HabitDomain
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HabitRow(
    habit: HabitDomain,
    daysInWeek: List<LocalDate>,
    logs: Map<String, Boolean>,
    currentStreak: Int,
    bestStreak: Int,
    onDayClick: (LocalDate) -> Unit,
    onDelete: () -> Unit,
    onLongClick: () -> Unit,
    isDragging: Boolean = false
) {
    val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val elevation = if (isDragging) 8.dp else 1.dp

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp, horizontal = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = elevation)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Category color indicator
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .background(
                        try {
                            Color(android.graphics.Color.parseColor(habit.category.colorHex))
                        } catch (e: Exception) {
                            MaterialTheme.colorScheme.primary
                        }
                    )
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Habit info
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .width(120.dp)
                        .combinedClickable(
                            onClick = {},
                            onLongClick = onLongClick
                        )
                ) {
                    if (habit.emoji.isNotEmpty()) {
                        Text(
                            text = habit.emoji,
                            fontSize = 24.sp,
                            modifier = Modifier.padding(end = 4.dp)
                        )
                    }
                    Column(modifier = Modifier.weight(1f, fill = false)) {
                        Text(
                            text = habit.name,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium,
                            maxLines = 2,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        // Show streak
                        if (currentStreak > 0) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(top = 2.dp)
                            ) {
                                Text(
                                    text = "🔥",
                                    fontSize = 12.sp
                                )
                                Text(
                                    text = stringResource(R.string.current_streak, currentStreak),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(start = 2.dp)
                                )
                                if (bestStreak > currentStreak) {
                                    Text(
                                        text = stringResource(R.string.best_streak_format, bestStreak),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontSize = 9.sp
                                    )
                                }
                            }
                        }
                    }
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = stringResource(R.string.cd_delete_habit),
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }

                // Day cells
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    val weekDaysList = listOf(
                        habit.weekDays.monday,
                        habit.weekDays.tuesday,
                        habit.weekDays.wednesday,
                        habit.weekDays.thursday,
                        habit.weekDays.friday,
                        habit.weekDays.saturday,
                        habit.weekDays.sunday
                    )

                    daysInWeek.forEachIndexed { index, date ->
                        val dateString = date.format(dateFormatter)
                        val isCompleted = logs[dateString] ?: false
                        val isDayEnabled = weekDaysList.getOrNull(index) ?: true

                        DayCell(
                            isCompleted = isCompleted,
                            onClick = { if (isDayEnabled) onDayClick(date) },
                            modifier = Modifier.weight(1f),
                            isEnabled = isDayEnabled
                        )
                    }
                }
            }
        }
    }
}
