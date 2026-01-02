package com.example.habittracker.domain.model

enum class HabitCategory(
    val colorHex: String,
    val emoji: String
) {
    HEALTH("#10b981", "💪"),
    LEARNING("#3b82f6", "🧠"),
    WORK("#f97316", "💼"),
    PERSONAL("#8b5cf6", "🎯"),
    SOCIAL("#ec4899", "👥"),
    CREATIVITY("#f59e0b", "🎨");

    companion object {
        fun fromName(name: String): HabitCategory {
            return values().find { it.name == name } ?: PERSONAL
        }
    }
}
