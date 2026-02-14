package com.example.go2office.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.DayOfWeek
import java.time.Instant

/**
 * Room entity for storing user office settings.
 */
@Entity(tableName = "office_settings")
data class OfficeSettingsEntity(
    @PrimaryKey
    val id: Int = 1, // Single row table
    val requiredDaysPerWeek: Int,
    val requiredHoursPerWeek: Float,
    val weekdayPreferences: List<DayOfWeek>, // Ordered list: Mon-Fri in user's preference
    val createdAt: Instant,
    val updatedAt: Instant
)

