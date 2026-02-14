package com.example.go2office.domain.model

import java.time.YearMonth

/**
 * Domain model representing calculated monthly requirements.
 */
data class MonthlyRequirements(
    val yearMonth: YearMonth,
    val requiredDays: Int,
    val requiredHours: Float,
    val totalWeekdaysInMonth: Int,
    val holidaysCount: Int
) {
    init {
        require(requiredDays >= 0) { "Required days cannot be negative" }
        require(requiredHours >= 0) { "Required hours cannot be negative" }
    }
}

