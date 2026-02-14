package com.example.go2office.data.mapper
import com.example.go2office.data.local.entities.OfficePresenceEntity
import com.example.go2office.domain.model.OfficePresence
import java.time.Duration
import java.time.LocalDateTime
object OfficePresenceMapper {
    fun toDomain(entity: OfficePresenceEntity): OfficePresence {
        val exitTime = entity.exitTime ?: LocalDateTime.now()
        val duration = Duration.between(entity.entryTime, exitTime)
        val totalHours = duration.toMinutes() / 60f
        return OfficePresence(
            id = entity.id,
            locationId = 0L, 
            entryTime = entity.entryTime.toString(),
            exitTime = entity.exitTime?.toString(),
            totalHours = totalHours
        )
    }
    fun toEntity(domain: OfficePresence): OfficePresenceEntity {
        return OfficePresenceEntity(
            id = domain.id,
            entryTime = LocalDateTime.parse(domain.entryTime),
            exitTime = domain.exitTime?.let { LocalDateTime.parse(it) },
            isAutoDetected = true,
            confidence = 1.0f,
            createdAt = java.time.Instant.now()
        )
    }
}
