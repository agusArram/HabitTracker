package com.example.habittracker.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.habittracker.data.dao.DailyLogDao
import com.example.habittracker.data.dao.HabitDao
import com.example.habittracker.data.entity.DailyLog
import com.example.habittracker.data.entity.Habit

@Database(
    entities = [Habit::class, DailyLog::class],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun habitDao(): HabitDao
    abstract fun dailyLogDao(): DailyLogDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "habit_tracker_database"
                )
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
