package com.example.go2office.presentation.autodetection

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.go2office.presentation.components.ErrorDialog
import com.example.go2office.presentation.components.LoadingIndicator
import com.example.go2office.util.Constants

/**
 * Auto-detection settings screen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AutoDetectionScreen(
    viewModel: AutoDetectionViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    // Permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        viewModel.checkPermissions()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Auto-Detection") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        if (uiState.isLoading) {
            LoadingIndicator()
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Enable/Disable Toggle
                EnableToggleCard(
                    isEnabled = uiState.isEnabled,
                    isActive = uiState.isGeofencingActive,
                    onToggle = { viewModel.onEvent(AutoDetectionEvent.ToggleAutoDetection) }
                )

                // Permissions Card
                PermissionsCard(
                    hasLocation = uiState.hasLocationPermission,
                    hasBackground = uiState.hasBackgroundPermission,
                    hasNotification = uiState.hasNotificationPermission,
                    onRequestPermissions = {
                        // Request ALL permissions at once
                        val permissions = mutableListOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )

                        // Add background location if API 29+
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                            permissions.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                        }

                        // Add notification permission if API 33+
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                            permissions.add(Manifest.permission.POST_NOTIFICATIONS)
                        }

                        permissionLauncher.launch(permissions.toTypedArray())
                    }
                )

                // Office Location Card
                OfficeLocationCard(
                    location = uiState.officeLocation,
                    onUseCurrentLocation = { viewModel.onEvent(AutoDetectionEvent.UseCurrentLocation) },
                    onSetCustomLocation = { lat, lon, name ->
                        viewModel.onEvent(AutoDetectionEvent.SetCustomLocation(lat, lon, name))
                    }
                )

                // Geofence Radius Card
                GeofenceRadiusCard(
                    radiusMeters = uiState.geofenceRadiusMeters,
                    onRadiusChange = { viewModel.onEvent(AutoDetectionEvent.UpdateGeofenceRadius(it)) }
                )

                // Work Hours Info Card
                WorkHoursInfoCard()

                // Current Session (if active)
                if (uiState.currentSessionStartTime != null) {
                    CurrentSessionCard(startTime = uiState.currentSessionStartTime!!)
                }
            }
        }

        // Error dialog
        if (uiState.errorMessage != null) {
            ErrorDialog(
                message = uiState.errorMessage!!,
                onDismiss = { viewModel.onEvent(AutoDetectionEvent.DismissError) }
            )
        }
    }
}

@Composable
private fun EnableToggleCard(
    isEnabled: Boolean,
    isActive: Boolean,
    onToggle: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Auto-Detection",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = if (isActive) "Active - Monitoring location" else "Inactive",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Switch(
                checked = isEnabled,
                onCheckedChange = { onToggle() }
            )
        }
    }
}

@Composable
private fun PermissionsCard(
    hasLocation: Boolean,
    hasBackground: Boolean,
    hasNotification: Boolean,
    onRequestPermissions: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (hasLocation && hasBackground && hasNotification)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Permissions",
                style = MaterialTheme.typography.titleMedium
            )

            PermissionItem("Location", hasLocation)
            PermissionItem("Background Location", hasBackground)
            PermissionItem("Notifications", hasNotification)

            if (!hasLocation || !hasBackground || !hasNotification) {
                Button(
                    onClick = onRequestPermissions,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Grant Permissions")
                }
            }
        }
    }
}

@Composable
private fun PermissionItem(name: String, granted: Boolean) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = if (granted) "✓" else "✗",
            color = if (granted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
        )
        Text(
            text = name,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun OfficeLocationCard(
    location: com.example.go2office.domain.model.OfficeLocation?,
    onUseCurrentLocation: () -> Unit,
    onSetCustomLocation: (Double, Double, String) -> Unit
) {
    var showLocationDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Office Location",
                style = MaterialTheme.typography.titleMedium
            )

            if (location != null) {
                Text(
                    text = "📍 ${location.name}",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "Lat: ${"%.4f".format(location.latitude)}, Lon: ${"%.4f".format(location.longitude)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                Text(
                    text = "Not set",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onUseCurrentLocation,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Use Current GPS")
                }

                Button(
                    onClick = { showLocationDialog = true },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Enter Manually")
                }
            }

            Text(
                text = "💡 100% FREE - No API costs!",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }

    if (showLocationDialog) {
        SetLocationDialog(
            onDismiss = { showLocationDialog = false },
            onConfirm = { lat, lon, name ->
                onSetCustomLocation(lat, lon, name)
                showLocationDialog = false
            }
        )
    }
}

@Composable
private fun SetLocationDialog(
    onDismiss: () -> Unit,
    onConfirm: (Double, Double, String) -> Unit
) {
    var latitude by remember { mutableStateOf("") }
    var longitude by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("Main Office") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Set Office Location") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Location Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = latitude,
                    onValueChange = { latitude = it },
                    label = { Text("Latitude") },
                    placeholder = { Text("37.7749") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = longitude,
                    onValueChange = { longitude = it },
                    label = { Text("Longitude") },
                    placeholder = { Text("-122.4194") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val lat = latitude.toDoubleOrNull() ?: 0.0
                    val lon = longitude.toDoubleOrNull() ?: 0.0
                    onConfirm(lat, lon, name)
                }
            ) {
                Text("Set")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun GeofenceRadiusCard(
    radiusMeters: Float,
    onRadiusChange: (Float) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Geofence Radius",
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = "${radiusMeters.toInt()} meters",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary
            )

            Slider(
                value = radiusMeters,
                onValueChange = onRadiusChange,
                valueRange = Constants.MIN_GEOFENCE_RADIUS_METERS..Constants.MAX_GEOFENCE_RADIUS_METERS,
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("${Constants.MIN_GEOFENCE_RADIUS_METERS.toInt()}m", style = MaterialTheme.typography.labelSmall)
                Text("${Constants.MAX_GEOFENCE_RADIUS_METERS.toInt()}m", style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}

@Composable
private fun WorkHoursInfoCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Work Hours Tracking",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "⏰ Counted: ${Constants.WORK_START_HOUR}:00 AM - ${Constants.WORK_END_HOUR % 12}:00 PM",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "📊 Daily Cap: ${Constants.MAX_DAILY_HOURS.toInt()} hours maximum",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "ℹ️ Time outside this window is not counted",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}

@Composable
private fun CurrentSessionCard(startTime: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "🤖 Currently at Office",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "Since: $startTime",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

