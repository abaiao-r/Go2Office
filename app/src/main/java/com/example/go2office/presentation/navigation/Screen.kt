package com.example.go2office.presentation.navigation
import java.time.LocalDate
sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Onboarding : Screen("onboarding")
    object Dashboard : Screen("dashboard")
    object DayEntry : Screen("dayEntry/{date}") {
        fun createRoute(date: LocalDate): String = "dayEntry/$date"
    }
    object Settings : Screen("settings")
    object AutoDetection : Screen("autoDetection")
    object PermissionsSetup : Screen("permissionsSetup")
    object AnnualCalendar : Screen("annualCalendar")
    object MonthlyHistory : Screen("monthlyHistory")
    object MapLocationPicker : Screen("mapLocationPicker?lat={lat}&lon={lon}") {
        fun createRoute(latitude: Double? = null, longitude: Double? = null): String {
            return if (latitude != null && longitude != null) {
                "mapLocationPicker?lat=$latitude&lon=$longitude"
            } else {
                "mapLocationPicker"
            }
        }
    }
}
