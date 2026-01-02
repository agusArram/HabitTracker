package com.example.habittracker.data.mapper

import com.example.habittracker.data.entity.DailyLog
import com.example.habittracker.domain.model.DailyLogDomain

object DailyLogMapper {
    fun toDomain(entity: DailyLog): DailyLogDomain = DailyLogDomain(
        id = entity.id,
        habitId = entity.habitId,
        date = entity.date,
        completed = entity.completed
    )

    fun toEntity(domain: DailyLogDomain): DailyLog = DailyLog(
        id = domain.id,
        habitId = domain.habitId,
        date = domain.date,
        completed = domain.completed
    )

    fun toDomainList(entities: List<DailyLog>): List<DailyLogDomain> =
        entities.map { toDomain(it) }

    fun toEntityList(domains: List<DailyLogDomain>): List<DailyLog> =
        domains.map { toEntity(it) }
}
