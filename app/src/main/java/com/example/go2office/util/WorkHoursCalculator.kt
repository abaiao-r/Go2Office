package com.example.go2office.util

import com.example.go2office.util.Constants.MAX_DAILY_HOURS
import com.example.go2office.util.Constants.WORK_END_HOUR
import com.example.go2office.util.Constants.WORK_START_HOUR
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime

/**
 * Utility functions for calculating work hours with constraints.
 *
 * Rules:
 * - Only time between 7 AM and 7 PM counts
 * - Maximum 10 hours per day
 * - Multiple sessions are summed
 */
object WorkHoursCalculator {

    /**
     * Calculate work hours for a single session.
     * Only counts time between 7 AM and 7 PM.
     *
     * @param entryTime When the user arrived
     * @param exitTime When the user left (null = still at office, uses current time)
     * @return Hours worked within the work window
     */
    fun calculateSessionHours(
        entryTime: LocalDateTime,
        exitTime: LocalDateTime? = null
    ): Float {
        val actualExitTime = exitTime ?: LocalDateTime.now()

        // Define work hours window
        val workStart = LocalTime.of(WORK_START_HOUR, 0)  // 7:00 AM
        val workEnd = LocalTime.of(WORK_END_HOUR, 0)      // 7:00 PM

        // Adjust entry time if before work hours
        var effectiveEntry = entryTime
        if (effectiveEntry.toLocalTime().isBefore(workStart)) {
            effectiveEntry = LocalDateTime.of(effectiveEntry.toLocalDate(), workStart)
        }

        // Adjust exit time if after work hours
        var effectiveExit = actualExitTime
        if (effectiveExit.toLocalTime().isAfter(workEnd)) {
            effectiveExit = LocalDateTime.of(effectiveExit.toLocalDate(), workEnd)
        }

        // If session is completely outside work hours, return 0
        if (effectiveEntry.toLocalTime().isAfter(workEnd) ||
            effectiveExit.toLocalTime().isBefore(workStart)) {
            return 0f
        }

        // If exit is before entry (shouldn't happen, but safety check)
        if (effectiveExit.isBefore(effectiveEntry)) {
            return 0f
        }

        // Calculate duration in hours
        val duration = Duration.between(effectiveEntry, effectiveExit)
        val hours = duration.toMinutes() / 60f

        return hours.coerceAtLeast(0f)
    }

    /**
     * Calculate total work hours for multiple sessions in a day.
     * Applies the daily cap of 10 hours maximum.
     *
     * @param sessions List of (entryTime, exitTime) pairs
     * @return Total hours worked, capped at MAX_DAILY_HOURS
     */
    fun calculateDailyHours(
        sessions: List<Pair<LocalDateTime, LocalDateTime?>>
    ): Float {
        val totalHours = sessions.sumOf { (entry, exit) ->
            calculateSessionHours(entry, exit).toDouble()
        }.toFloat()

        return totalHours.coerceAtMost(MAX_DAILY_HOURS)
    }

    /**
     * Format hours as a readable string.
     * Examples: "8.0h", "8.5h", "10.0h (capped)"
     */
    fun formatHours(hours: Float, isCapped: Boolean = false): String {
        val formatted = "%.1fh".format(hours)
        return if (isCapped) "$formatted (capped)" else formatted
    }

    /**
     * Check if a time is within work hours.
     */
    fun isWithinWorkHours(time: LocalDateTime): Boolean {
        val localTime = time.toLocalTime()
        val workStart = LocalTime.of(WORK_START_HOUR, 0)
        val workEnd = LocalTime.of(WORK_END_HOUR, 0)

        return !localTime.isBefore(workStart) && !localTime.isAfter(workEnd)
    }

    /**
     * Get the work hours window for display.
     */
    fun getWorkHoursWindow(): String {
        return "${WORK_START_HOUR}:00 AM - ${WORK_END_HOUR % 12}:00 PM"
    }

    /**
     * Check if daily hours would be capped.
     */
    fun wouldBeCapped(hours: Float): Boolean {
        return hours > MAX_DAILY_HOURS
    }
}

