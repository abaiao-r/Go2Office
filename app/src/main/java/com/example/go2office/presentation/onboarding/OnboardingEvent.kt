package com.example.go2office.presentation.onboarding

import java.time.DayOfWeek
import java.time.LocalDate

/**
 * Events for onboarding screen.
 */
sealed class OnboardingEvent {
    data class UpdateRequiredDays(val days: Int) : OnboardingEvent()
    data class UpdateHoursPerDay(val hours: Float) : OnboardingEvent()  // Changed from UpdateRequiredHours
    data class UpdateWeekdayPreferences(val preferences: List<DayOfWeek>) : OnboardingEvent()
    data class ToggleAutoDetection(val enabled: Boolean) : OnboardingEvent()
    object UseCurrentLocation : OnboardingEvent()
    data class SetOfficeLocation(val latitude: Double, val longitude: Double, val name: String) : OnboardingEvent()
    object RequestLocationPermission : OnboardingEvent()
    data class AddHoliday(val date: LocalDate, val description: String, val isVacation: Boolean) : OnboardingEvent()
    object NextStep : OnboardingEvent()
    object PreviousStep : OnboardingEvent()
    object Complete : OnboardingEvent()
    object DismissError : OnboardingEvent()
}

