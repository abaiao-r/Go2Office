package com.example.go2office.presentation.settings

import com.example.go2office.domain.model.OfficeSettings
import java.time.DayOfWeek

/**
 * UI state for settings screen.
 */
data class SettingsUiState(
    val settings: OfficeSettings? = null,
    val isLoading: Boolean = true,
    val isSaved: Boolean = false,
    val errorMessage: String? = null
)

