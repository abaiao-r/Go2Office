package com.example.go2office.presentation.autodetection

import com.example.go2office.domain.model.OfficeLocation

/**
 * UI state for auto-detection settings.
 */
data class AutoDetectionUiState(
    val isEnabled: Boolean = false,
    val officeLocation: OfficeLocation? = null,
    val geofenceRadiusMeters: Float = 100f,
    val hasLocationPermission: Boolean = false,
    val hasBackgroundPermission: Boolean = false,
    val hasNotificationPermission: Boolean = false,
    val isGeofencingActive: Boolean = false,
    val currentSessionStartTime: String? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

