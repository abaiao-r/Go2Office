package com.example.go2office.domain.usecase

import com.example.go2office.domain.model.OfficeSettings
import java.time.DayOfWeek
import javax.inject.Inject

/**
 * Use case to validate office settings before saving.
 */
class ValidateSettingsUseCase @Inject constructor() {

    operator fun invoke(
        requiredDaysPerWeek: Int,
        requiredHoursPerWeek: Float,
        weekdayPreferences: List<DayOfWeek>
    ): Result<OfficeSettings> {
        return try {
            // Validate required days
            if (requiredDaysPerWeek !in 1..5) {
                return Result.failure(IllegalArgumentException("Required days per week must be between 1 and 5"))
            }

            // Validate required hours
            if (requiredHoursPerWeek <= 0) {
                return Result.failure(IllegalArgumentException("Required hours per week must be positive"))
            }

            if (requiredHoursPerWeek > 40) {
                return Result.failure(IllegalArgumentException("Required hours per week seems too high (max 40)"))
            }

            // Validate weekday preferences
            if (weekdayPreferences.size != 5) {
                return Result.failure(IllegalArgumentException("Must specify exactly 5 weekday preferences"))
            }

            if (weekdayPreferences.any { it !in DayOfWeek.MONDAY..DayOfWeek.FRIDAY }) {
                return Result.failure(IllegalArgumentException("Preferences must only include Monday through Friday"))
            }

            if (weekdayPreferences.distinct().size != 5) {
                return Result.failure(IllegalArgumentException("Weekday preferences must be unique"))
            }

            // Create settings object (this will also validate via data class init)
            val settings = OfficeSettings(
                requiredDaysPerWeek = requiredDaysPerWeek,
                requiredHoursPerWeek = requiredHoursPerWeek,
                weekdayPreferences = weekdayPreferences
            )

            Result.success(settings)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

