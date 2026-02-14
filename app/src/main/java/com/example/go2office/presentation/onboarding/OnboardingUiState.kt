package com.example.go2office.presentation.onboarding
import java.time.DayOfWeek
data class OnboardingUiState(
    val currentStep: Int = 0,
    val requiredDaysPerWeek: Int = 3,
    val hoursPerDay: Float = 8f,  
    val weekdayPreferences: List<DayOfWeek> = listOf(
        DayOfWeek.MONDAY,
        DayOfWeek.TUESDAY,
        DayOfWeek.WEDNESDAY,
        DayOfWeek.THURSDAY,
        DayOfWeek.FRIDAY
    ),
    val enableAutoDetection: Boolean = false,
    val officeLatitude: Double? = null,
    val officeLongitude: Double? = null,
    val officeName: String = "Main Office",
    val hasLocationPermission: Boolean = false,
    val holidaysConfigured: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isComplete: Boolean = false
) {
    val totalSteps = 5
    val requiredHoursPerWeek: Float
        get() = requiredDaysPerWeek * hoursPerDay
    val canGoNext: Boolean
        get() = when (currentStep) {
            0 -> requiredDaysPerWeek in 1..5
            1 -> hoursPerDay > 0  
            2 -> weekdayPreferences.size == 5
            3 -> !enableAutoDetection || (officeLatitude != null && officeLongitude != null)
            4 -> true
            else -> false
        }
}
