package com.example.go2office.presentation.dashboard

import java.time.LocalDate
import java.time.YearMonth

/**
 * Events for dashboard screen.
 */
sealed class DashboardEvent {
    data class SelectMonth(val yearMonth: YearMonth) : DashboardEvent()
    object Refresh : DashboardEvent()
    data class NavigateToDayEntry(val date: LocalDate) : DashboardEvent()
    object NavigateToSettings : DashboardEvent()
    data class QuickMarkDay(val date: LocalDate, val hours: Float) : DashboardEvent()
    object DismissError : DashboardEvent()
}

