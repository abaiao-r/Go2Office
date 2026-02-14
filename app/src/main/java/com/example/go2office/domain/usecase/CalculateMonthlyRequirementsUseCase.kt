package com.example.go2office.domain.usecase

import com.example.go2office.domain.model.MonthlyRequirements
import com.example.go2office.domain.repository.OfficeRepository
import com.example.go2office.util.DateUtils
import java.time.YearMonth
import java.time.ZoneId
import javax.inject.Inject
import kotlin.math.ceil

/**
 * Use case to calculate required days and hours for a given month.
 *
 * Formula:
 * 1. Count weekdays in month (Mon-Fri)
 * 2. EXCLUDE public holidays AND vacation days (ONLY if they fall on weekdays)
 * 3. requiredDays = ceil(availableWeekdays × (requiredDaysPerWeek / 5.0))
 * 4. requiredHours = requiredDays × hoursPerDay
 *
 * Example:
 * - February: 20 weekdays
 * - Public holidays: 2 (both on Mon-Fri) → exclude both
 * - Vacation days: 5 (3 on Mon-Fri, 2 on Sat-Sun) → exclude only 3
 * - Available: 20 - 2 - 3 = 15 weekdays
 * - Requirement: 3 days/week (60%)
 * - Required: 15 × 0.6 = 9 days, 72 hours
 *
 * Note: Holidays/vacations on weekends don't reduce requirements
 * since they're already non-working days.
 */
class CalculateMonthlyRequirementsUseCase @Inject constructor(
    private val repository: OfficeRepository
) {

    suspend operator fun invoke(
        yearMonth: YearMonth,
        zoneId: ZoneId = ZoneId.systemDefault()
    ): Result<MonthlyRequirements> {
        return try {
            val settings = repository.getSettingsOnce()
                ?: return Result.failure(Exception("Settings not configured"))

            // Get ALL holidays and vacations for the month
            val (startDate, endDate) = DateUtils.getMonthBounds(yearMonth)
            val holidaysAndVacations = repository.getHolidaysInRangeOnce(startDate, endDate)

            // ONLY exclude holidays/vacations that fall on weekdays (Mon-Fri)
            // If a holiday falls on Saturday/Sunday, it doesn't reduce available work days
            val excludeDates = holidaysAndVacations
                .map { it.date }
                .filter { !DateUtils.isWeekend(it) }  // Only exclude if NOT weekend

            // Count weekdays EXCLUDING holidays AND vacations (that fall on weekdays)
            val weekdaysInMonth = DateUtils.getWeekdaysInMonth(yearMonth, excludeDates)

            // Calculate required days: proportion of weekdays based on weekly requirement
            val requiredDaysRaw = weekdaysInMonth * (settings.requiredDaysPerWeek / 5.0)
            val requiredDays = ceil(requiredDaysRaw).toInt()

            // Calculate required hours: proportional to required days
            val hoursPerDay = settings.requiredHoursPerWeek / settings.requiredDaysPerWeek
            val requiredHours = requiredDays * hoursPerDay

            val requirements = MonthlyRequirements(
                yearMonth = yearMonth,
                requiredDays = requiredDays,
                requiredHours = requiredHours,
                totalWeekdaysInMonth = weekdaysInMonth,
                holidaysCount = holidaysAndVacations.size  // Total: public holidays + vacation days
            )

            Result.success(requirements)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

