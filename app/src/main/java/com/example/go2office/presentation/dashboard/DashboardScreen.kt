package com.example.go2office.presentation.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.go2office.domain.model.DailyEntry
import com.example.go2office.domain.model.MonthProgress
import com.example.go2office.domain.model.OfficePresence
import com.example.go2office.domain.model.SuggestedDay
import com.example.go2office.presentation.components.EmptyState
import com.example.go2office.presentation.components.ErrorDialog
import com.example.go2office.presentation.components.LoadingIndicator
import com.example.go2office.util.DateUtils
import kotlinx.coroutines.delay
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.time.temporal.ChronoUnit
import java.util.*

/**
 * Dashboard screen showing monthly progress and suggestions.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = hiltViewModel(),
    onNavigateToDayEntry: (LocalDate) -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Go2Office") },
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, "Settings")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onNavigateToDayEntry(LocalDate.now()) }
            ) {
                Icon(Icons.Default.Add, "Add entry")
            }
        }
    ) { padding ->
        when {
            uiState.isLoading -> {
                LoadingIndicator()
            }

            uiState.monthProgress == null -> {
                EmptyState(
                    message = "No data available for this month",
                    actionText = "Refresh",
                    onAction = { viewModel.onEvent(DashboardEvent.Refresh) }
                )
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Month selector
                    item {
                        MonthSelectorCard(
                            selectedMonth = uiState.selectedMonth,
                            onMonthChanged = { viewModel.onEvent(DashboardEvent.SelectMonth(it)) }
                        )
                    }

                    // Progress overview
                    item {
                        ProgressOverviewCard(progress = uiState.monthProgress!!)
                    }

                    // Currently at office timer (shows if there's an active session)
                    if (uiState.activeSession != null) {
                        item {
                            CurrentlyAtOfficeCard(session = uiState.activeSession!!)
                        }
                    }

                    // Suggested days
                    if (uiState.suggestedDays.isNotEmpty()) {
                        item {
                            SuggestedDaysSection(
                                suggestions = uiState.suggestedDays,
                                onDayClick = onNavigateToDayEntry
                            )
                        }
                    }

                    // Recent entries
                    if (uiState.recentEntries.isNotEmpty()) {
                        item {
                            RecentEntriesSection(
                                entries = uiState.recentEntries,
                                onEntryClick = onNavigateToDayEntry
                            )
                        }
                    }
                }
            }
        }

        // Error dialog
        if (uiState.errorMessage != null) {
            ErrorDialog(
                message = uiState.errorMessage!!,
                onDismiss = { viewModel.onEvent(DashboardEvent.DismissError) }
            )
        }
    }
}

@Composable
private fun MonthSelectorCard(
    selectedMonth: YearMonth,
    onMonthChanged: (YearMonth) -> Unit
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
            IconButton(onClick = { onMonthChanged(selectedMonth.minusMonths(1)) }) {
                Text("◀")
            }

            Text(
                text = selectedMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy")),
                style = MaterialTheme.typography.titleLarge
            )

            IconButton(
                onClick = { onMonthChanged(selectedMonth.plusMonths(1)) },
                enabled = !selectedMonth.isAfter(YearMonth.now())
            ) {
                Text("▶")
            }
        }
    }
}

@Composable
private fun ProgressOverviewCard(progress: MonthProgress) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Monthly Progress",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            // Days progress
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Office Days",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "${progress.completedDays} / ${progress.requiredDays}",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }

                LinearProgressIndicator(
                    progress = { progress.daysPercentComplete / 100f },
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = "${progress.remainingDays} days remaining",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            Divider()

            // Hours progress
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Office Hours",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "%.1fh / %.1fh".format(progress.completedHours, progress.requiredHours),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }

                LinearProgressIndicator(
                    progress = { progress.hoursPercentComplete / 100f },
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = "%.1fh remaining".format(progress.remainingHours),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            if (progress.isComplete) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.tertiary
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("🎉", style = MaterialTheme.typography.titleLarge)
                        Text(
                            text = "You've met your monthly requirement!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onTertiary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SuggestedDaysSection(
    suggestions: List<SuggestedDay>,
    onDayClick: (LocalDate) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Suggested Days",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (suggestions.isNotEmpty()) {
                        "Complete these ${suggestions.size} days to meet requirements"
                    } else {
                        "No more days needed - requirements met!"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Show ALL suggestions in a vertical list (not just horizontal scroll)
        if (suggestions.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    suggestions.forEachIndexed { index, suggestion ->
                        SuggestedDayRow(
                            suggestion = suggestion,
                            index = index + 1,
                            onClick = { onDayClick(suggestion.date) }
                        )
                        if (index < suggestions.size - 1) {
                            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SuggestedDayRow(
    suggestion: SuggestedDay,
    index: Int,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = when (suggestion.priority) {
                0 -> MaterialTheme.colorScheme.primaryContainer
                in 1..2 -> MaterialTheme.colorScheme.secondaryContainer
                else -> MaterialTheme.colorScheme.surfaceVariant
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Number badge
            Surface(
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = index.toString(),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }

            // Date info
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = suggestion.date.format(java.time.format.DateTimeFormatter.ofPattern("EEE, dd MMM")),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    if (suggestion.priority == 0) {
                        Surface(
                            shape = MaterialTheme.shapes.extraSmall,
                            color = MaterialTheme.colorScheme.tertiary
                        ) {
                            Text(
                                text = "⭐ TOP",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onTertiary,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                Text(
                    text = suggestion.reason,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Arrow
            Text(
                text = "›",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun RecentEntriesSection(
    entries: List<DailyEntry>,
    onEntryClick: (LocalDate) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Recent Entries",
            style = MaterialTheme.typography.titleLarge
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            entries.forEach { entry ->
                RecentEntryCard(
                    entry = entry,
                    onClick = { onEntryClick(entry.date) }
                )
            }
        }
    }
}

@Composable
private fun RecentEntryCard(
    entry: DailyEntry,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = entry.date.format(DateTimeFormatter.ofPattern("EEE, MMM d")),
                    style = MaterialTheme.typography.titleMedium
                )

                if (entry.wasInOffice) {
                    Text(
                        text = "%.1fh in office".format(entry.hoursWorked),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    Text(
                        text = "Not in office",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (entry.wasInOffice) {
                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = "✓",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun CurrentlyAtOfficeCard(session: OfficePresence) {
    // State to force recomposition every minute
    var currentTime by remember { mutableStateOf(LocalDateTime.now()) }

    // Update every minute
    LaunchedEffect(Unit) {
        while (true) {
            delay(60_000) // 60 seconds
            currentTime = LocalDateTime.now()
        }
    }

    // Calculate duration
    val entryTime = LocalDateTime.parse(session.entryTime)
    val duration = ChronoUnit.MINUTES.between(entryTime, currentTime)
    val hours = duration / 60
    val minutes = duration % 60
    val durationText = if (hours > 0) {
        "${hours}h ${minutes}m"
    } else {
        "${minutes}m"
    }

    // Format entry time (extract HH:mm from ISO string)
    val entryTimeFormatted = try {
        entryTime.format(DateTimeFormatter.ofPattern("HH:mm"))
    } catch (e: Exception) {
        "00:00"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                )
                Column {
                    Text(
                        text = "Currently at office",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                    Text(
                        text = "Since $entryTimeFormatted",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = durationText,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "elapsed",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
            }
        }
    }
}
