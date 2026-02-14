package com.example.go2office.presentation.settings
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
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToAutoDetection: () -> Unit = {},
    onNavigateToAnnualCalendar: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) {
            onNavigateBack()
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
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
        } else if (uiState.settings != null) {
            val settings = uiState.settings!!
            var requiredDays by remember { mutableStateOf(settings.requiredDaysPerWeek) }
            var hoursPerDay by remember {
                mutableStateOf(settings.requiredHoursPerWeek / settings.requiredDaysPerWeek)
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Office Requirements",
                    style = MaterialTheme.typography.headlineSmall
                )
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer
                    ),
                    onClick = onNavigateToAutoDetection
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "🤖 Auto-Detection",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "Automatically track office hours",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        Text("›", style = MaterialTheme.typography.headlineMedium)
                    }
                }
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    ),
                    onClick = onNavigateToAnnualCalendar
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "📅 Annual Calendar",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "Manage holidays and vacations",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        Text("›", style = MaterialTheme.typography.headlineMedium)
                    }
                }
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "Required Days Per Week",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "$requiredDays days",
                            style = MaterialTheme.typography.displaySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Slider(
                            value = requiredDays.toFloat(),
                            onValueChange = { requiredDays = it.toInt() },
                            valueRange = 1f..5f,
                            steps = 3,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "Hours Per Day",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "%.1f hours".format(hoursPerDay),
                            style = MaterialTheme.typography.displaySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Slider(
                            value = hoursPerDay,
                            onValueChange = { hoursPerDay = it },
                            valueRange = 1f..12f,  
                            modifier = Modifier.fillMaxWidth()
                        )
                        HorizontalDivider()
                        val weeklyHours = hoursPerDay * requiredDays
                        Text(
                            text = "Weekly total: %.1fh (%.1fh × %d days)".format(weeklyHours, hoursPerDay, requiredDays),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Current Preferences",
                            style = MaterialTheme.typography.titleMedium
                        )
                        settings.weekdayPreferences.forEachIndexed { index, day ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Surface(
                                    shape = MaterialTheme.shapes.small,
                                    color = MaterialTheme.colorScheme.tertiary,
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Box(contentAlignment = androidx.compose.ui.Alignment.Center) {
                                        Text(
                                            text = "${index + 1}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onTertiary
                                        )
                                    }
                                }
                                Text(
                                    text = day.name.lowercase().replaceFirstChar { it.uppercase() },
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
                Button(
                    onClick = {
                        val weeklyHours = hoursPerDay * requiredDays
                        viewModel.onEvent(
                            SettingsEvent.UpdateSettings(
                                settings.copy(
                                    requiredDaysPerWeek = requiredDays,
                                    requiredHoursPerWeek = weeklyHours  
                                )
                            )
                        )
                        viewModel.onEvent(SettingsEvent.Save)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Save Changes")
                }
            }
        }
        if (uiState.errorMessage != null) {
            ErrorDialog(
                message = uiState.errorMessage!!,
                onDismiss = { viewModel.onEvent(SettingsEvent.DismissError) }
            )
        }
    }
}
