package com.example.go2office.domain.model
import java.time.DayOfWeek
data class OfficeSettings(
    val requiredDaysPerWeek: Int,
    val requiredHoursPerWeek: Float,
    val weekdayPreferences: List<DayOfWeek> 
) {
    init {
        require(requiredDaysPerWeek in 1..5) { "Required days per week must be between 1 and 5" }
        require(requiredHoursPerWeek > 0) { "Required hours per week must be positive" }
        require(weekdayPreferences.size == 5) { "Must have exactly 5 weekday preferences" }
        require(weekdayPreferences.all { it in DayOfWeek.MONDAY..DayOfWeek.FRIDAY }) {
            "Preferences must only include Monday through Friday"
        }
        require(weekdayPreferences.distinct().size == 5) { "Weekday preferences must be unique" }
    }
}
