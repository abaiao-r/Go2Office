package com.example.go2office.presentation.onboarding

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.go2office.data.local.dao.OfficeLocationDao
import com.example.go2office.data.local.entities.OfficeLocationEntity
import com.example.go2office.domain.usecase.SaveOfficeSettingsUseCase
import com.example.go2office.domain.usecase.ValidateSettingsUseCase
import com.example.go2office.service.GeofencingManager
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import javax.inject.Inject

/**
 * ViewModel for onboarding flow.
 */
@HiltViewModel
class OnboardingViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val saveSettings: SaveOfficeSettingsUseCase,
    private val validateSettings: ValidateSettingsUseCase,
    private val officeLocationDao: OfficeLocationDao,
    private val geofencingManager: GeofencingManager,
    private val repository: com.example.go2office.domain.repository.OfficeRepository,
    private val holidayApiService: com.example.go2office.data.remote.HolidayApiService
) : ViewModel() {

    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    init {
        // Check permissions on init
        checkLocationPermission()
    }

    fun onEvent(event: OnboardingEvent) {
        when (event) {
            is OnboardingEvent.UpdateRequiredDays -> {
                _uiState.update { it.copy(requiredDaysPerWeek = event.days) }
            }

            is OnboardingEvent.UpdateHoursPerDay -> {
                _uiState.update { it.copy(hoursPerDay = event.hours) }
            }

            is OnboardingEvent.UpdateWeekdayPreferences -> {
                _uiState.update { it.copy(weekdayPreferences = event.preferences) }
            }

            is OnboardingEvent.ToggleAutoDetection -> {
                _uiState.update { it.copy(enableAutoDetection = event.enabled) }
            }

            OnboardingEvent.UseCurrentLocation -> {
                useCurrentLocation()
            }

            is OnboardingEvent.SetOfficeLocation -> {
                _uiState.update {
                    it.copy(
                        officeLatitude = event.latitude,
                        officeLongitude = event.longitude,
                        officeName = event.name
                    )
                }
            }

            OnboardingEvent.RequestLocationPermission -> {
                // Handled in UI
            }

            is OnboardingEvent.AddHoliday -> {
                addHoliday(event.date, event.description, event.isVacation)
            }

            OnboardingEvent.NextStep -> {
                val currentState = _uiState.value
                if (currentState.canGoNext && currentState.currentStep < currentState.totalSteps - 1) {
                    _uiState.update { it.copy(currentStep = it.currentStep + 1) }
                }
            }

            OnboardingEvent.PreviousStep -> {
                val currentState = _uiState.value
                if (currentState.currentStep > 0) {
                    _uiState.update { it.copy(currentStep = it.currentStep - 1) }
                }
            }

            OnboardingEvent.Complete -> {
                completeOnboarding()
            }

            OnboardingEvent.DismissError -> {
                _uiState.update { it.copy(errorMessage = null) }
            }
        }
    }

    fun checkLocationPermission() {
        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        _uiState.update { it.copy(hasLocationPermission = hasPermission) }
    }

    private fun useCurrentLocation() {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, errorMessage = null) }

                // Check permission
                if (ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Location permission required. Please grant permission first."
                        )
                    }
                    return@launch
                }

                // Get current location with timeout
                val cancellationToken = CancellationTokenSource()

                try {
                    fusedLocationClient.getCurrentLocation(
                        Priority.PRIORITY_HIGH_ACCURACY,
                        cancellationToken.token
                    ).addOnSuccessListener { location ->
                        viewModelScope.launch {
                            if (location != null) {
                                _uiState.update {
                                    it.copy(
                                        officeLatitude = location.latitude,
                                        officeLongitude = location.longitude,
                                        officeName = "Current Location",
                                        isLoading = false,
                                        errorMessage = null
                                    )
                                }
                            } else {
                                _uiState.update {
                                    it.copy(
                                        isLoading = false,
                                        errorMessage = "Could not get location. Please:\n1. Enable GPS\n2. Go to open area\n3. Try again, or use 'Enter Manually'"
                                    )
                                }
                            }
                        }
                    }.addOnFailureListener { e ->
                        viewModelScope.launch {
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    errorMessage = "GPS Error: ${e.localizedMessage}\n\nTry 'Enter Manually' instead."
                                )
                            }
                        }
                    }
                } catch (e: SecurityException) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Permission denied. Please grant location permission."
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Error: ${e.localizedMessage ?: e.message}\n\nPlease use 'Enter Manually' instead."
                    )
                }
            }
        }
    }

    private fun completeOnboarding() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val currentState = _uiState.value

            // Validate settings
            val validationResult = validateSettings(
                requiredDaysPerWeek = currentState.requiredDaysPerWeek,
                requiredHoursPerWeek = currentState.requiredHoursPerWeek,
                weekdayPreferences = currentState.weekdayPreferences
            )

            if (validationResult.isFailure) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = validationResult.exceptionOrNull()?.message
                            ?: "Validation failed"
                    )
                }
                return@launch
            }

            // Save settings
            val settings = validationResult.getOrNull()!!
            val saveResult = saveSettings(settings)

            if (saveResult.isFailure) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = saveResult.exceptionOrNull()?.message
                            ?: "Failed to save settings"
                    )
                }
                return@launch
            }

            // Save auto-detection settings if enabled
            if (currentState.enableAutoDetection &&
                currentState.officeLatitude != null &&
                currentState.officeLongitude != null) {

                try {
                    // Deactivate any existing locations first
                    officeLocationDao.deactivateAll()

                    // Save office location
                    val locationEntity = OfficeLocationEntity(
                        latitude = currentState.officeLatitude,
                        longitude = currentState.officeLongitude,
                        radiusMeters = 100f,
                        name = currentState.officeName,
                        isActive = true,
                        createdAt = Instant.now()
                    )
                    officeLocationDao.insert(locationEntity)

                    // Start geofencing if permissions granted
                    if (currentState.hasLocationPermission) {
                        geofencingManager.startGeofencing(
                            locationEntity,
                            onSuccess = { /* Geofencing started */ },
                            onFailure = { /* Log but don't block */ }
                        )
                    }
                } catch (e: Exception) {
                    // Log error but don't block completion
                }
            }

            // Success
            _uiState.update {
                it.copy(
                    isLoading = false,
                    isComplete = true
                )
            }
        }
    }

    private fun addHoliday(date: java.time.LocalDate, description: String, isVacation: Boolean) {
        viewModelScope.launch {
            try {
                val holiday = com.example.go2office.domain.model.Holiday(
                    date = date,
                    description = description,
                    type = if (isVacation) com.example.go2office.domain.model.HolidayType.VACATION
                           else com.example.go2office.domain.model.HolidayType.PUBLIC_HOLIDAY
                )
                repository.saveHoliday(holiday)
                _uiState.update { it.copy(holidaysConfigured = true) }
            } catch (e: Exception) {
                // Log but don't block
            }
        }
    }

    fun loadCountryHolidays(countryCode: String, countryName: String) {
        viewModelScope.launch {
            try {
                val year = java.time.LocalDate.now().year
                val result = holidayApiService.fetchPublicHolidays(countryCode, year)

                result.onSuccess { holidayDtos ->
                    holidayDtos.forEach { dto ->
                        val holiday = com.example.go2office.domain.model.Holiday(
                            date = java.time.LocalDate.parse(dto.date),
                            description = dto.localName,
                            type = com.example.go2office.domain.model.HolidayType.PUBLIC_HOLIDAY
                        )
                        repository.saveHoliday(holiday)
                    }
                    _uiState.update { it.copy(holidaysConfigured = true) }
                }
            } catch (e: Exception) {
                // Log but don't block
            }
        }
    }
}
