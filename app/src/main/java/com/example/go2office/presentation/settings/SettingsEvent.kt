package com.example.go2office.presentation.settings

import com.example.go2office.domain.model.OfficeSettings

/**
 * Events for settings screen.
 */
sealed class SettingsEvent {
    data class UpdateSettings(val settings: OfficeSettings) : SettingsEvent()
    object Save : SettingsEvent()
    object DismissError : SettingsEvent()
}

