package com.example.habittracker.data.model

import androidx.compose.ui.graphics.Color

enum class HabitCategory(
    val displayName: String,
    val color: Color,
    val emoji: String
) {
    HEALTH("Salud", Color(0xFF10b981), "💪"),
    LEARNING("Aprendizaje", Color(0xFF3b82f6), "🧠"),
    WORK("Trabajo", Color(0xFFf97316), "💼"),
    PERSONAL("Personal", Color(0xFF8b5cf6), "🎯"),
    SOCIAL("Social", Color(0xFFec4899), "👥"),
    CREATIVITY("Creatividad", Color(0xFFf59e0b), "🎨");

    companion object {
        fun fromString(value: String): HabitCategory {
            return values().find { it.displayName == value } ?: PERSONAL
        }

        fun getColorHex(category: HabitCategory): String {
            val rgb = android.graphics.Color.valueOf(
                category.color.red,
                category.color.green,
                category.color.blue
            ).toArgb() and 0xFFFFFF
            return String.format("#%06X", rgb)
        }
    }
}
