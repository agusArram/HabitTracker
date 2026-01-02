package com.example.habittracker.presentation.screens.habittracker

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.habittracker.R
import com.example.habittracker.presentation.components.*
import com.example.habittracker.presentation.dialog.AddHabitDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitTrackerScreen(
    viewModel: HabitTrackerViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.showAddDialog() },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = stringResource(R.string.cd_add_habit),
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Branding header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.app_branding),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Normal
                )
            }

            WeekHeader(
                weekStart = state.currentWeekStart,
                weekProgress = state.weekProgress,
                monthProgress = state.monthProgress,
                onPreviousWeek = { viewModel.previousWeek() },
                onNextWeek = { viewModel.nextWeek() }
            )

            if (state.habits.isEmpty()) {
                EmptyState()
            } else {
                HabitGrid(
                    habits = state.habits,
                    daysInWeek = state.daysInWeek,
                    onDayClick = { habitId, date -> viewModel.toggleDay(habitId, date) },
                    onDeleteHabit = { habit -> viewModel.deleteHabit(habit) },
                    onEditHabit = { habit -> viewModel.updateHabit(habit) },
                    onReorder = { habits -> viewModel.reorderHabits(habits) }
                )
            }
        }
    }

    if (state.showAddDialog) {
        AddHabitDialog(
            onDismiss = { viewModel.hideAddDialog() },
            onConfirm = { habit -> viewModel.addHabit(habit) }
        )
    }
}
