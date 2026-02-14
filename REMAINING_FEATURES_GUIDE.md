# 🚀 Remaining Features Implementation Guide

## ✅ What Was Already Implemented

1. **LocationHelper utility** - Gets current GPS location ✅
2. **Google Maps dependency** added ✅
3. **Auto-detection state fields** in onboarding ✅
4. **Auto-detection events** added ✅
5. **"Use Current Location"** in AutoDetectionViewModel ✅

---

## 📋 What Still Needs Implementation

### 1. Update OnboardingViewModel

**File**: `presentation/onboarding/OnboardingViewModel.kt`

**Add imports**:
```kotlin
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.example.go2office.data.local.dao.OfficeLocationDao
import com.example.go2office.data.local.entities.OfficeLocationEntity
import com.example.go2office.service.GeofencingManager
import com.example.go2office.util.LocationHelper
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.Instant
```

**Update constructor**:
```kotlin
@HiltViewModel
class OnboardingViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val saveSettings: SaveOfficeSettingsUseCase,
    private val validateSettings: ValidateSettingsUseCase,
    private val locationHelper: LocationHelper,
    private val officeLocationDao: OfficeLocationDao,
    private val geofencingManager: GeofencingManager
) : ViewModel()
```

**Add permission check function**:
```kotlin
fun checkLocationPermission() {
    val hasPermission = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
    
    _uiState.update { it.copy(hasLocationPermission = hasPermission) }
}
```

**Add to onEvent when block**:
```kotlin
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
```

**Add useCurrentLocation function**:
```kotlin
private fun useCurrentLocation() {
    viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true) }
        
        val location = locationHelper.getCurrentLocation() 
            ?: locationHelper.getLastKnownLocation()
        
        if (location != null) {
            _uiState.update {
                it.copy(
                    officeLatitude = location.latitude,
                    officeLongitude = location.longitude,
                    officeName = "Current Location",
                    isLoading = false
                )
            }
        } else {
            _uiState.update {
                it.copy(
                    isLoading = false,
                    errorMessage = "Could not get location"
                )
            }
        }
    }
}
```

**Update completeOnboarding to save auto-detection**:
```kotlin
// After saving settings successfully
if (currentState.enableAutoDetection && 
    currentState.officeLatitude != null && 
    currentState.officeLongitude != null) {
    
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
            onSuccess = { /* Success */ },
            onFailure = { /* Log error */ }
        )
    }
}
```

---

### 2. Update OnboardingScreen - Add Step 4

**File**: `presentation/onboarding/OnboardingScreen.kt`

**Add after Step 3** (around line 150):

```kotlin
// Step 4: Auto-Detection (Optional)
3 -> {
    Step4AutoDetection(
        enableAutoDetection = uiState.enableAutoDetection,
        officeLatitude = uiState.officeLatitude,
        officeLongitude = uiState.officeLongitude,
        officeName = uiState.officeName,
        hasLocationPermission = uiState.hasLocationPermission,
        onToggleAutoDetection = { 
            viewModel.onEvent(OnboardingEvent.ToggleAutoDetection(it)) 
        },
        onUseCurrentLocation = { 
            viewModel.onEvent(OnboardingEvent.UseCurrentLocation) 
        },
        onSetLocation = { lat, lon, name ->
            viewModel.onEvent(OnboardingEvent.SetOfficeLocation(lat, lon, name))
        },
        onRequestPermission = permissionLauncher
    )
}
```

**Add Step4AutoDetection composable**:
```kotlin
@Composable
private fun Step4AutoDetection(
    enableAutoDetection: Boolean,
    officeLatitude: Double?,
    officeLongitude: Double?,
    officeName: String,
    hasLocationPermission: Boolean,
    onToggleAutoDetection: (Boolean) -> Unit,
    onUseCurrentLocation: () -> Unit,
    onSetLocation: (Double, Double, String) -> Unit,
    onRequestPermission: ManagedActivityResultLauncher<Array<String>, Map<String, @JvmSuppressWildcards Boolean>>
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Auto-Detection (Optional)",
            style = MaterialTheme.typography.headlineSmall
        )
        
        Text(
            text = "Automatically track when you arrive and leave the office",
            style = MaterialTheme.typography.bodyMedium
        )
        
        // Enable toggle
        Card(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Enable Auto-Detection")
                Switch(
                    checked = enableAutoDetection,
                    onCheckedChange = onToggleAutoDetection
                )
            }
        }
        
        if (enableAutoDetection) {
            // Permission card
            if (!hasLocationPermission) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("⚠️ Location Permission Required")
                        Button(
                            onClick = {
                                onRequestPermission.launch(
                                    arrayOf(
                                        Manifest.permission.ACCESS_FINE_LOCATION,
                                        Manifest.permission.ACCESS_BACKGROUND_LOCATION
                                    )
                                )
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Grant Permission")
                        }
                    }
                }
            }
            
            // Location card
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Office Location", style = MaterialTheme.typography.titleMedium)
                    
                    if (officeLatitude != null && officeLongitude != null) {
                        Text("📍 $officeName")
                        Text(
                            "Lat: %.4f, Lon: %.4f".format(officeLatitude, officeLongitude),
                            style = MaterialTheme.typography.bodySmall
                        )
                    } else {
                        Text("Not set", color = MaterialTheme.colorScheme.error)
                    }
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = onUseCurrentLocation,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Use Current")
                        }
                        
                        Button(
                            onClick = { /* Show location dialog */ },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Set Location")
                        }
                    }
                }
            }
        }
    }
}
```

