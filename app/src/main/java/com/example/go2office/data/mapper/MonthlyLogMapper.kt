package com.example.go2office.data.mapper

import com.example.go2office.data.local.entities.MonthlyLogEntity
import com.example.go2office.domain.model.MonthlyLog
import java.time.Instant

/**
 * Mapper for converting between MonthlyLogEntity and MonthlyLog domain model.
 */
object MonthlyLogMapper {

    fun toDomain(entity: MonthlyLogEntity): MonthlyLog {
        return MonthlyLog(
            id = entity.id,
            yearMonth = entity.yearMonth,
            requiredDays = entity.requiredDays,
            requiredHours = entity.requiredHours,
            completedDays = entity.completedDays,
            completedHours = entity.completedHours
        )
    }

    fun toEntity(domain: MonthlyLog): MonthlyLogEntity {
        return MonthlyLogEntity(
            id = domain.id,
            yearMonth = domain.yearMonth,
            requiredDays = domain.requiredDays,
            requiredHours = domain.requiredHours,
            completedDays = domain.completedDays,
            completedHours = domain.completedHours,
            createdAt = Instant.now()
        )
    }

    fun toDomainList(entities: List<MonthlyLogEntity>): List<MonthlyLog> {
        return entities.map { toDomain(it) }
    }
}

