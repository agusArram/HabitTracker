package com.example.habittracker.di

import android.content.Context
import androidx.room.Room
import com.example.habittracker.data.dao.DailyLogDao
import com.example.habittracker.data.dao.HabitDao
import com.example.habittracker.data.database.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "habit_tracker_database"
        ).build()
    }

    @Provides
    @Singleton
    fun provideHabitDao(database: AppDatabase): HabitDao =
        database.habitDao()

    @Provides
    @Singleton
    fun provideDailyLogDao(database: AppDatabase): DailyLogDao =
        database.dailyLogDao()
}
