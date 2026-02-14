package com.example.go2office.domain.usecase

import com.example.go2office.domain.model.SuggestedDay
import com.example.go2office.domain.repository.OfficeRepository
import com.example.go2office.util.DateUtils
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import javax.inject.Inject

/**
 * Use case to suggest next office days based on user preferences and current progress.
 *
 * Algorithm (IMPROVED - Chronological + Fair Distribution):
 * 1. Get remaining required days for the month
 * 2. Distribute fairly across remaining weeks (e.g., 3 days/week requirement → suggest 3 per week)
 * 3. Get user's weekday preference order
 * 4. For each week, pick top preference days (respecting user's order)
 * 5. ONLY suggest extra days if impossible to meet requirements otherwise
 * 6. Return suggestions in CHRONOLOGICAL order (earliest first)
 *
 * Example: Need 9 days, 3 weeks left, preference Mon>Tue>Wed>Thu>Fri
 * - Week 1: Mon, Tue, Wed (3 days)
 * - Week 2: Mon, Tue, Wed (3 days)
 * - Week 3: Mon, Tue, Wed (3 days)
 * Total: 9 days, evenly distributed, respecting preferences
 */
class GetSuggestedOfficeDaysUseCase @Inject constructor(
    private val repository: OfficeRepository,
    private val getMonthProgress: GetMonthProgressUseCase
) {

    companion object {
        private const val AVERAGE_HOURS_PER_DAY = 8f
    }

    suspend operator fun invoke(
        yearMonth: YearMonth,
        count: Int = 20,  // Request more to ensure we get all needed days
        zoneId: ZoneId = ZoneId.systemDefault()
    ): Result<List<SuggestedDay>> {
        return try {
            val settings = repository.getSettingsOnce()
                ?: return Result.failure(Exception("Settings not configured"))

            // Get current progress
            val progressResult = getMonthProgress(yearMonth, zoneId)
            if (progressResult.isFailure) {
                return Result.failure(progressResult.exceptionOrNull()!!)
            }

            val progress = progressResult.getOrThrow()
            val remainingDays = progress.remainingDays
            val remainingHours = progress.remainingHours

            // Calculate days needed based on BOTH days AND hours constraints
            // Example: Completed 8 days but only 40h (need 64h)
            // remainingDays = 0, but remainingHours = 24h → need 3 more days!
            val daysNeededForHours = if (remainingHours > 0) {
                kotlin.math.ceil(remainingHours / AVERAGE_HOURS_PER_DAY).toInt()
            } else {
                0
            }

            // Take MAXIMUM to ensure BOTH constraints are met
            val totalDaysNeeded = maxOf(remainingDays, daysNeededForHours)

            if (totalDaysNeeded <= 0) {
                return Result.success(emptyList())
            }

            // IMPORTANT: We MUST suggest ALL totalDaysNeeded days
            // Example: If user needs 7 days, we suggest ALL 7 days, not just 5!

            // Get existing office days and holidays
            val (startDate, endDate) = DateUtils.getMonthBounds(yearMonth)
            val existingEntries = repository.getOfficeDaysInRange(startDate, endDate)
            val markedDates = existingEntries.map { it.date }.toSet()

            val holidays = repository.getHolidaysInRangeOnce(startDate, endDate)
            val holidayDates = holidays
                .map { it.date }
                .filter { !DateUtils.isWeekend(it) }  // Only exclude weekday holidays
                .toSet()

            // Get all available weekdays from today until end of month
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

            // Group available dates by week
            val datesByWeek = availableDates.groupBy {
                java.time.temporal.WeekFields.ISO.weekOfWeekBasedYear().getFrom(it)
            }.toSortedMap()

            val weeklyRequirement = settings.requiredDaysPerWeek
            val suggestions = mutableListOf<SuggestedDay>()
            var stillNeeded = totalDaysNeeded  // Use totalDaysNeeded (considers both days and hours)

            // Distribute days across weeks fairly
            for ((weekNum, datesInWeek) in datesByWeek) {
                if (stillNeeded <= 0) break

                // Calculate how many days to suggest this week
                val weeksRemaining = datesByWeek.keys.filter { it >= weekNum }.size

                // IMPORTANT: We MUST suggest enough days to complete totalDaysNeeded
                // Calculate minimum needed this week to finish on time
                val daysForThisWeek = if (weeksRemaining > 0) {
                    // Calculate fair distribution across remaining weeks
                    val idealPerWeek = kotlin.math.ceil(stillNeeded.toDouble() / weeksRemaining).toInt()

                    // Always suggest at least idealPerWeek to ensure we complete
                    // Only respect weekly requirement if we have plenty of weeks left
                    val canSpread = stillNeeded <= (weeklyRequirement * weeksRemaining)

                    when {
                        // Last week or only option: suggest everything needed
                        weeksRemaining == 1 -> stillNeeded
                        // Can spread comfortably: respect weekly requirement
                        canSpread && idealPerWeek <= weeklyRequirement -> idealPerWeek
                        // Need to catch up: suggest more than weekly requirement
                        else -> idealPerWeek
                    }
                } else {
                    stillNeeded
                }.coerceAtMost(datesInWeek.size)  // Can't suggest more than available

                // Pick best days from this week based on preferences
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

            // Sort chronologically (earliest first)
            val chronologicalSuggestions = suggestions.sortedBy { it.date }

            // SAFETY CHECK: Ensure we suggest AT LEAST totalDaysNeeded days
            // If user needs 7 days, we MUST suggest 7 days (if available)
            if (chronologicalSuggestions.size < totalDaysNeeded) {
                // Log warning: Not enough available days to meet requirements
                // This can happen if month is almost over or too many holidays
                // In this case, we return all available days
            }

            Result.success(chronologicalSuggestions)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Select best days from a week based on user preferences.
     * Returns days in chronological order.
     */
    private fun selectBestDaysFromWeek(
        datesInWeek: List<LocalDate>,
        count: Int,
        preferences: List<DayOfWeek>,
        stillNeeded: Int,
        remainingHours: Float,
        daysNeededForHours: Int
    ): List<SuggestedDay> {
        // Create preference score map (lower = better)
        val preferenceMap = preferences.mapIndexed { index, day -> day to index }.toMap()

        // Score and sort dates by preference, then by date
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

        // Sort by preference FIRST, then by date (chronological within same preference)
        return scoredDates
            .sortedWith(compareBy({ it.priority }, { it.date }))
            .take(count)
            .sortedBy { it.date }  // Final sort: chronological
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

        // Build reason considering both days and hours
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

