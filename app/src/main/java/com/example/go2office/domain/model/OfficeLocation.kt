package com.example.go2office.domain.model
data class OfficeLocation(
    val latitude: Double,
    val longitude: Double,
    val radiusMeters: Float = 100f, 
    val name: String = "Office"
)
data class OfficePresence(
    val id: Long = 0,
    val locationId: Long = 0,
    val entryTime: String, 
    val exitTime: String? = null, 
    val totalHours: Float = 0f
)
