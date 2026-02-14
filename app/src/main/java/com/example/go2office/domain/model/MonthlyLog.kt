package com.example.go2office.domain.model

import java.time.YearMonth

/**
 * Domain model representing a monthly log summary.
 */
data class MonthlyLog(
    val id: Long = 0,
    val yearMonth: YearMonth,
    val requiredDays: Int,
    val requiredHours: Float,
    val completedDays: Int,
    val completedHours: Float
)

