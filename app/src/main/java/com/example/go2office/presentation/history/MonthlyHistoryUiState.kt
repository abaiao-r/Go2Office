package com.example.go2office.presentation.history

import com.example.go2office.domain.model.DailyEntry
import com.example.go2office.domain.model.OfficeSession
import java.time.LocalDate
import java.time.YearMonth

data class MonthlyHistoryUiState(
    val selectedMonth: YearMonth = YearMonth.now(),
    val dailyEntries: List<DailyEntry> = emptyList(),
    val sessionsByDate: Map<LocalDate, List<OfficeSession>> = emptyMap(),
    val totalDaysWorked: Int = 0,
    val totalHoursWorked: Float = 0f,
    val requiredDays: Int = 0,
    val requiredHours: Float = 0f,
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val expandedDate: LocalDate? = null
)

