package com.example.go2office.presentation.autodetection
sealed class AutoDetectionEvent {
    object ToggleAutoDetection : AutoDetectionEvent()
    object UseCurrentLocation : AutoDetectionEvent()
    data class SetCustomLocation(val latitude: Double, val longitude: Double, val name: String) : AutoDetectionEvent()
    data class UpdateGeofenceRadius(val radiusMeters: Float) : AutoDetectionEvent()
    object RequestLocationPermission : AutoDetectionEvent()
    object DismissError : AutoDetectionEvent()
}
