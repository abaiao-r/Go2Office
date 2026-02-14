package com.example.go2office.domain.model
import java.time.YearMonth
data class MonthProgress(
    val yearMonth: YearMonth,
    val requiredDays: Int,
    val completedDays: Int,
    val requiredHours: Float,
    val completedHours: Float
) {
    val remainingDays: Int get() = (requiredDays - completedDays).coerceAtLeast(0)
    val remainingHours: Float get() = (requiredHours - completedHours).coerceAtLeast(0f)
    val daysPercentComplete: Float get() = if (requiredDays > 0) (completedDays.toFloat() / requiredDays * 100f).coerceAtMost(100f) else 0f
    val hoursPercentComplete: Float get() = if (requiredHours > 0) (completedHours / requiredHours * 100f).coerceAtMost(100f) else 0f
    val isComplete: Boolean get() = completedDays >= requiredDays && completedHours >= requiredHours
}
