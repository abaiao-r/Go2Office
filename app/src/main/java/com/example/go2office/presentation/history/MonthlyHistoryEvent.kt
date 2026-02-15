package com.example.go2office.presentation.history

import java.time.YearMonth

sealed class MonthlyHistoryEvent {
    data class SelectMonth(val yearMonth: YearMonth) : MonthlyHistoryEvent()
    object Refresh : MonthlyHistoryEvent()
}

