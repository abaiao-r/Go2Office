package com.example.go2office.domain.usecase

import com.example.go2office.domain.model.MonthProgress
import com.example.go2office.domain.repository.OfficeRepository
import com.example.go2office.util.DateUtils
import java.time.YearMonth
import java.time.ZoneId
import javax.inject.Inject

/**
 * Use case to get progress for a specific month.
 * Calculates completed vs required days and hours.
 */
class GetMonthProgressUseCase @Inject constructor(
    private val repository: OfficeRepository,
    private val calculateRequirements: CalculateMonthlyRequirementsUseCase
) {

    suspend operator fun invoke(
        yearMonth: YearMonth,
        zoneId: ZoneId = ZoneId.systemDefault()
    ): Result<MonthProgress> {
        return try {
            // Calculate required days and hours for the month
            val requirementsResult = calculateRequirements(yearMonth, zoneId)
            if (requirementsResult.isFailure) {
                return Result.failure(requirementsResult.exceptionOrNull()!!)
            }

            val requirements = requirementsResult.getOrThrow()

            // Get all daily entries for the month
            val (startDate, endDate) = DateUtils.getMonthBounds(yearMonth)
            val entries = repository.getDailyEntriesInRangeOnce(startDate, endDate)

            // Count completed office days and sum hours
            val completedDays = entries.count { it.wasInOffice }
            val completedHours = entries
                .filter { it.wasInOffice }
                .sumOf { it.hoursWorked.toDouble() }
                .toFloat()

            val progress = MonthProgress(
                yearMonth = yearMonth,
                requiredDays = requirements.requiredDays,
                completedDays = completedDays,
                requiredHours = requirements.requiredHours,
                completedHours = completedHours
            )

            Result.success(progress)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

