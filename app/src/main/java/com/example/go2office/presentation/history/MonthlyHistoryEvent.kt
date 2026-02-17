package com.example.go2office.presentation.history

import java.time.LocalDate
import java.time.YearMonth

sealed class MonthlyHistoryEvent {
    data class SelectMonth(val yearMonth: YearMonth) : MonthlyHistoryEvent()
    data class ToggleExpanded(val date: LocalDate) : MonthlyHistoryEvent()
    object Refresh : MonthlyHistoryEvent()
}

