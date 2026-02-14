package com.example.go2office.domain.usecase

import com.example.go2office.domain.model.DailyEntry
import com.example.go2office.domain.repository.OfficeRepository
import java.time.LocalDate
import javax.inject.Inject

/**
 * Use case to update hours for a daily entry.
 */
class UpdateDailyHoursUseCase @Inject constructor(
    private val repository: OfficeRepository
) {

    suspend operator fun invoke(
        date: LocalDate,
        hoursWorked: Float,
        wasInOffice: Boolean = true,
        notes: String? = null
    ): Result<Unit> {
        return try {
            if (hoursWorked < 0 || hoursWorked > 24) {
                return Result.failure(IllegalArgumentException("Hours must be between 0 and 24"))
            }

            val entry = DailyEntry(
                date = date,
                wasInOffice = wasInOffice,
                hoursWorked = hoursWorked,
                notes = notes
            )

            repository.saveDailyEntry(entry)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

