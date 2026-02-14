package com.example.go2office.presentation.navigation
import java.time.LocalDate
sealed class Screen(val route: String) {
    object Onboarding : Screen("onboarding")
    object Dashboard : Screen("dashboard")
    object DayEntry : Screen("dayEntry/{date}") {
        fun createRoute(date: LocalDate): String = "dayEntry/$date"
    }
    object Settings : Screen("settings")
    object AutoDetection : Screen("autoDetection")
    object PermissionsSetup : Screen("permissionsSetup")
    object AnnualCalendar : Screen("annualCalendar")
}
