package com.example.habittracker.domain.model

data class HabitDomain(
    val id: Long = 0,
    val name: String,
    val emoji: String,
    val createdAt: Long = System.currentTimeMillis(),
    val category: HabitCategory,
    val weekDays: WeekDaysSchedule,
    val orderPosition: Int = 0
)

data class WeekDaysSchedule(
    val monday: Boolean,
    val tuesday: Boolean,
    val wednesday: Boolean,
    val thursday: Boolean,
    val friday: Boolean,
    val saturday: Boolean,
    val sunday: Boolean
) {
    fun toStorageString(): String =
        listOf(monday, tuesday, wednesday, thursday, friday, saturday, sunday)
            .joinToString("") { if (it) "1" else "0" }

    companion object {
        fun fromString(value: String): WeekDaysSchedule {
            val chars = value.padEnd(7, '1')
            return WeekDaysSchedule(
                monday = chars.getOrNull(0) == '1',
                tuesday = chars.getOrNull(1) == '1',
                wednesday = chars.getOrNull(2) == '1',
                thursday = chars.getOrNull(3) == '1',
                friday = chars.getOrNull(4) == '1',
                saturday = chars.getOrNull(5) == '1',
                sunday = chars.getOrNull(6) == '1'
            )
        }

        fun allDays() = WeekDaysSchedule(
            monday = true,
            tuesday = true,
            wednesday = true,
            thursday = true,
            friday = true,
            saturday = true,
            sunday = true
        )

        fun noDays() = WeekDaysSchedule(
            monday = false,
            tuesday = false,
            wednesday = false,
            thursday = false,
            friday = false,
            saturday = false,
            sunday = false
        )
    }
}
