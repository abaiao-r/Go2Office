package com.example.go2office.util
import com.example.go2office.util.Constants.MAX_DAILY_HOURS
import com.example.go2office.util.Constants.WORK_END_HOUR
import com.example.go2office.util.Constants.WORK_START_HOUR
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime
object WorkHoursCalculator {
    fun calculateSessionHours(
        entryTime: LocalDateTime,
        exitTime: LocalDateTime? = null
    ): Float {
        val actualExitTime = exitTime ?: LocalDateTime.now()
        val workStart = LocalTime.of(WORK_START_HOUR, 0)  
        val workEnd = LocalTime.of(WORK_END_HOUR, 0)      
        var effectiveEntry = entryTime
        if (effectiveEntry.toLocalTime().isBefore(workStart)) {
            effectiveEntry = LocalDateTime.of(effectiveEntry.toLocalDate(), workStart)
        }
        var effectiveExit = actualExitTime
        if (effectiveExit.toLocalTime().isAfter(workEnd)) {
            effectiveExit = LocalDateTime.of(effectiveExit.toLocalDate(), workEnd)
        }
        if (effectiveEntry.toLocalTime().isAfter(workEnd) ||
            effectiveExit.toLocalTime().isBefore(workStart)) {
            return 0f
        }
        if (effectiveExit.isBefore(effectiveEntry)) {
            return 0f
        }
        val duration = Duration.between(effectiveEntry, effectiveExit)
        val hours = duration.toMinutes() / 60f
        return hours.coerceAtLeast(0f)
    }
    fun calculateDailyHours(
        sessions: List<Pair<LocalDateTime, LocalDateTime?>>
    ): Float {
        val totalHours = sessions.sumOf { (entry, exit) ->
            calculateSessionHours(entry, exit).toDouble()
        }.toFloat()
        return totalHours.coerceAtMost(MAX_DAILY_HOURS)
    }
    fun formatHours(hours: Float, isCapped: Boolean = false): String {
        val formatted = formatHoursMinutes(hours)
        return if (isCapped) "$formatted (capped)" else formatted
    }

    fun formatHoursMinutes(hours: Float): String {
        val totalMinutes = (hours * 60).toInt()
        val h = totalMinutes / 60
        val m = totalMinutes % 60
        return if (m == 0) "${h}h" else "${h}h ${m}m"
    }
    fun isWithinWorkHours(time: LocalDateTime): Boolean {
        val localTime = time.toLocalTime()
        val workStart = LocalTime.of(WORK_START_HOUR, 0)
        val workEnd = LocalTime.of(WORK_END_HOUR, 0)
        return !localTime.isBefore(workStart) && !localTime.isAfter(workEnd)
    }
    fun getWorkHoursWindow(): String {
        return "${WORK_START_HOUR}:00 AM - ${WORK_END_HOUR % 12}:00 PM"
    }
    fun wouldBeCapped(hours: Float): Boolean {
        return hours > MAX_DAILY_HOURS
    }
}
