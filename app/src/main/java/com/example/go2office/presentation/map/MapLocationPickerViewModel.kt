package com.example.go2office.presentation.map

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.net.URL
import java.net.URLEncoder
import javax.inject.Inject

@HiltViewModel
class MapLocationPickerViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(MapLocationPickerUiState())
    val uiState: StateFlow<MapLocationPickerUiState> = _uiState.asStateFlow()

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    private val json = Json { ignoreUnknownKeys = true }

    fun setSelectedLocation(latitude: Double, longitude: Double) {
        _uiState.update {
            it.copy(
                selectedLatitude = latitude,
                selectedLongitude = longitude,
                selectedLocationName = null
            )
        }
        reverseGeocode(latitude, longitude)
    }

    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query, searchResults = emptyList()) }
    }

    fun clearSearch() {
        _uiState.update { it.copy(searchQuery = "", searchResults = emptyList()) }
    }

    fun searchLocation() {
        val query = _uiState.value.searchQuery.trim()
        if (query.isEmpty()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isSearching = true) }

            try {
                val results = withContext(Dispatchers.IO) {
                    searchWithNominatim(query)
                }
                _uiState.update { it.copy(searchResults = results, isSearching = false) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isSearching = false,
                        errorMessage = "Search failed: ${e.message}"
                    )
                }
            }
        }
    }

    fun selectSearchResult(result: SearchResult) {
        _uiState.update {
            it.copy(
                selectedLatitude = result.latitude,
                selectedLongitude = result.longitude,
                selectedLocationName = result.displayName.split(",").firstOrNull()?.trim() ?: result.displayName,
                searchQuery = "",
                searchResults = emptyList()
            )
        }
    }

    @SuppressLint("MissingPermission")
    fun useCurrentLocation() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            try {
                val cancellationToken = CancellationTokenSource()
                fusedLocationClient.getCurrentLocation(
                    Priority.PRIORITY_HIGH_ACCURACY,
                    cancellationToken.token
                ).addOnSuccessListener { location: Location? ->
                    if (location != null) {
                        setSelectedLocation(location.latitude, location.longitude)
                    } else {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = "Could not get current location"
                            )
                        }
                    }
                    _uiState.update { it.copy(isLoading = false) }
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
                        errorMessage = "Location error: ${e.message}"
                    )
                }
            }
        }
    }

    fun dismissError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    private fun reverseGeocode(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            try {
                val name = withContext(Dispatchers.IO) {
                    reverseGeocodeWithNominatim(latitude, longitude)
                }
                _uiState.update { it.copy(selectedLocationName = name) }
            } catch (e: Exception) {
                // Silently fail - coordinates are still valid
            }
        }
    }

    private fun searchWithNominatim(query: String): List<SearchResult> {
        val encodedQuery = URLEncoder.encode(query, "UTF-8")
        val url = "https://nominatim.openstreetmap.org/search?q=$encodedQuery&format=json&limit=10"

        val connection = URL(url).openConnection()
        connection.setRequestProperty("User-Agent", "Go2Office/1.0 (Android)")
        connection.connectTimeout = 10000
        connection.readTimeout = 10000

        val response = connection.getInputStream().bufferedReader().readText()
        val results = json.decodeFromString<List<NominatimSearchResult>>(response)

        return results.map { result ->
            SearchResult(
                displayName = result.display_name,
                latitude = result.lat.toDoubleOrNull() ?: 0.0,
                longitude = result.lon.toDoubleOrNull() ?: 0.0
            )
        }
    }

    private fun reverseGeocodeWithNominatim(latitude: Double, longitude: Double): String {
        val url = "https://nominatim.openstreetmap.org/reverse?lat=$latitude&lon=$longitude&format=json"

        val connection = URL(url).openConnection()
        connection.setRequestProperty("User-Agent", "Go2Office/1.0 (Android)")
        connection.connectTimeout = 10000
        connection.readTimeout = 10000

        val response = connection.getInputStream().bufferedReader().readText()
        val result = json.decodeFromString<NominatimReverseResult>(response)

        return result.address?.let { addr ->
            listOfNotNull(
                addr.building,
                addr.amenity,
                addr.road,
                addr.neighbourhood,
                addr.suburb,
                addr.city ?: addr.town ?: addr.village
            ).filter { it.isNotBlank() }.take(3).joinToString(", ")
        } ?: result.display_name.split(",").take(2).joinToString(",")
    }
}

data class MapLocationPickerUiState(
    val selectedLatitude: Double? = null,
    val selectedLongitude: Double? = null,
    val selectedLocationName: String? = null,
    val searchQuery: String = "",
    val searchResults: List<SearchResult> = emptyList(),
    val isSearching: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

@Serializable
private data class NominatimSearchResult(
    val display_name: String,
    val lat: String,
    val lon: String
)

@Serializable
private data class NominatimReverseResult(
    val display_name: String,
    val address: NominatimAddress? = null
)

@Serializable
private data class NominatimAddress(
    val building: String? = null,
    val amenity: String? = null,
    val road: String? = null,
    val neighbourhood: String? = null,
    val suburb: String? = null,
    val city: String? = null,
    val town: String? = null,
    val village: String? = null
)

