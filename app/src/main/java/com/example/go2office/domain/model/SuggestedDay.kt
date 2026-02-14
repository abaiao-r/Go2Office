package com.example.go2office.domain.model

import java.time.DayOfWeek
import java.time.LocalDate

/**
 * Domain model representing a suggested office day.
 */
data class SuggestedDay(
    val date: LocalDate,
    val dayOfWeek: DayOfWeek,
    val reason: String,
    val priority: Int // Lower number = higher priority
)

