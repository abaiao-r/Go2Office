package com.example.go2office.data.mapper

import com.example.go2office.data.local.entities.DailyEntryEntity
import com.example.go2office.domain.model.DailyEntry
import java.time.Instant

/**
 * Mapper for converting between DailyEntryEntity and DailyEntry domain model.
 */
object DailyEntryMapper {

    fun toDomain(entity: DailyEntryEntity): DailyEntry {
        return DailyEntry(
            id = entity.id,
            date = entity.date,
            wasInOffice = entity.wasInOffice,
            hoursWorked = entity.hoursWorked,
            notes = entity.notes
        )
    }

    fun toEntity(domain: DailyEntry): DailyEntryEntity {
        return DailyEntryEntity(
            id = domain.id,
            date = domain.date,
            wasInOffice = domain.wasInOffice,
            hoursWorked = domain.hoursWorked,
            notes = domain.notes,
            createdAt = Instant.now()
        )
    }

    fun toDomainList(entities: List<DailyEntryEntity>): List<DailyEntry> {
        return entities.map { toDomain(it) }
    }
}

