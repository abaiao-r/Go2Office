package com.example.go2office.presentation.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.go2office.domain.model.DailyEntry
import com.example.go2office.domain.repository.OfficeRepository
import com.example.go2office.domain.usecase.GetDailyEntriesUseCase
import com.example.go2office.domain.usecase.GetMonthProgressUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.YearMonth
import javax.inject.Inject

@HiltViewModel
class MonthlyHistoryViewModel @Inject constructor(
    private val getDailyEntries: GetDailyEntriesUseCase,
    private val getMonthProgress: GetMonthProgressUseCase,
    private val repository: OfficeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MonthlyHistoryUiState())
    val uiState: StateFlow<MonthlyHistoryUiState> = _uiState.asStateFlow()

    init {
        loadHistoryData()
    }

    fun onEvent(event: MonthlyHistoryEvent) {
        when (event) {
            is MonthlyHistoryEvent.SelectMonth -> {
                _uiState.update { it.copy(selectedMonth = event.yearMonth) }
                loadHistoryData()
            }
            MonthlyHistoryEvent.Refresh -> loadHistoryData()
        }
    }

    private fun loadHistoryData() {
        viewModelScope.launch {
            _uiState.update { state -> state.copy(isLoading = true) }
            val yearMonth = _uiState.value.selectedMonth
            try {
                val progressResult = getMonthProgress(yearMonth)
                val progress = progressResult.getOrNull()
                getDailyEntries.forMonth(yearMonth).collect { entries ->
                    val workedEntries = entries.filter { entry -> entry.wasInOffice }
                    val totalDays = workedEntries.size
                    val totalHours = workedEntries.sumOf { entry -> entry.hoursWorked.toDouble() }.toFloat()
                    val sortedEntries = entries.sortedByDescending { entry -> entry.date }
                    _uiState.update { state ->
                        state.copy(
                            dailyEntries = sortedEntries,
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