**Add permission launcher at top of OnboardingScreen**:
```kotlin
val permissionLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.RequestMultiplePermissions()
) { permissions ->
    viewModel.checkLocationPermission()
}
```

---

### 3. Google Maps Location Picker (Advanced)

**Create new file**: `presentation/autodetection/MapLocationPicker.kt`

```kotlin
@Composable
fun MapLocationPicker(
    initialLatitude: Double = 0.0,
    initialLongitude: Double = 0.0,
    onLocationSelected: (Double, Double) -> Unit,
    onDismiss: () -> Unit
) {
    // Full-screen map dialog
    // Uses Google Maps Compose
    // Allows user to tap on map to select location
    // Shows marker at selected position
    // "Confirm" button calls onLocationSelected
}
```

**Usage in AutoDetectionScreen and OnboardingScreen**:
```kotlin
var showMapPicker by remember { mutableStateOf(false) }

if (showMapPicker) {
    MapLocationPicker(
        onLocationSelected = { lat, lon ->
            viewModel.onEvent(AutoDetectionEvent.SetCustomLocation(lat, lon, "Selected Location"))
            showMapPicker = false
        },
        onDismiss = { showMapPicker = false }
    )
}
```

---

## 🎯 Implementation Priority

### Phase 1 (High Priority):
1. ✅ LocationHelper - **DONE**
2. ✅ "Use Current Location" in AutoDetectionViewModel - **DONE**
3. ⏳ Update OnboardingViewModel with auto-detection logic
4. ⏳ Add Step 4 to OnboardingScreen
5. ⏳ Permission handling in onboarding

### Phase 2 (Medium Priority):
6. ⏳ Google Maps location picker dialog
7. ⏳ Map integration in both screens
8. ⏳ Address reverse geocoding

### Phase 3 (Nice to Have):
9. ⏳ Address search/autocomplete
10. ⏳ Save multiple favorite locations
11. ⏳ Map with office radius visualization

---

## 🔧 Quick Implementation Steps

### To complete Phase 1 (30 minutes):

1. **Update OnboardingViewModel.kt**:
   - Add LocationHelper, OfficeLocationDao, GeofencingManager to constructor
   - Add permission check function
   - Add auto-detection event handlers
   - Update completeOnboarding to save location and start geofencing

2. **Update OnboardingScreen.kt**:
   - Add permission launcher
   - Add Step 4 composable
   - Wire up events

3. **Test**:
   - Run onboarding
   - See Step 4
   - Toggle auto-detection
   - Grant permissions
   - Use current location
   - Complete setup

---

## 📖 Files to Modify

1. ✅ `util/LocationHelper.kt` - **CREATED**
2. ⏳ `presentation/onboarding/OnboardingViewModel.kt` - **NEEDS UPDATE**
3. ⏳ `presentation/onboarding/OnboardingScreen.kt` - **NEEDS UPDATE**
4. ⏳ `presentation/autodetection/MapLocationPicker.kt` - **NEEDS CREATION** (optional)

---

## 🎉 What Works Now

- ✅ LocationHelper can get GPS coordinates
- ✅ "Use Current Location" works in Auto-Detection settings
- ✅ Onboarding has auto-detection state
- ✅ Auto-detection events defined

## ⏳ What Needs Work

- ⏳ Onboarding Step 4 UI
- ⏳ Onboarding permission flow
- ⏳ Onboarding location saving
- ⏳ Google Maps picker (nice to have)

---

*The foundation is in place - just need to wire up the onboarding UI and logic!* 🚀

