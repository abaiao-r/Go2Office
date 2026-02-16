package com.example.go2office.presentation.onboarding
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.go2office.presentation.components.ErrorDialog
import com.example.go2office.presentation.components.LoadingIndicator
import java.time.DayOfWeek
import java.time.LocalDate
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(
    viewModel: OnboardingViewModel = hiltViewModel(),
    onComplete: () -> Unit,
    onNavigateToPermissions: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    LaunchedEffect(uiState.isComplete) {
        if (uiState.isComplete) {
            onComplete()
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Setup Go2Office") },
                navigationIcon = {
                    if (uiState.currentStep > 0) {
                        IconButton(onClick = { viewModel.onEvent(OnboardingEvent.PreviousStep) }) {
                            Icon(Icons.Default.ArrowBack, "Back")
                        }
                    }
                }
            )
        }
    ) { padding ->
        if (uiState.isLoading) {
            LoadingIndicator(message = "Saving settings...")
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    LinearProgressIndicator(
                        progress = { (uiState.currentStep + 1).toFloat() / uiState.totalSteps },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        text = "Step ${uiState.currentStep + 1} of ${uiState.totalSteps}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    when (uiState.currentStep) {
                        0 -> RequiredDaysStep(
                            selectedDays = uiState.requiredDaysPerWeek,
                            onDaysSelected = { viewModel.onEvent(OnboardingEvent.UpdateRequiredDays(it)) }
                        )
                        1 -> RequiredHoursStep(
                            selectedHours = uiState.hoursPerDay,  
                            requiredDays = uiState.requiredDaysPerWeek,  
                            onHoursChanged = { viewModel.onEvent(OnboardingEvent.UpdateHoursPerDay(it)) }  
                        )
                        2 -> WeekdayPreferencesStep(
                            preferences = uiState.weekdayPreferences,
                            onPreferencesChanged = { viewModel.onEvent(OnboardingEvent.UpdateWeekdayPreferences(it)) }
                        )
                        3 -> AutoDetectionStep(
                            viewModel = viewModel,
                            uiState = uiState,
                            onNavigateToPermissions = onNavigateToPermissions
                        )
                        4 -> HolidaysSetupStep(
                            viewModel = viewModel,
                            uiState = uiState
                        )
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (uiState.currentStep > 0) {
                        OutlinedButton(
                            onClick = { viewModel.onEvent(OnboardingEvent.PreviousStep) },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Back")
                        }
                    }
                    Button(
                        onClick = {
                            if (uiState.currentStep == uiState.totalSteps - 1) {
                                viewModel.onEvent(OnboardingEvent.Complete)
                            } else {
                                viewModel.onEvent(OnboardingEvent.NextStep)
                            }
                        },
                        enabled = uiState.canGoNext,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(if (uiState.currentStep == uiState.totalSteps - 1) "Complete" else "Next")
                    }
                }
            }
        }
        if (uiState.errorMessage != null) {
            ErrorDialog(
                message = uiState.errorMessage!!,
                onDismiss = { viewModel.onEvent(OnboardingEvent.DismissError) }
            )
        }
    }
}
@Composable
private fun RequiredDaysStep(
    selectedDays: Int,
    onDaysSelected: (Int) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Required Office Days",
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = "How many days per week do you need to work from the office?",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "$selectedDays",
                    style = MaterialTheme.typography.displayLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = if (selectedDays == 1) "day per week" else "days per week",
                    style = MaterialTheme.typography.titleMedium
                )
                Slider(
                    value = selectedDays.toFloat(),
                    onValueChange = { onDaysSelected(it.toInt()) },
                    valueRange = 1f..5f,
                    steps = 3,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("1", style = MaterialTheme.typography.labelSmall)
                    Text("5", style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}
@Composable
private fun RequiredHoursStep(
    selectedHours: Float,
    requiredDays: Int,
    onHoursChanged: (Float) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Hours Per Day",
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = "How many hours do you typically work at the office each day?",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "%.1f".format(selectedHours),
                    style = MaterialTheme.typography.displayLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "hours per day",
                    style = MaterialTheme.typography.titleMedium
                )
                Slider(
                    value = selectedHours,
                    onValueChange = onHoursChanged,
                    valueRange = 1f..12f,  
                    modifier = Modifier.fillMaxWidth()
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("1h", style = MaterialTheme.typography.labelSmall)
                    Text("12h", style = MaterialTheme.typography.labelSmall)
                }
                HorizontalDivider()
                val weeklyHours = selectedHours * requiredDays
                Text(
                    text = "Weekly total: %.1fh (%.1fh × %d days)".format(weeklyHours, selectedHours, requiredDays),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
@Composable
private fun WeekdayPreferencesStep(
    preferences: List<DayOfWeek>,
    onPreferencesChanged: (List<DayOfWeek>) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Day Preferences",
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = "Order your preferred office days from most to least preferred (drag to reorder):",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                preferences.forEachIndexed { index, day ->
                    WeekdayPreferenceItem(
                        dayOfWeek = day,
                        rank = index + 1,
                        onMoveUp = {
                            if (index > 0) {
                                val newList = preferences.toMutableList()
                                newList[index] = preferences[index - 1]
                                newList[index - 1] = day
                                onPreferencesChanged(newList)
                            }
                        },
                        onMoveDown = {
                            if (index < preferences.size - 1) {
                                val newList = preferences.toMutableList()
                                newList[index] = preferences[index + 1]
                                newList[index + 1] = day
                                onPreferencesChanged(newList)
                            }
                        },
                        canMoveUp = index > 0,
                        canMoveDown = index < preferences.size - 1
                    )
                }
            }
        }
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "💡",
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = "The app will suggest your top-preference days first when you need to meet your monthly requirement.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}
@Composable
private fun WeekdayPreferenceItem(
    dayOfWeek: DayOfWeek,
    rank: Int,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit,
    canMoveUp: Boolean,
    canMoveDown: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "$rank",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
                Text(
                    text = dayOfWeek.name.lowercase().replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.titleMedium
                )
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                IconButton(
                    onClick = onMoveUp,
                    enabled = canMoveUp
                ) {
                    Text("↑")
                }
                IconButton(
                    onClick = onMoveDown,
                    enabled = canMoveDown
                ) {
                    Text("↓")
                }
            }
        }
    }
}
@Composable
private fun AutoDetectionStep(
    viewModel: OnboardingViewModel,
    uiState: OnboardingUiState,
    onNavigateToPermissions: () -> Unit
) {
    LaunchedEffect(uiState.currentStep) {
        if (uiState.currentStep == 3) { 
            viewModel.checkLocationPermission()
        }
    }
    var showLocationDialog by remember { mutableStateOf(false) }
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Auto-Detection (Optional)",
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = "Automatically track when you arrive and leave the office using GPS.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Card(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Enable Auto-Detection",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "Track office hours automatically",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = uiState.enableAutoDetection,
                    onCheckedChange = { viewModel.onEvent(OnboardingEvent.ToggleAutoDetection(it)) }
                )
            }
        }
        if (uiState.enableAutoDetection) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (uiState.hasLocationPermission) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        MaterialTheme.colorScheme.errorContainer
                    }
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = if (uiState.hasLocationPermission) {
                                Icons.Default.Check
                            } else {
                                Icons.Default.Settings
                            },
                            contentDescription = null,
                            modifier = Modifier.size(32.dp)
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (uiState.hasLocationPermission) {
                                    "✅ Permissions Configured"
                                } else {
                                    "⚠️ Permissions Required"
                                },
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (uiState.hasLocationPermission) {
                                    "You can review and grant additional permissions"
                                } else {
                                    "Grant permissions to enable auto-detection"
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Button(
                        onClick = onNavigateToPermissions,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (uiState.hasLocationPermission) {
                                "Review Permissions"
                            } else {
                                "Setup Permissions"
                            }
                        )
                    }
                }
            }
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Office Location",
                        style = MaterialTheme.typography.titleMedium
                    )
                    if (uiState.officeLatitude != null && uiState.officeLongitude != null) {
                        Text("📍 ${uiState.officeName}")
                        Text(
                            text = "Lat: %.4f, Lon: %.4f".format(uiState.officeLatitude, uiState.officeLongitude),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        Text(
                            text = "Not set",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { viewModel.onEvent(OnboardingEvent.UseCurrentLocation) },
                            modifier = Modifier.weight(1f),
                            enabled = uiState.hasLocationPermission
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
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("💡", style = MaterialTheme.typography.titleLarge)
                    Column {
                        Text(
                            text = "Work Hours: 7 AM - 7 PM",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "Daily Cap: 10 hours",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "You can change these settings later.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
            }
        }
        if (!uiState.enableAutoDetection) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "✓ You can enable auto-detection later in Settings",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
    if (showLocationDialog) {
        SetLocationDialog(
            onDismiss = { showLocationDialog = false },
            onConfirm = { lat, lon, name ->
                viewModel.onEvent(OnboardingEvent.SetOfficeLocation(lat, lon, name))
                showLocationDialog = false
            }
        )
    }
}
private fun parseDmsToDecimal(dms: String): Double? {
    val cleanDms = dms.trim().uppercase()
    val dmsRegex = """(\d+)[°]\s*(\d+)[′']\s*(\d+(?:\.\d+)?)[″"]\s*([NSEW])""".toRegex()
    val match = dmsRegex.find(cleanDms)
    if (match != null) {
        val (deg, min, sec, dir) = match.destructured
        val decimal = deg.toDouble() + min.toDouble() / 60 + sec.toDouble() / 3600
        return if (dir == "S" || dir == "W") -decimal else decimal
    }
    return cleanDms.toDoubleOrNull()
}

private fun parseCoordinates(input: String): Pair<Double, Double>? {
    val trimmed = input.trim()
    val dmsPattern = """(\d+[°]\s*\d+[′']\s*\d+(?:\.\d+)?[″"]\s*[NS])\s*[,\s]\s*(\d+[°]\s*\d+[′']\s*\d+(?:\.\d+)?[″"]\s*[EW])""".toRegex(RegexOption.IGNORE_CASE)
    val dmsMatch = dmsPattern.find(trimmed)
    if (dmsMatch != null) {
        val lat = parseDmsToDecimal(dmsMatch.groupValues[1])
        val lon = parseDmsToDecimal(dmsMatch.groupValues[2])
        if (lat != null && lon != null) return Pair(lat, lon)
    }
    val decimalPattern = """(-?\d+\.?\d*)\s*[,\s]\s*(-?\d+\.?\d*)""".toRegex()
    val decimalMatch = decimalPattern.find(trimmed)
    if (decimalMatch != null) {
        val lat = decimalMatch.groupValues[1].toDoubleOrNull()
        val lon = decimalMatch.groupValues[2].toDoubleOrNull()
        if (lat != null && lon != null) return Pair(lat, lon)
    }
    return null
}

@Composable
private fun SetLocationDialog(
    onDismiss: () -> Unit,
    onConfirm: (Double, Double, String) -> Unit
) {
    var coordinates by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("Main Office") }
    var parseError by remember { mutableStateOf(false) }
    val parsedCoords = remember(coordinates) { parseCoordinates(coordinates) }
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
                    value = coordinates,
                    onValueChange = {
                        coordinates = it
                        parseError = false
                    },
                    label = { Text("Coordinates") },
                    placeholder = { Text("38.7707, -9.0972") },
                    isError = parseError,
                    supportingText = if (parseError) {{ Text("Invalid format", color = MaterialTheme.colorScheme.error) }} else null,
                    modifier = Modifier.fillMaxWidth()
                )
                if (parsedCoords != null) {
                    Text(
                        text = "✓ Parsed: ${String.format("%.6f", parsedCoords.first)}, ${String.format("%.6f", parsedCoords.second)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Text(
                    text = "Supported formats:",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "• Decimal: 38.7707, -9.0972\n• DMS: 38°46'14.52\"N 9°05'49.89\"W",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val parsed = parseCoordinates(coordinates)
                    if (parsed != null) {
                        onConfirm(parsed.first, parsed.second, name)
                    } else {
                        parseError = true
                    }
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
private fun HolidaysSetupStep(
    viewModel: OnboardingViewModel,
    uiState: OnboardingUiState
) {
    var showQuickAddDialog by remember { mutableStateOf(false) }
    var showCountryDialog by remember { mutableStateOf(false) }
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Holidays & Vacations (Optional)",
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = "Configure public holidays and vacation days. These will NOT count toward your required office days.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "💡",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(end = 12.dp)
                    )
                    Column {
                        Text(
                            text = "Why configure holidays?",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Holidays and vacations reduce your monthly requirements automatically!",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
                HorizontalDivider()
                Text("Example:", style = MaterialTheme.typography.labelLarge)
                Text(
                    text = "• December: 23 work days\n• Holidays: 2 (Christmas, New Year)\n• Required: 13 days (instead of 14)",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Text(
            text = "Quick Setup:",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 8.dp)
        )
        Button(
            onClick = { showCountryDialog = true },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary
            )
        ) {
            Icon(Icons.Default.Place, "Country")
            Spacer(Modifier.width(8.dp))
            Text("🌍 Load Country Holidays (100+ countries)")
        }
        OutlinedButton(
            onClick = { showQuickAddDialog = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Add, "Add")
            Spacer(Modifier.width(8.dp))
            Text("Add Single Holiday or Vacation")
        }
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ℹ️",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(end = 16.dp)
                )
                Column {
                    Text(
                        text = "You can skip this for now",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Configure anytime in Settings → Annual Calendar",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
    if (showCountryDialog) {
        SimpleCountryDialog(
            viewModel = viewModel,
            onDismiss = { showCountryDialog = false }
        )
    }
    if (showQuickAddDialog) {
        QuickAddHolidayDialog(
            onDismiss = { showQuickAddDialog = false },
            onAdd = { date, description, isVacation ->
                viewModel.onEvent(OnboardingEvent.AddHoliday(date, description, isVacation))
                showQuickAddDialog = false
            }
        )
    }
}
@Composable
private fun QuickAddHolidayDialog(
    onDismiss: () -> Unit,
    onAdd: (LocalDate, String, Boolean) -> Unit
) {
    var date by remember { mutableStateOf(LocalDate.now()) }
    var description by remember { mutableStateOf("") }
    var isVacation by remember { mutableStateOf(false) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add ${if (isVacation) "Vacation" else "Holiday"}") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Date:", style = MaterialTheme.typography.labelMedium)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { date = date.minusDays(1) }) {
                        Icon(Icons.Default.KeyboardArrowLeft, "Previous")
                    }
                    Text(
                        text = date.format(java.time.format.DateTimeFormatter.ofPattern("dd MMM yyyy")),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = { date = date.plusDays(1) }) {
                        Icon(Icons.Default.KeyboardArrowRight, "Next")
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { date = LocalDate.now() },
                        modifier = Modifier.weight(1f)
                    ) { Text("Today") }
                    OutlinedButton(
                        onClick = { date = LocalDate.now().plusDays(1) },
                        modifier = Modifier.weight(1f)
                    ) { Text("Tomorrow") }
                }
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    placeholder = { Text(if (isVacation) "e.g., Summer Vacation" else "e.g., Christmas") },
                    modifier = Modifier.fillMaxWidth()
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Switch(checked = isVacation, onCheckedChange = { isVacation = it })
                    Spacer(Modifier.width(8.dp))
                    Text(if (isVacation) "🏖️ Vacation Day" else "🎉 Public Holiday")
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onAdd(date, description, isVacation) },
                enabled = description.isNotBlank()
            ) { Text("Add") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
@Composable
private fun SimpleCountryDialog(
    viewModel: OnboardingViewModel,
    onDismiss: () -> Unit
) {
    val popularCountries = listOf(
        "PT" to "🇵🇹 Portugal",
        "ES" to "🇪🇸 Spain",
        "BR" to "🇧🇷 Brazil",
        "US" to "🇺🇸 United States",
        "GB" to "🇬🇧 United Kingdom",
        "FR" to "🇫🇷 France",
        "DE" to "🇩🇪 Germany",
        "IT" to "🇮🇹 Italy"
    )
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Load Country Holidays") },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    Text(
                        "Select your country to load official public holidays:",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                items(popularCountries) { (code, name) ->
                    OutlinedButton(
                        onClick = {
                            viewModel.loadCountryHolidays(code, name)
                            onDismiss()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(name)
                            Text(code, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
