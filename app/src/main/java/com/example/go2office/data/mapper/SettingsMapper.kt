package com.example.go2office.data.mapper
import com.example.go2office.data.local.entities.OfficeSettingsEntity
import com.example.go2office.domain.model.OfficeSettings
import java.time.Instant
object SettingsMapper {
    fun toDomain(entity: OfficeSettingsEntity): OfficeSettings {
        return OfficeSettings(
            requiredDaysPerWeek = entity.requiredDaysPerWeek,
            requiredHoursPerWeek = entity.requiredHoursPerWeek,
            weekdayPreferences = entity.weekdayPreferences
        )
    }
    fun toEntity(domain: OfficeSettings): OfficeSettingsEntity {
        val now = Instant.now()
        return OfficeSettingsEntity(
            id = 1, 
            requiredDaysPerWeek = domain.requiredDaysPerWeek,
            requiredHoursPerWeek = domain.requiredHoursPerWeek,
            weekdayPreferences = domain.weekdayPreferences,
            createdAt = now,
            updatedAt = now
        )
    }
    fun toEntityUpdate(domain: OfficeSettings, existingEntity: OfficeSettingsEntity): OfficeSettingsEntity {
        return existingEntity.copy(
            requiredDaysPerWeek = domain.requiredDaysPerWeek,
            requiredHoursPerWeek = domain.requiredHoursPerWeek,
            weekdayPreferences = domain.weekdayPreferences,
            updatedAt = Instant.now()
        )
    }
}
