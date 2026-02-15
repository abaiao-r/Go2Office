package com.example.go2office.presentation.history

import com.example.go2office.domain.model.DailyEntry
import java.time.YearMonth

data class MonthlyHistoryUiState(
    val selectedMonth: YearMonth = YearMonth.now(),
    val dailyEntries: List<DailyEntry> = emptyList(),
    val totalDaysWorked: Int = 0,
    val totalHoursWorked: Float = 0f,
    val requiredDays: Int = 0,
    val requiredHours: Float = 0f,
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)

