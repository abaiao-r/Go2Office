package com.example.go2office.presentation.dashboard
import com.example.go2office.domain.model.DailyEntry
import com.example.go2office.domain.model.MonthProgress
import com.example.go2office.domain.model.OfficePresence
import com.example.go2office.domain.model.SuggestedDay
import java.time.LocalDate
import java.time.YearMonth
data class DashboardUiState(
    val selectedMonth: YearMonth = YearMonth.now(),
    val monthProgress: MonthProgress? = null,
    val suggestedDays: List<SuggestedDay> = emptyList(),
    val recentEntries: List<DailyEntry> = emptyList(),
    val activeSession: OfficePresence? = null,
    val todayTotalHours: Float = 0f,
    val isAtOffice: Boolean = false,
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)
