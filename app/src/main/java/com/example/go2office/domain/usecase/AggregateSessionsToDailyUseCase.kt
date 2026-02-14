package com.example.go2office.domain.usecase
import com.example.go2office.data.local.dao.DailyEntryDao
import com.example.go2office.data.local.dao.OfficePresenceDao
import com.example.go2office.data.mapper.DailyEntryMapper
import com.example.go2office.domain.model.DailyEntry
import com.example.go2office.util.Constants
import com.example.go2office.util.WorkHoursCalculator
import java.time.LocalDate
import javax.inject.Inject
class AggregateSessionsToDailyUseCase @Inject constructor(
    private val officePresenceDao: OfficePresenceDao,
    private val dailyEntryDao: DailyEntryDao
) {
    suspend operator fun invoke(date: LocalDate): Result<Unit> {
        return try {
            val sessions = officePresenceDao.getSessionsForDate(date.toString())
            val completedSessions = sessions.filter { it.exitTime != null }
            if (completedSessions.isEmpty()) {
                val entry = DailyEntry(
                    date = date,
                    wasInOffice = false,
                    hoursWorked = 0f,
                    notes = null
                )
                dailyEntryDao.insert(DailyEntryMapper.toEntity(entry))
                return Result.success(Unit)
            }
            val sessionPairs = completedSessions.map { session ->
                session.entryTime to session.exitTime
            }
            val totalHours = WorkHoursCalculator.calculateDailyHours(sessionPairs)
            val rawHours = completedSessions.sumOf { session ->
                WorkHoursCalculator.calculateSessionHours(session.entryTime, session.exitTime!!).toDouble()
            }.toFloat()
            val isCapped = WorkHoursCalculator.wouldBeCapped(rawHours)
            val notes = buildString {
                append("Auto-detected (${completedSessions.size} session(s))")
                if (isCapped) {
                    append("\nRaw: ${"%.1f".format(rawHours)}h, Counted: ${"%.1f".format(totalHours)}h (capped at ${Constants.MAX_DAILY_HOURS.toInt()}h)")
                }
            }
            val entry = DailyEntry(
                date = date,
                wasInOffice = true,
                hoursWorked = totalHours,
                notes = notes
            )
            val existing = dailyEntryDao.getByDate(date)
            if (existing != null) {
                val updated = DailyEntryMapper.toEntity(entry).copy(id = existing.id)
                dailyEntryDao.update(updated)
            } else {
                dailyEntryDao.insert(DailyEntryMapper.toEntity(entry))
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
