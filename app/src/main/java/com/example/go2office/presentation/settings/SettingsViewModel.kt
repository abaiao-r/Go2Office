package com.example.go2office.presentation.settings
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.go2office.domain.usecase.GetOfficeSettingsUseCase
import com.example.go2office.domain.usecase.SaveOfficeSettingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val getSettings: GetOfficeSettingsUseCase,
    private val saveSettings: SaveOfficeSettingsUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()
    init {
        loadSettings()
    }
    fun onEvent(event: SettingsEvent) {
        when (event) {
            is SettingsEvent.UpdateSettings -> {
                _uiState.update { it.copy(settings = event.settings) }
            }
            SettingsEvent.Save -> {
                saveCurrentSettings()
            }
            SettingsEvent.DismissError -> {
                _uiState.update { it.copy(errorMessage = null) }
            }
        }
    }
    private fun loadSettings() {
        viewModelScope.launch {
            getSettings().collect { settings ->
                _uiState.update {
                    it.copy(
                        settings = settings,
                        isLoading = false
                    )
                }
            }
        }
    }
    private fun saveCurrentSettings() {
        viewModelScope.launch {
            val currentSettings = _uiState.value.settings
            if (currentSettings == null) {
                _uiState.update {
                    it.copy(errorMessage = "No settings to save")
                }
                return@launch
            }
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = saveSettings(currentSettings)
            if (result.isFailure) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = result.exceptionOrNull()?.message ?: "Failed to save settings"
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
