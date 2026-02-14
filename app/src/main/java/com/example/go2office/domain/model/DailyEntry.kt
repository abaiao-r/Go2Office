package com.example.go2office.domain.model

import java.time.LocalDate

/**
 * Domain model representing a daily office entry.
 */
data class DailyEntry(
    val id: Long = 0,
    val date: LocalDate,
    val wasInOffice: Boolean,
    val hoursWorked: Float,
    val notes: String? = null
) {
    init {
        require(hoursWorked >= 0 && hoursWorked <= 24) { "Hours worked must be between 0 and 24" }
    }
}

