package com.example.go2office.data.local.entities
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Index
import java.time.Instant
import java.time.LocalDateTime
@Entity(tableName = "office_locations")
data class OfficeLocationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val latitude: Double,
    val longitude: Double,
    val radiusMeters: Float,
    val name: String,
    val isActive: Boolean = true,
    val createdAt: Instant
)
@Entity(
    tableName = "office_presence",
    indices = [Index(value = ["entryTime"], unique = false)]
)
data class OfficePresenceEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val entryTime: LocalDateTime,
    val exitTime: LocalDateTime? = null,
    val isAutoDetected: Boolean,
    val confidence: Float,
    val createdAt: Instant
)
