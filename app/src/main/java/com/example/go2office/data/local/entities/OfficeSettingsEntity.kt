package com.example.go2office.data.local.entities
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.DayOfWeek
import java.time.Instant
@Entity(tableName = "office_settings")
data class OfficeSettingsEntity(
    @PrimaryKey
    val id: Int = 1, 
    val requiredDaysPerWeek: Int,
    val requiredHoursPerWeek: Float,
    val weekdayPreferences: List<DayOfWeek>, 
    val createdAt: Instant,
    val updatedAt: Instant
)
