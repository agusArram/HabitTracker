package com.example.habittracker.presentation.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.habittracker.R
import com.example.habittracker.domain.model.HabitCategory
import com.example.habittracker.domain.model.HabitDomain
import com.example.habittracker.domain.model.WeekDaysSchedule

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditHabitDialog(
    habit: HabitDomain,
    onDismiss: () -> Unit,
    onConfirm: (HabitDomain) -> Unit
) {
    var name by remember { mutableStateOf(habit.name) }
    var emoji by remember { mutableStateOf(habit.emoji) }
    var selectedCategory by remember { mutableStateOf(habit.category) }
    var weekDays by remember { mutableStateOf(habit.weekDays) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(stringResource(R.string.edit_habit))
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(stringResource(R.string.habit_name)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = emoji,
                    onValueChange = { if (it.length <= 2) emoji = it },
                    label = { Text(stringResource(R.string.emoji_optional)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Text(
                    text = stringResource(R.string.category),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )

                // Grid 2 columnas x 3 filas
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val categories = HabitCategory.values().toList()
                    val rows = categories.chunked(2)

                    rows.forEach { rowCategories ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            rowCategories.forEach { category ->
                                val isSelected = category == selectedCategory
                                val categoryName = when (category) {
                                    HabitCategory.HEALTH -> stringResource(R.string.category_health)
                                    HabitCategory.LEARNING -> stringResource(R.string.category_learning)
                                    HabitCategory.WORK -> stringResource(R.string.category_work)
                                    HabitCategory.PERSONAL -> stringResource(R.string.category_personal)
                                    HabitCategory.SOCIAL -> stringResource(R.string.category_social)
                                    HabitCategory.CREATIVITY -> stringResource(R.string.category_creativity)
                                }

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(50.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(
                                            try {
                                                Color(android.graphics.Color.parseColor(category.colorHex))
                                            } catch (e: Exception) {
                                                MaterialTheme.colorScheme.primary
                                            }
                                        )
                                        .border(
                                            width = if (isSelected) 3.dp else 0.dp,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .clickable { selectedCategory = category },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Text(
                                            text = category.emoji,
                                            style = MaterialTheme.typography.titleMedium
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = categoryName,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Color.White,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            }
                            // Rellenar row si no tiene 2 elementos
                            repeat(2 - rowCategories.size) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }

                Text(
                    text = stringResource(R.string.week_days),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )

                val dayLetters = listOf(
                    stringResource(R.string.day_monday_letter),
                    stringResource(R.string.day_tuesday_letter),
                    stringResource(R.string.day_wednesday_letter),
                    stringResource(R.string.day_thursday_letter),
                    stringResource(R.string.day_friday_letter),
                    stringResource(R.string.day_saturday_letter),
                    stringResource(R.string.day_sunday_letter)
                )
                val weekDaysList = listOf(
                    weekDays.monday,
                    weekDays.tuesday,
                    weekDays.wednesday,
                    weekDays.thursday,
                    weekDays.friday,
                    weekDays.saturday,
                    weekDays.sunday
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    dayLetters.forEachIndexed { index, letter ->
                        val isActive = weekDaysList.getOrNull(index) ?: true
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isActive) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.surfaceVariant
                                )
                                .clickable {
                                    weekDays = when (index) {
                                        0 -> weekDays.copy(monday = !weekDays.monday)
                                        1 -> weekDays.copy(tuesday = !weekDays.tuesday)
                                        2 -> weekDays.copy(wednesday = !weekDays.wednesday)
                                        3 -> weekDays.copy(thursday = !weekDays.thursday)
                                        4 -> weekDays.copy(friday = !weekDays.friday)
                                        5 -> weekDays.copy(saturday = !weekDays.saturday)
                                        else -> weekDays.copy(sunday = !weekDays.sunday)
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = letter,
                                color = if (isActive) MaterialTheme.colorScheme.onPrimary
                                else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (name.isNotBlank()) {
                        onConfirm(
                            habit.copy(
                                name = name,
                                emoji = emoji,
                                category = selectedCategory,
                                weekDays = weekDays
                            )
                        )
                    }
                }
            ) {
                Text(stringResource(R.string.save), color = MaterialTheme.colorScheme.primary)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel), color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    )
}
