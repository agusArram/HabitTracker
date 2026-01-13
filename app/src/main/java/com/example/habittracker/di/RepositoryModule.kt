package com.example.habittracker.di

import com.example.habittracker.data.repository.HabitRepositoryImpl
import com.example.habittracker.data.repository.VacationRepositoryImpl
import com.example.habittracker.domain.repository.HabitRepository
import com.example.habittracker.domain.repository.VacationRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindHabitRepository(
        impl: HabitRepositoryImpl
    ): HabitRepository

    @Binds
    @Singleton
    abstract fun bindVacationRepository(
        impl: VacationRepositoryImpl
    ): VacationRepository
}

