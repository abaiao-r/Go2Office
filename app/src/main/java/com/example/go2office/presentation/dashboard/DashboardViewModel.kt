package com.example.go2office.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.go2office.domain.usecase.*
import com.example.go2office.util.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.YearMonth
import javax.inject.Inject

/**
 * ViewModel for dashboard screen.
 */
@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val getMonthProgress: GetMonthProgressUseCase,
    private val getSuggestedDays: GetSuggestedOfficeDaysUseCase,
    private val getDailyEntries: GetDailyEntriesUseCase,
    private val markDayAsOffice: MarkDayAsOfficeUseCase,
    private val getActiveSession: GetActiveOfficeSessionUseCase,
    private val repository: com.example.go2office.domain.repository.OfficeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        loadDashboardData()
        observeActiveSession()
        observeHolidayChanges()
        observeSettingsChanges()
    }

    private fun observeActiveSession() {
        viewModelScope.launch {
            getActiveSession().collect { session ->
                _uiState.update { it.copy(activeSession = session) }
            }
        }
    }

    /**
     * Observe settings changes and reload dashboard when user changes days/hours in Settings.
     * This ensures requirements update immediately when user changes requirements.
     */
    private fun observeSettingsChanges() {
        viewModelScope.launch {
            repository.getSettings().collect { settings ->
                // Only reload if not initial load (avoid double loading)
                if (_uiState.value.monthProgress != null) {
                    loadDashboardData()
                }
            }
        }
    }

    /**
     * Observe holiday changes and reload dashboard when holidays are added/removed.
     * This ensures monthly requirements update automatically when holidays change.
     */
    private fun observeHolidayChanges() {
        viewModelScope.launch {
            // Observe all holidays - when they change, reload dashboard
            repository.getAllHolidays().collect { holidays ->
                // Only reload if not initial load (avoid double loading)
                if (_uiState.value.monthProgress != null) {
                    loadDashboardData()
                }
            }
        }
    }

    fun onEvent(event: DashboardEvent) {
        when (event) {
            is DashboardEvent.SelectMonth -> {
                _uiState.update { it.copy(selectedMonth = event.yearMonth) }
                loadDashboardData()
            }

            DashboardEvent.Refresh -> {
                loadDashboardData()
            }

            is DashboardEvent.QuickMarkDay -> {
                quickMarkDay(event.date, event.hours)
            }

            DashboardEvent.DismissError -> {
                _uiState.update { it.copy(errorMessage = null) }
            }

            else -> { /* Navigation events handled in UI */ }
        }
    }

    private fun loadDashboardData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val yearMonth = _uiState.value.selectedMonth

            try {
                // Load month progress
                val progressResult = getMonthProgress(yearMonth)
                if (progressResult.isFailure) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = progressResult.exceptionOrNull()?.message
                        )
                    }
                    return@launch
                }

                val progress = progressResult.getOrNull()

                // Load ALL suggested days needed to meet requirements (not just 5)
                val suggestedResult = getSuggestedDays(
                    yearMonth = yearMonth,
                    count = 20 // Request up to 20 days to ensure we show all needed days
                )
                val suggested = suggestedResult.getOrNull() ?: emptyList()

                // Collect recent entries
                getDailyEntries.recent(Constants.DEFAULT_RECENT_ENTRIES_LIMIT)
                    .collect { entries ->
                        _uiState.update {
                            it.copy(
                                monthProgress = progress,
                                suggestedDays = suggested,
                                recentEntries = entries,
                                isLoading = false
                            )
                        }
                    }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Failed to load dashboard data"
                    )
                }
            }
        }
    }

    private fun quickMarkDay(date: java.time.LocalDate, hours: Float) {
        viewModelScope.launch {
            val result = markDayAsOffice(date, hours)
            if (result.isFailure) {
                _uiState.update {
                    it.copy(errorMessage = result.exceptionOrNull()?.message)
                }
            } else {
                // Refresh data after marking
                loadDashboardData()
            }
        }
    }
}

