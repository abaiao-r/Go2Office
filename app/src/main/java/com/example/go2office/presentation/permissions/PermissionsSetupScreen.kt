package com.example.go2office.presentation.permissions

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat

/**
 * Dedicated screen for granting permissions one by one.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PermissionsSetupScreen(
    onNavigateBack: () -> Unit,
    onAllPermissionsGranted: () -> Unit
) {
    val context = LocalContext.current

    // Track each permission state
    var hasForegroundLocation by remember { mutableStateOf(false) }
    var hasBackgroundLocation by remember { mutableStateOf(false) }
    var hasNotifications by remember { mutableStateOf(false) }

    // Check permissions on load
    LaunchedEffect(Unit) {
        hasForegroundLocation = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED

        hasBackgroundLocation = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED
        } else {
            true // Not needed on older Android
        }

        hasNotifications = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED
        } else {
            true // Not needed on older Android
        }
    }

    // Permission launcher for foreground location
    val foregroundLocationLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasForegroundLocation = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
    }

    // Permission launcher for background location
    val backgroundLocationLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasBackgroundLocation = granted
    }

    // Permission launcher for notifications
    val notificationsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasNotifications = granted
    }

    // Check if all required permissions are granted
    val allGranted = hasForegroundLocation && hasBackgroundLocation && hasNotifications

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Setup Permissions") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Text(
                text = "Grant Permissions",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "To enable automatic office detection, we need the following permissions. Grant them one by one below.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Permission 1: Foreground Location
            PermissionCard(
                title = "Location Access",
                description = "Required to detect when you arrive at the office.",
                icon = Icons.Default.LocationOn,
                isGranted = hasForegroundLocation,
                isEnabled = true,
                onGrantClick = {
                    foregroundLocationLauncher.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                },
                instruction = "In the next dialog, choose 'While using the app' or 'Allow'"
            )

            // Permission 2: Background Location (only if foreground is granted)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                PermissionCard(
                    title = "Background Location",
                    description = "Allows detection even when the app is closed.",
                    icon = Icons.Default.LocationOn,
                    isGranted = hasBackgroundLocation,
                    isEnabled = hasForegroundLocation,
                    onGrantClick = {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            backgroundLocationLauncher.launch(
                                Manifest.permission.ACCESS_BACKGROUND_LOCATION
                            )
                        }
                    },
                    instruction = "Choose 'Allow all the time' for best results",
                    note = if (!hasForegroundLocation) "Grant Location Access first" else null
                )
            }

            // Permission 3: Notifications
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                PermissionCard(
                    title = "Notifications",
                    description = "Get notified when you arrive/leave the office.",
                    icon = Icons.Default.Notifications,
                    isGranted = hasNotifications,
                    isEnabled = true,
                    onGrantClick = {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            notificationsLauncher.launch(
                                Manifest.permission.POST_NOTIFICATIONS
                            )
                        }
                    },
                    instruction = "Choose 'Allow' to receive notifications"
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Continue button (only enabled when all granted)
            if (allGranted) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "All permissions granted!",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Auto-detection is ready to use",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }

            Button(
                onClick = onAllPermissionsGranted,
                modifier = Modifier.fillMaxWidth(),
                enabled = allGranted
            ) {
                Text(if (allGranted) "Continue" else "Grant all permissions above to continue")
            }
        }
    }
}

@Composable
private fun PermissionCard(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isGranted: Boolean,
    isEnabled: Boolean,
    onGrantClick: () -> Unit,
    instruction: String,
    note: String? = null
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isGranted) {
                MaterialTheme.colorScheme.primaryContainer
            } else if (!isEnabled) {
                MaterialTheme.colorScheme.surfaceVariant
            } else {
                MaterialTheme.colorScheme.errorContainer
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = if (isGranted) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (isGranted) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Granted",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            if (!isGranted) {
                Text(
                    text = "💡 $instruction",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(start = 44.dp)
                )

                if (note != null) {
                    Text(
                        text = "⚠️ $note",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(start = 44.dp)
                    )
                }

                Button(
                    onClick = onGrantClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 44.dp),
                    enabled = isEnabled
                ) {
                    Text("Grant Permission")
                }
            }
        }
    }
}

