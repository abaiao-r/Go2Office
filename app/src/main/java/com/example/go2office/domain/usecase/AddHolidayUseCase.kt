package com.example.go2office.domain.usecase

import com.example.go2office.domain.model.Holiday
import com.example.go2office.domain.repository.OfficeRepository
import java.time.LocalDate
import javax.inject.Inject

/**
 * Use case to add a holiday (office-not-required day).
 */
class AddHolidayUseCase @Inject constructor(
    private val repository: OfficeRepository
) {

    suspend operator fun invoke(date: LocalDate, description: String): Result<Unit> {
        return try {
            if (description.isBlank()) {
                return Result.failure(IllegalArgumentException("Description cannot be empty"))
            }

            val holiday = Holiday(
                date = date,
                description = description
            )

            repository.saveHoliday(holiday)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

