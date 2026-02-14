package com.example.go2office.presentation.autodetection
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.go2office.data.local.dao.OfficeLocationDao
import com.example.go2office.data.local.dao.OfficePresenceDao
import com.example.go2office.data.local.entities.OfficeLocationEntity
import com.example.go2office.domain.model.OfficeLocation
import com.example.go2office.service.GeofencingManager
import com.example.go2office.util.Constants
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
@HiltViewModel
class AutoDetectionViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val officeLocationDao: OfficeLocationDao,
    private val officePresenceDao: OfficePresenceDao,
    private val geofencingManager: GeofencingManager
) : ViewModel() {
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    private val _uiState = MutableStateFlow(AutoDetectionUiState())
    val uiState: StateFlow<AutoDetectionUiState> = _uiState.asStateFlow()
    init {
        checkPermissions()
        loadSettings()
    }
    init {
        loadSettings()
        checkPermissions()
    }
    fun onEvent(event: AutoDetectionEvent) {
        when (event) {
            AutoDetectionEvent.ToggleAutoDetection -> toggleAutoDetection()
            AutoDetectionEvent.UseCurrentLocation -> useCurrentLocation()
            is AutoDetectionEvent.SetCustomLocation -> setCustomLocation(event.latitude, event.longitude, event.name)
            is AutoDetectionEvent.UpdateGeofenceRadius -> updateGeofenceRadius(event.radiusMeters)
            AutoDetectionEvent.RequestLocationPermission -> {} 
            AutoDetectionEvent.DismissError -> _uiState.update { it.copy(errorMessage = null) }
        }
    }
    private fun loadSettings() {
        viewModelScope.launch {
            try {
                val location = officeLocationDao.getActiveLocation()
                val activeSession = officePresenceDao.getCurrentActiveSession()
                _uiState.update {
                    it.copy(
                        officeLocation = location?.let { entity ->
                            OfficeLocation(
                                latitude = entity.latitude,
                                longitude = entity.longitude,
                                radiusMeters = entity.radiusMeters,
                                name = entity.name
                            )
                        },
                        geofenceRadiusMeters = location?.radiusMeters ?: Constants.DEFAULT_GEOFENCE_RADIUS_METERS,
                        isEnabled = location != null && location.isActive,
                        isGeofencingActive = location != null && location.isActive,
                        currentSessionStartTime = activeSession?.entryTime?.toString()
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "Failed to load settings: ${e.message}") }
            }
        }
    }
    fun checkPermissions() {
        val hasLocation = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val hasBackground = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val hasNotification = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
        _uiState.update {
            it.copy(
                hasLocationPermission = hasLocation,
                hasBackgroundPermission = hasBackground,
                hasNotificationPermission = hasNotification
            )
        }
    }
    private fun toggleAutoDetection() {
        viewModelScope.launch {
            val currentState = _uiState.value
            if (currentState.isEnabled) {
                stopGeofencing()
            } else {
                if (currentState.officeLocation == null) {
                    _uiState.update { it.copy(errorMessage = "Please set office location first") }
                    return@launch
                }
                if (!currentState.hasLocationPermission || !currentState.hasBackgroundPermission) {
                    _uiState.update { it.copy(errorMessage = "Location permissions required") }
                    return@launch
                }
                startGeofencing()
            }
        }
    }
    private fun useCurrentLocation() {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, errorMessage = null) }
                if (ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Location permission not granted. Please grant permission first."
                        )
                    }
                    return@launch
                }
                val cancellationToken = CancellationTokenSource()
                fusedLocationClient.getCurrentLocation(
                    Priority.PRIORITY_HIGH_ACCURACY,
                    cancellationToken.token
                ).addOnSuccessListener { location ->
                    if (location != null) {
                        viewModelScope.launch {
                            setCustomLocation(
                                latitude = location.latitude,
                                longitude = location.longitude,
                                name = "Current Location"
                            )
                            _uiState.update { it.copy(isLoading = false) }
                        }
                    } else {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = "Could not get current location. Please try again or enter manually."
                            )
                        }
                    }
                }.addOnFailureListener { e ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Failed to get location: ${e.message}"
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Failed to get location: ${e.message}"
                    )
                }
            }
        }
    }
    private fun setCustomLocation(latitude: Double, longitude: Double, name: String) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }
                officeLocationDao.deactivateAll()
                val entity = OfficeLocationEntity(
                    latitude = latitude,
                    longitude = longitude,
                    radiusMeters = _uiState.value.geofenceRadiusMeters,
                    name = name,
                    isActive = true,
                    createdAt = Instant.now()
                )
                officeLocationDao.insert(entity)
                _uiState.update {
                    it.copy(
                        officeLocation = OfficeLocation(latitude, longitude, entity.radiusMeters, name),
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Failed to save location: ${e.message}"
                    )
                }
            }
        }
    }
    private fun updateGeofenceRadius(radiusMeters: Float) {
        viewModelScope.launch {
            _uiState.update { it.copy(geofenceRadiusMeters = radiusMeters) }
            val location = officeLocationDao.getActiveLocation()
            if (location != null) {
                val updated = location.copy(radiusMeters = radiusMeters)
                officeLocationDao.update(updated)
                if (_uiState.value.isEnabled) {
                    stopGeofencing()
                    startGeofencing()
                }
            }
        }
    }
    private suspend fun startGeofencing() {
        try {
            val location = officeLocationDao.getActiveLocation()
            if (location == null) {
                _uiState.update { it.copy(errorMessage = "No office location configured") }
                return
            }
            geofencingManager.startGeofencing(
                location = location,
                onSuccess = {
                    _uiState.update {
                        it.copy(
                            isEnabled = true,
                            isGeofencingActive = true,
                            errorMessage = null
                        )
                    }
                },
                onFailure = { exception ->
                    _uiState.update {
                        it.copy(
                            isEnabled = false,
                            isGeofencingActive = false,
                            errorMessage = "Failed to start geofencing: ${exception.message}"
                        )
                    }
                }
            )
        } catch (e: Exception) {
            _uiState.update {
                it.copy(
                    isEnabled = false,
                    errorMessage = "Error starting geofencing: ${e.message}"
                )
            }
        }
    }
    private fun stopGeofencing() {
        geofencingManager.stopGeofencing(
            onSuccess = {
                _uiState.update {
                    it.copy(
                        isEnabled = false,
                        isGeofencingActive = false
                    )
                }
            },
            onFailure = { exception ->
                _uiState.update {
                    it.copy(errorMessage = "Failed to stop geofencing: ${exception.message}")
                }
            }
        )
    }
}
