package com.example.go2office.presentation.settings
import com.example.go2office.domain.model.OfficeSettings
sealed class SettingsEvent {
    data class UpdateSettings(val settings: OfficeSettings) : SettingsEvent()
    object Save : SettingsEvent()
    object DismissError : SettingsEvent()
}
