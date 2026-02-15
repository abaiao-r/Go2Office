package com.example.go2office.presentation.dashboard
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.go2office.domain.usecase.GetActiveOfficeSessionUseCase
import com.example.go2office.domain.usecase.GetDailyEntriesUseCase
import com.example.go2office.domain.usecase.GetMonthProgressUseCase
import com.example.go2office.domain.usecase.GetSuggestedOfficeDaysUseCase
import com.example.go2office.domain.usecase.MarkDayAsOfficeUseCase
import com.example.go2office.util.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

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
    private fun observeSettingsChanges() {
        viewModelScope.launch {
            repository.getSettings().collect { settings ->
                if (_uiState.value.monthProgress != null) {
                    loadDashboardData()
                }
            }
        }
    }
    private fun observeHolidayChanges() {
        viewModelScope.launch {
            repository.getAllHolidays().collect { holidays ->
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
            else -> {  }
        }
    }
    private fun loadDashboardData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val yearMonth = _uiState.value.selectedMonth
            try {
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
                val suggestedResult = getSuggestedDays(
                    yearMonth = yearMonth,
                    count = 20 
                )
                val suggested = suggestedResult.getOrNull() ?: emptyList()
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
                loadDashboardData()
            }
        }
    }
}
