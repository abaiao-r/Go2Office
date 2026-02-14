package com.example.go2office.domain.usecase
import com.example.go2office.domain.model.MonthlyRequirements
import com.example.go2office.domain.repository.OfficeRepository
import com.example.go2office.util.DateUtils
import java.time.YearMonth
import java.time.ZoneId
import javax.inject.Inject
import kotlin.math.ceil
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
            val (startDate, endDate) = DateUtils.getMonthBounds(yearMonth)
            val holidaysAndVacations = repository.getHolidaysInRangeOnce(startDate, endDate)
            val excludeDates = holidaysAndVacations
                .map { it.date }
                .filter { !DateUtils.isWeekend(it) }  
            val weekdaysInMonth = DateUtils.getWeekdaysInMonth(yearMonth, excludeDates)
            val requiredDaysRaw = weekdaysInMonth * (settings.requiredDaysPerWeek / 5.0)
            val requiredDays = ceil(requiredDaysRaw).toInt()
            val hoursPerDay = settings.requiredHoursPerWeek / settings.requiredDaysPerWeek
            val requiredHours = requiredDays * hoursPerDay
            val requirements = MonthlyRequirements(
                yearMonth = yearMonth,
                requiredDays = requiredDays,
                requiredHours = requiredHours,
                totalWeekdaysInMonth = weekdaysInMonth,
                holidaysCount = holidaysAndVacations.size  
            )
            Result.success(requirements)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
