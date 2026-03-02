package com.example.go2office.domain.usecase
import com.example.go2office.data.local.dao.DailyEntryDao
import com.example.go2office.data.local.dao.OfficePresenceDao
import com.example.go2office.data.mapper.DailyEntryMapper
import com.example.go2office.domain.model.DailyEntry
import com.example.go2office.util.Constants
import com.example.go2office.util.WorkHoursCalculator
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject
class AggregateSessionsToDailyUseCase @Inject constructor(
    private val officePresenceDao: OfficePresenceDao,
    private val dailyEntryDao: DailyEntryDao
) {
    suspend operator fun invoke(date: LocalDate): Result<Unit> {
        return try {
            val sessions = officePresenceDao.getSessionsForDate(date.toString())

            if (sessions.isEmpty()) {
                val entry = DailyEntry(
                    date = date,
                    wasInOffice = false,
                    hoursWorked = 0f,
                    notes = null
                )
                dailyEntryDao.insert(DailyEntryMapper.toEntity(entry))
                return Result.success(Unit)
            }

            val entries = sessions.map { it.entryTime }
            val exits = sessions.map { it.exitTime }

            val result = WorkHoursCalculator.calculateDailyTimeFromSessions(entries, exits)

            val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
            val notes = buildString {
                append("Auto-detected (${sessions.size} session(s))")
                if (result.adjustedFirstEntry != null && result.adjustedLastExit != null) {
                    append("\nFirst entry: ${result.adjustedFirstEntry.format(timeFormatter)}")
                    append(", Last exit: ${result.adjustedLastExit.format(timeFormatter)}")
                }
                if (result.isCapped) {
                    append("\nCapped at ${Constants.MAX_DAILY_HOURS.toInt()}h")
                }
            }

            val entry = DailyEntry(
                date = date,
                wasInOffice = result.countsAsDay,
                hoursWorked = result.workedHours,
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
