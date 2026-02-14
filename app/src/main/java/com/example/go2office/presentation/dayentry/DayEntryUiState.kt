package com.example.go2office.presentation.dayentry
import java.time.LocalDate
data class DayEntryUiState(
    val selectedDate: LocalDate,
    val wasInOffice: Boolean = true,
    val hoursWorked: Float = 8f,
    val notes: String = "",
    val isExistingEntry: Boolean = false,
    val isLoading: Boolean = true,
    val isSaved: Boolean = false,
    val errorMessage: String? = null
)
