package com.example.go2office.data.mapper
import com.example.go2office.data.local.entities.HolidayEntity
import com.example.go2office.domain.model.Holiday
import com.example.go2office.domain.model.HolidayType
import java.time.Instant
object HolidayMapper {
    fun toDomain(entity: HolidayEntity): Holiday {
        return Holiday(
            id = entity.id,
            date = entity.date,
            description = entity.description,
            type = when (entity.type) {
                "VACATION" -> HolidayType.VACATION
                else -> HolidayType.PUBLIC_HOLIDAY
            }
        )
    }
    fun toEntity(domain: Holiday): HolidayEntity {
        return HolidayEntity(
            id = domain.id,
            date = domain.date,
            description = domain.description,
            type = when (domain.type) {
                HolidayType.VACATION -> "VACATION"
                HolidayType.PUBLIC_HOLIDAY -> "PUBLIC_HOLIDAY"
            },
            createdAt = Instant.now()
        )
    }
    fun toDomainList(entities: List<HolidayEntity>): List<Holiday> {
        return entities.map { toDomain(it) }
    }
}
