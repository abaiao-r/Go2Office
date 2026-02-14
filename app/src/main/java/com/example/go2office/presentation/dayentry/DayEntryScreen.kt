package com.example.go2office.presentation.dayentry
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.go2office.presentation.components.ConfirmationDialog
import com.example.go2office.presentation.components.ErrorDialog
import com.example.go2office.presentation.components.LoadingIndicator
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DayEntryScreen(
    date: LocalDate,
    viewModel: DayEntryViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }
    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) {
            onNavigateBack()
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = date.format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy"))
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                actions = {
                    if (uiState.isExistingEntry) {
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(Icons.Default.Delete, "Delete")
                        }
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
                verticalArrangement = Arrangement.spacedBy(24.dp)
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
                        Column {
                            Text(
                                text = "Was in office?",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "Toggle to mark this day",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = uiState.wasInOffice,
                            onCheckedChange = {
                                viewModel.onEvent(DayEntryEvent.ToggleWasInOffice(it))
                            }
                        )
                    }
                }
                if (uiState.wasInOffice) {
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                text = "Hours Worked",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "%.1f hours".format(uiState.hoursWorked),
                                style = MaterialTheme.typography.displayMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Slider(
                                value = uiState.hoursWorked,
                                onValueChange = {
                                    viewModel.onEvent(DayEntryEvent.UpdateHours(it))
                                },
                                valueRange = 0f..24f,
                                steps = 47, 
                                modifier = Modifier.fillMaxWidth()
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("0h", style = MaterialTheme.typography.labelSmall)
                                Text("24h", style = MaterialTheme.typography.labelSmall)
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf(4f, 6f, 8f, 10f).forEach { hours ->
                                    FilterChip(
                                        selected = uiState.hoursWorked == hours,
                                        onClick = {
                                            viewModel.onEvent(DayEntryEvent.UpdateHours(hours))
                                        },
                                        label = { Text("${hours.roundToInt()}h") },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Notes (optional)",
                                style = MaterialTheme.typography.titleMedium
                            )
                            OutlinedTextField(
                                value = uiState.notes,
                                onValueChange = {
                                    viewModel.onEvent(DayEntryEvent.UpdateNotes(it))
                                },
                                placeholder = { Text("Add any notes about this day...") },
                                modifier = Modifier.fillMaxWidth(),
                                minLines = 3,
                                maxLines = 5
                            )
                        }
                    }
                }
                Button(
                    onClick = { viewModel.onEvent(DayEntryEvent.Save) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !uiState.isLoading
                ) {
                    Text(if (uiState.isExistingEntry) "Update Entry" else "Save Entry")
                }
            }
        }
        if (showDeleteDialog) {
            ConfirmationDialog(
                title = "Delete Entry",
                message = "Are you sure you want to delete this entry?",
                onConfirm = {
                    showDeleteDialog = false
                    viewModel.onEvent(DayEntryEvent.Delete)
                },
                onDismiss = { showDeleteDialog = false },
                confirmText = "Delete",
                dismissText = "Cancel"
            )
        }
        if (uiState.errorMessage != null) {
            ErrorDialog(
                message = uiState.errorMessage!!,
                onDismiss = { viewModel.onEvent(DayEntryEvent.DismissError) }
            )
        }
    }
}
