package com.example.go2office.domain.usecase
import com.example.go2office.domain.model.SuggestedDay
import com.example.go2office.domain.repository.OfficeRepository
import com.example.go2office.util.DateUtils
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import javax.inject.Inject
class GetSuggestedOfficeDaysUseCase @Inject constructor(
    private val repository: OfficeRepository,
    private val getMonthProgress: GetMonthProgressUseCase
) {
    companion object {
        private const val AVERAGE_HOURS_PER_DAY = 8f
    }
    suspend operator fun invoke(
        yearMonth: YearMonth,
        count: Int = 20,  
        zoneId: ZoneId = ZoneId.systemDefault()
    ): Result<List<SuggestedDay>> {
        return try {
            val settings = repository.getSettingsOnce()
                ?: return Result.failure(Exception("Settings not configured"))
            val progressResult = getMonthProgress(yearMonth, zoneId)
            if (progressResult.isFailure) {
                return Result.failure(progressResult.exceptionOrNull()!!)
            }
            val progress = progressResult.getOrThrow()
            val remainingDays = progress.remainingDays
            val remainingHours = progress.remainingHours
            val daysNeededForHours = if (remainingHours > 0) {
                kotlin.math.ceil(remainingHours / AVERAGE_HOURS_PER_DAY).toInt()
            } else {
                0
            }
            val totalDaysNeeded = maxOf(remainingDays, daysNeededForHours)
            if (totalDaysNeeded <= 0) {
                return Result.success(emptyList())
            }
            val (startDate, endDate) = DateUtils.getMonthBounds(yearMonth)
            val existingEntries = repository.getOfficeDaysInRange(startDate, endDate)
            val markedDates = existingEntries.map { it.date }.toSet()
            val holidays = repository.getHolidaysInRangeOnce(startDate, endDate)
            val holidayDates = holidays
                .map { it.date }
                .filter { !DateUtils.isWeekend(it) }  
                .toSet()
            val today = DateUtils.getCurrentDate(zoneId)
            val fromDate = if (today.isAfter(startDate)) today else startDate
            val availableDates = DateUtils.getWorkingDaysInRange(fromDate, endDate)
                .filter { date ->
                    date !in markedDates &&
                    date !in holidayDates &&
                    !DateUtils.isPast(date, zoneId)
                }
            if (availableDates.isEmpty()) {
                return Result.success(emptyList())
            }
            val datesByWeek = availableDates.groupBy {
                java.time.temporal.WeekFields.ISO.weekOfWeekBasedYear().getFrom(it)
            }.toSortedMap()
            val weeklyRequirement = settings.requiredDaysPerWeek
            val suggestions = mutableListOf<SuggestedDay>()
            var stillNeeded = totalDaysNeeded  
            for ((weekNum, datesInWeek) in datesByWeek) {
                if (stillNeeded <= 0) break
                val weeksRemaining = datesByWeek.keys.filter { it >= weekNum }.size
                val daysForThisWeek = if (weeksRemaining > 0) {
                    val idealPerWeek = kotlin.math.ceil(stillNeeded.toDouble() / weeksRemaining).toInt()
                    val canSpread = stillNeeded <= (weeklyRequirement * weeksRemaining)
                    when {
                        weeksRemaining == 1 -> stillNeeded
                        canSpread && idealPerWeek <= weeklyRequirement -> idealPerWeek
                        else -> idealPerWeek
                    }
                } else {
                    stillNeeded
                }.coerceAtMost(datesInWeek.size)  
                val weekSuggestions = selectBestDaysFromWeek(
                    datesInWeek = datesInWeek,
                    count = daysForThisWeek,
                    preferences = settings.weekdayPreferences,
                    stillNeeded = stillNeeded,
                    remainingHours = remainingHours,
                    daysNeededForHours = daysNeededForHours
                )
                suggestions.addAll(weekSuggestions)
                stillNeeded -= weekSuggestions.size
            }
            val chronologicalSuggestions = suggestions.sortedBy { it.date }
            if (chronologicalSuggestions.size < totalDaysNeeded) {
            }
            Result.success(chronologicalSuggestions)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    private fun selectBestDaysFromWeek(
        datesInWeek: List<LocalDate>,
        count: Int,
        preferences: List<DayOfWeek>,
        stillNeeded: Int,
        remainingHours: Float,
        daysNeededForHours: Int
    ): List<SuggestedDay> {
        val preferenceMap = preferences.mapIndexed { index, day -> day to index }.toMap()
        val scoredDates = datesInWeek.map { date ->
            val dayOfWeek = date.dayOfWeek
            val priority = preferenceMap[dayOfWeek] ?: 999
            val reason = buildReason(
                dayOfWeek = dayOfWeek,
                priority = priority,
                preferences = preferences,
                stillNeeded = stillNeeded,
                remainingHours = remainingHours,
                daysNeededForHours = daysNeededForHours
            )
            SuggestedDay(
                date = date,
                dayOfWeek = dayOfWeek,
                reason = reason,
                priority = priority
            )
        }
        return scoredDates
            .sortedWith(compareBy({ it.priority }, { it.date }))
            .take(count)
            .sortedBy { it.date }  
    }
    private fun buildReason(
        dayOfWeek: DayOfWeek,
        priority: Int,
        preferences: List<DayOfWeek>,
        stillNeeded: Int,
        remainingHours: Float,
        daysNeededForHours: Int
    ): String {
        val dayName = dayOfWeek.name.lowercase().replaceFirstChar { it.uppercase() }
        val preferenceLabel = when (priority) {
            0 -> "Top preference"
            in 1..2 -> "Preferred"
            else -> "Available"
        }
        val needInfo = when {
            daysNeededForHours > 0 && remainingHours > 0 -> {
                "${remainingHours.toInt()}h remaining (~$daysNeededForHours days)"
            }
            stillNeeded > 0 -> {
                "$stillNeeded days remaining"
            }
            else -> "Available"
        }
        return "$preferenceLabel ($dayName) • $needInfo"
    }
}
