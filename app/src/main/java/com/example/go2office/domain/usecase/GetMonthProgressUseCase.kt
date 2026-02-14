package com.example.go2office.domain.usecase
import com.example.go2office.domain.model.MonthProgress
import com.example.go2office.domain.repository.OfficeRepository
import com.example.go2office.util.DateUtils
import java.time.YearMonth
import java.time.ZoneId
import javax.inject.Inject
class GetMonthProgressUseCase @Inject constructor(
    private val repository: OfficeRepository,
    private val calculateRequirements: CalculateMonthlyRequirementsUseCase
) {
    suspend operator fun invoke(
        yearMonth: YearMonth,
        zoneId: ZoneId = ZoneId.systemDefault()
    ): Result<MonthProgress> {
        return try {
            val requirementsResult = calculateRequirements(yearMonth, zoneId)
            if (requirementsResult.isFailure) {
                return Result.failure(requirementsResult.exceptionOrNull()!!)
            }
            val requirements = requirementsResult.getOrThrow()
            val (startDate, endDate) = DateUtils.getMonthBounds(yearMonth)
            val entries = repository.getDailyEntriesInRangeOnce(startDate, endDate)
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
