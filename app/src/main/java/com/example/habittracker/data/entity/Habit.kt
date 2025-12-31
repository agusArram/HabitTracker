package com.example.habittracker.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "habits")
data class Habit(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val emoji: String,
    val createdAt: Long = System.currentTimeMillis(),
    val bestStreak: Int = 0, // Mejor racha histórica
    val category: String = "Personal", // Categoría del hábito
    val color: String = "#38bdf8", // Color de la categoría
    val orderPosition: Int = 0, // Posición para ordenar
    val weekDays: String = "1111111" // Días de la semana: LMMJVSD (1=activo, 0=inactivo)
)
