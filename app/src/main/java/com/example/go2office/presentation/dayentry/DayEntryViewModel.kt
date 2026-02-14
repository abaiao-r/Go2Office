package com.example.go2office.presentation.dayentry

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.go2office.domain.usecase.GetDailyEntriesUseCase
import com.example.go2office.domain.usecase.UpdateDailyHoursUseCase
import com.example.go2office.domain.repository.OfficeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

/**
 * ViewModel for day entry screen.
 */
@HiltViewModel
class DayEntryViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getDailyEntries: GetDailyEntriesUseCase,
    private val updateDailyHours: UpdateDailyHoursUseCase,
    private val repository: OfficeRepository
) : ViewModel() {

    private val dateString: String = savedStateHandle.get<String>("date") ?: LocalDate.now().toString()
    private val date: LocalDate = LocalDate.parse(dateString)

    private val _uiState = MutableStateFlow(DayEntryUiState(selectedDate = date))
    val uiState: StateFlow<DayEntryUiState> = _uiState.asStateFlow()

    init {
        loadEntry()
    }

    fun onEvent(event: DayEntryEvent) {
        when (event) {
            is DayEntryEvent.SelectDate -> {
                _uiState.update { it.copy(selectedDate = event.date) }
                loadEntry()
            }

            is DayEntryEvent.ToggleWasInOffice -> {
                _uiState.update { it.copy(wasInOffice = event.value) }
            }

            is DayEntryEvent.UpdateHours -> {
                _uiState.update { it.copy(hoursWorked = event.hours) }
            }

            is DayEntryEvent.UpdateNotes -> {
                _uiState.update { it.copy(notes = event.notes) }
            }

            DayEntryEvent.Save -> {
                saveEntry()
            }

            DayEntryEvent.Delete -> {
                deleteEntry()
            }

            DayEntryEvent.DismissError -> {
                _uiState.update { it.copy(errorMessage = null) }
            }
        }
    }

    private fun loadEntry() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val entry = getDailyEntries.getByDate(_uiState.value.selectedDate)

            if (entry != null) {
                _uiState.update {
                    it.copy(
                        wasInOffice = entry.wasInOffice,
                        hoursWorked = entry.hoursWorked,
                        notes = entry.notes ?: "",
                        isExistingEntry = true,
                        isLoading = false
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        isExistingEntry = false,
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun saveEntry() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val currentState = _uiState.value
            val result = updateDailyHours(
                date = currentState.selectedDate,
                hoursWorked = currentState.hoursWorked,
                wasInOffice = currentState.wasInOffice,
                notes = currentState.notes.ifBlank { null }
            )

            if (result.isFailure) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = result.exceptionOrNull()?.message ?: "Failed to save entry"
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isSaved = true
                    )
                }
            }
        }
    }

    private fun deleteEntry() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val result = repository.deleteDailyEntry(_uiState.value.selectedDate)

            if (result.isFailure) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = result.exceptionOrNull()?.message ?: "Failed to delete entry"
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isSaved = true
                    )
                }
            }
        }
    }
}

