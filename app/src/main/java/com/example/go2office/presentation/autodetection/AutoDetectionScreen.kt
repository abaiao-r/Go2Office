package com.example.go2office.presentation.autodetection
import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.go2office.R
import com.example.go2office.presentation.components.ErrorDialog
import com.example.go2office.presentation.components.LoadingIndicator
import com.example.go2office.util.Constants
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AutoDetectionScreen(
    viewModel: AutoDetectionViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToPermissions: () -> Unit = {},
    onNavigateToMapPicker: (Double?, Double?) -> Unit = { _, _ -> },
    selectedMapLocation: Triple<Double, Double, String>? = null,
    onMapLocationConsumed: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        viewModel.checkPermissions()
    }

    LaunchedEffect(selectedMapLocation) {
        if (selectedMapLocation != null) {
            viewModel.onEvent(AutoDetectionEvent.SetCustomLocation(
                selectedMapLocation.first,
                selectedMapLocation.second,
                selectedMapLocation.third
            ))
            onMapLocationConsumed()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.auto_detection_title)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.back))
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
                EnableToggleCard(
                    isEnabled = uiState.isEnabled,
                    isActive = uiState.isGeofencingActive,
                    onToggle = { viewModel.onEvent(AutoDetectionEvent.ToggleAutoDetection) }
                )
                PermissionsCard(
                    hasLocation = uiState.hasLocationPermission,
                    hasBackground = uiState.hasBackgroundPermission,
                    hasNotification = uiState.hasNotificationPermission,
                    onRequestPermissions = {
                        val permissions = mutableListOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                            permissions.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                        }
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                            permissions.add(Manifest.permission.POST_NOTIFICATIONS)
                        }
                        permissionLauncher.launch(permissions.toTypedArray())
                    },
                    onManagePermissions = onNavigateToPermissions
                )
                OfficeLocationCard(
                    location = uiState.officeLocation,
                    onUseCurrentLocation = { viewModel.onEvent(AutoDetectionEvent.UseCurrentLocation) },
                    onSetCustomLocation = { lat, lon, name ->
                        viewModel.onEvent(AutoDetectionEvent.SetCustomLocation(lat, lon, name))
                    },
                    onPickOnMap = {
                        onNavigateToMapPicker(
                            uiState.officeLocation?.latitude,
                            uiState.officeLocation?.longitude
                        )
                    }
                )
                GeofenceRadiusCard(
                    radiusMeters = uiState.geofenceRadiusMeters,
                    onRadiusChange = { viewModel.onEvent(AutoDetectionEvent.UpdateGeofenceRadius(it)) }
                )
                WorkHoursInfoCard()
                if (uiState.currentSessionStartTime != null) {
                    CurrentSessionCard(startTime = uiState.currentSessionStartTime!!)
                }
            }
        }
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
                    text = stringResource(R.string.auto_detection),
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = if (isActive) stringResource(R.string.status_active) else stringResource(R.string.status_inactive),
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
    onRequestPermissions: () -> Unit,
    onManagePermissions: () -> Unit
) {
    val allGranted = hasLocation && hasBackground && hasNotification
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (allGranted)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.permissions),
                    style = MaterialTheme.typography.titleMedium
                )
                TextButton(onClick = onManagePermissions) {
                    Text(stringResource(R.string.manage))
                }
            }
            PermissionItem(stringResource(R.string.permission_location), hasLocation)
            PermissionItem(stringResource(R.string.permission_background_location), hasBackground)
            PermissionItem(stringResource(R.string.permission_notifications), hasNotification)
            if (!allGranted) {
                Button(
                    onClick = onRequestPermissions,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.grant_permissions))
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
    onSetCustomLocation: (Double, Double, String) -> Unit,
    onPickOnMap: () -> Unit
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
                text = stringResource(R.string.office_location),
                style = MaterialTheme.typography.titleMedium
            )
            if (location != null) {
                Text(
                    text = stringResource(R.string.location_pin_format, location.name),
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = stringResource(R.string.location_coords_format, location.latitude, location.longitude),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                Text(
                    text = stringResource(R.string.not_set),
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
                    Text(stringResource(R.string.use_current_gps))
                }
                OutlinedButton(
                    onClick = { showLocationDialog = true },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(stringResource(R.string.enter_manually))
                }
            }
            Button(
                onClick = onPickOnMap,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.pick_on_map))
            }
            Text(
                text = stringResource(R.string.free_no_api_costs),
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
    val defaultOfficeName = stringResource(R.string.default_office_name)
    var latitude by remember { mutableStateOf("") }
    var longitude by remember { mutableStateOf("") }
    var name by remember { mutableStateOf(defaultOfficeName) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.set_office_location)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(stringResource(R.string.location_name)) },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = latitude,
                    onValueChange = { latitude = it },
                    label = { Text(stringResource(R.string.latitude)) },
                    placeholder = { Text(stringResource(R.string.latitude_placeholder)) },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = longitude,
                    onValueChange = { longitude = it },
                    label = { Text(stringResource(R.string.longitude)) },
                    placeholder = { Text(stringResource(R.string.longitude_placeholder)) },
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
                Text(stringResource(R.string.set))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
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
                text = stringResource(R.string.geofence_radius),
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = stringResource(R.string.meters_format, radiusMeters.toInt()),
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
                Text(stringResource(R.string.meters_format, Constants.MIN_GEOFENCE_RADIUS_METERS.toInt()), style = MaterialTheme.typography.labelSmall)
                Text(stringResource(R.string.meters_format, Constants.MAX_GEOFENCE_RADIUS_METERS.toInt()), style = MaterialTheme.typography.labelSmall)
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
                text = stringResource(R.string.work_hours_tracking),
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = stringResource(R.string.work_hours_counted, Constants.WORK_START_HOUR, Constants.WORK_END_HOUR % 12),
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = stringResource(R.string.daily_cap, Constants.MAX_DAILY_HOURS.toInt()),
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = stringResource(R.string.time_outside_not_counted),
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
                text = stringResource(R.string.currently_at_office),
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = stringResource(R.string.since_format, startTime),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
