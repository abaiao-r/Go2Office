package com.example.go2office.presentation.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.go2office.domain.repository.OfficeRepository
import com.example.go2office.domain.usecase.GetDailyEntriesUseCase
import com.example.go2office.domain.usecase.GetMonthProgressUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MonthlyHistoryViewModel @Inject constructor(
    private val getDailyEntries: GetDailyEntriesUseCase,
    private val getMonthProgress: GetMonthProgressUseCase,
    private val repository: OfficeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MonthlyHistoryUiState())
    val uiState: StateFlow<MonthlyHistoryUiState> = _uiState.asStateFlow()

    private var historyJob: Job? = null

    init {
        loadHistoryData()
    }

    fun onEvent(event: MonthlyHistoryEvent) {
        when (event) {
            is MonthlyHistoryEvent.SelectMonth -> {
                _uiState.update { it.copy(selectedMonth = event.yearMonth, expandedDate = null) }
                loadHistoryData()
            }
            is MonthlyHistoryEvent.ToggleExpanded -> {
                val currentExpanded = _uiState.value.expandedDate
                val newExpanded = if (currentExpanded == event.date) null else event.date
                _uiState.update { it.copy(expandedDate = newExpanded) }
            }
            MonthlyHistoryEvent.Refresh -> loadHistoryData()
        }
    }

    private fun loadHistoryData() {
        historyJob?.cancel()
        historyJob = viewModelScope.launch {
            // Only show loading indicator on initial load when we have no data
            val showLoading = _uiState.value.dailyEntries.isEmpty() && _uiState.value.totalDaysWorked == 0
            if (showLoading) {
                _uiState.update { state -> state.copy(isLoading = true) }
            }
            val yearMonth = _uiState.value.selectedMonth
            try {
                val progressResult = getMonthProgress(yearMonth)
                val progress = progressResult.getOrNull()

                val startDate = yearMonth.atDay(1)
                val endDate = yearMonth.atEndOfMonth()

                val sessionsFlow = repository.getSessionsInRange(startDate, endDate)
                val entriesFlow = getDailyEntries.forMonth(yearMonth)

                combine(sessionsFlow, entriesFlow) { sessions, entries ->
                    Pair(sessions, entries)
                }.collect { (sessions, entries) ->
                    val sessionsByDate = sessions.groupBy { it.entryTime.toLocalDate() }
                    val workedEntries = entries.filter { entry -> entry.wasInOffice }
                    val totalDays = workedEntries.size
                    val totalHours = workedEntries.sumOf { entry -> entry.hoursWorked.toDouble() }.toFloat()
                    val sortedEntries = entries.sortedByDescending { entry -> entry.date }

                    val entriesWithSessions = sortedEntries.map { entry ->
                        entry.copy(sessions = sessionsByDate[entry.date] ?: emptyList())
                    }

                    _uiState.update { state ->
                        state.copy(
                            dailyEntries = entriesWithSessions,
                            sessionsByDate = sessionsByDate,
                            totalDaysWorked = totalDays,
                            totalHoursWorked = totalHours,
                            requiredDays = progress?.requiredDays ?: 0,
                            requiredHours = progress?.requiredHours ?: 0f,
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update { state ->
                    state.copy(isLoading = false, errorMessage = e.message)
                }
            }
        }
    }
}
