package com.example.go2office.util
import com.example.go2office.util.Constants.MAX_DAILY_HOURS
import com.example.go2office.util.Constants.WORK_END_HOUR
import com.example.go2office.util.Constants.WORK_START_HOUR
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime

data class DailyTimeResult(
    val adjustedFirstEntry: LocalTime?,
    val adjustedLastExit: LocalTime?,
    val workedHours: Float,
    val countsAsDay: Boolean,
    val isCapped: Boolean = false
)

object WorkHoursCalculator {
    private val WORK_START = LocalTime.of(WORK_START_HOUR, 0)
    private val WORK_END = LocalTime.of(WORK_END_HOUR, 0)

    fun calculateDailyTimeFromSessions(
        entries: List<LocalDateTime>,
        exits: List<LocalDateTime?>
    ): DailyTimeResult {
        if (entries.isEmpty()) {
            return DailyTimeResult(null, null, 0f, countsAsDay = false)
        }

        val firstEntry = entries.minByOrNull { it }!!.toLocalTime()
        val completedExits = exits.filterNotNull()
        val lastExit = if (completedExits.isEmpty()) null else completedExits.maxByOrNull { it }!!.toLocalTime()

        if (firstEntry.isBefore(WORK_START) && lastExit != null && lastExit.isBefore(WORK_START)) {
            return DailyTimeResult(null, null, 0f, countsAsDay = true)
        }

        val adjustedFirstEntry = when {
            firstEntry.isBefore(WORK_START) -> WORK_START
            firstEntry.isAfter(WORK_END) -> WORK_END
            else -> firstEntry
        }

        val adjustedLastExit = when {
            lastExit == null -> WORK_END
            lastExit.isBefore(WORK_START) -> WORK_START
            lastExit.isAfter(WORK_END) -> WORK_END
            else -> lastExit
        }

        val rawMinutes = Duration.between(adjustedFirstEntry, adjustedLastExit).toMinutes()
        val rawHours = (rawMinutes / 60f).coerceAtLeast(0f)
        val workedHours = rawHours.coerceAtMost(MAX_DAILY_HOURS)
        val isCapped = rawHours > MAX_DAILY_HOURS

        return DailyTimeResult(
            adjustedFirstEntry = adjustedFirstEntry,
            adjustedLastExit = adjustedLastExit,
            workedHours = workedHours,
            countsAsDay = true,
            isCapped = isCapped
        )
    }

    fun calculateDailyHours(
        sessions: List<Pair<LocalDateTime, LocalDateTime?>>
    ): Float {
        if (sessions.isEmpty()) return 0f

        val entries = sessions.map { it.first }
        val exits = sessions.map { it.second }

        return calculateDailyTimeFromSessions(entries, exits).workedHours
    }

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

    fun formatHoursMinutes(hours: Float): String {
        val totalMinutes = (hours * 60).toInt()
        val h = totalMinutes / 60
        val m = totalMinutes % 60
        return if (m == 0) "${h}h" else "${h}h ${m}m"
    }

    fun formatHours(hours: Float, isCapped: Boolean = false): String {
        val formatted = formatHoursMinutes(hours)
        return if (isCapped) "$formatted (capped)" else formatted
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
