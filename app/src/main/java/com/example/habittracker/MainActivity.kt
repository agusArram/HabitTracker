package com.example.habittracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.habittracker.data.database.AppDatabase
import com.example.habittracker.data.repository.HabitRepository
import com.example.habittracker.ui.screens.HabitTrackerScreen
import com.example.habittracker.ui.theme.HabitTrackerTheme
import com.example.habittracker.viewmodel.HabitViewModel
import com.example.habittracker.viewmodel.HabitViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = AppDatabase.getDatabase(applicationContext)
        val repository = HabitRepository(
            habitDao = database.habitDao(),
            dailyLogDao = database.dailyLogDao()
        )
        val viewModelFactory = HabitViewModelFactory(repository)

        setContent {
            HabitTrackerTheme {
                val viewModel: HabitViewModel = viewModel(factory = viewModelFactory)
                HabitTrackerScreen(viewModel = viewModel)
            }
        }
    }
}