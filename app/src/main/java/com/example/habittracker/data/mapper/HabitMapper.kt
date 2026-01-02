package com.example.habittracker.data.mapper

import com.example.habittracker.data.entity.Habit
import com.example.habittracker.domain.model.HabitCategory
import com.example.habittracker.domain.model.HabitDomain
import com.example.habittracker.domain.model.WeekDaysSchedule

object HabitMapper {
    fun toDomain(entity: Habit): HabitDomain = HabitDomain(
        id = entity.id,
        name = entity.name,
        emoji = entity.emoji,
        createdAt = entity.createdAt,
        category = HabitCategory.fromName(entity.category),
        weekDays = WeekDaysSchedule.fromString(entity.weekDays),
        orderPosition = entity.orderPosition
    )

    fun toEntity(domain: HabitDomain): Habit = Habit(
        id = domain.id,
        name = domain.name,
        emoji = domain.emoji,
        createdAt = domain.createdAt,
        category = domain.category.name,
        color = domain.category.colorHex,
        orderPosition = domain.orderPosition,
        weekDays = domain.weekDays.toStorageString()
    )

    fun toDomainList(entities: List<Habit>): List<HabitDomain> =
        entities.map { toDomain(it) }

    fun toEntityList(domains: List<HabitDomain>): List<Habit> =
        domains.map { toEntity(it) }
}
