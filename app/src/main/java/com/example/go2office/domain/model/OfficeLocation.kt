package com.example.go2office.domain.model

/**
 * Domain model for office location configuration.
 */
data class OfficeLocation(
    val latitude: Double,
    val longitude: Double,
    val radiusMeters: Float = 100f, // Default 100 meters
    val name: String = "Office"
)

/**
 * Domain model for tracking office presence.
 */
data class OfficePresence(
    val id: Long = 0,
    val locationId: Long = 0,
    val entryTime: String, // ISO-8601 format string
    val exitTime: String? = null, // ISO-8601 format string
    val totalHours: Float = 0f
)

