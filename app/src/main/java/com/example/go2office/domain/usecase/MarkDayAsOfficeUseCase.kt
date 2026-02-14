package com.example.go2office.domain.usecase
import com.example.go2office.domain.model.DailyEntry
import com.example.go2office.domain.repository.OfficeRepository
import com.example.go2office.util.DateUtils
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject
class MarkDayAsOfficeUseCase @Inject constructor(
    private val repository: OfficeRepository
) {
    suspend operator fun invoke(
        date: LocalDate,
        hoursWorked: Float,
        notes: String? = null,
        zoneId: ZoneId = ZoneId.systemDefault()
    ): Result<Unit> {
        return try {
            if (hoursWorked < 0 || hoursWorked > 24) {
                return Result.failure(IllegalArgumentException("Hours must be between 0 and 24"))
            }
            if (DateUtils.isFuture(date, zoneId)) {
                return Result.failure(IllegalArgumentException("Cannot mark future dates"))
            }
            val entry = DailyEntry(
                date = date,
                wasInOffice = true,
                hoursWorked = hoursWorked,
                notes = notes
            )
            repository.saveDailyEntry(entry)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
