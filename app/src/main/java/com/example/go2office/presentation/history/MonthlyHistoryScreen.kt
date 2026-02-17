package com.example.go2office.presentation.history

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.go2office.R
import com.example.go2office.domain.model.OfficeSession
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonthlyHistoryScreen(
    viewModel: MonthlyHistoryViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToDayEntry: (LocalDate) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.monthly_history_title)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, stringResource(R.string.back))
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                MonthSelector(
                    selectedMonth = uiState.selectedMonth,
                    onMonthChanged = { viewModel.onEvent(MonthlyHistoryEvent.SelectMonth(it)) }
                )
            }

            item {
                MonthlySummaryCard(
                    totalDays = uiState.totalDaysWorked,
                    totalHours = uiState.totalHoursWorked,
                    requiredDays = uiState.requiredDays,
                    requiredHours = uiState.requiredHours
                )
            }

            item {
                Text(
                    text = stringResource(R.string.daily_breakdown),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            if (uiState.dailyEntries.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = stringResource(R.string.no_office_days),
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                items(uiState.dailyEntries) { entry ->
                    DailyHistoryCard(
                        date = entry.date,
                        hours = entry.hoursWorked,
                        wasInOffice = entry.wasInOffice,
                        sessions = entry.sessions,
                        isExpanded = uiState.expandedDate == entry.date,
                        onToggleExpand = { viewModel.onEvent(MonthlyHistoryEvent.ToggleExpanded(entry.date)) },
                        onClick = { onNavigateToDayEntry(entry.date) }
                    )
                }
            }
        }
    }
}

@Composable
private fun MonthSelector(
    selectedMonth: YearMonth,
    onMonthChanged: (YearMonth) -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { onMonthChanged(selectedMonth.minusMonths(1)) }) {
                Text("◀", style = MaterialTheme.typography.titleLarge)
            }
            Text(
                text = selectedMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy")),
                style = MaterialTheme.typography.titleLarge
            )
            IconButton(
                onClick = { onMonthChanged(selectedMonth.plusMonths(1)) },
                enabled = !selectedMonth.isAfter(YearMonth.now())
            ) {
                Text("▶", style = MaterialTheme.typography.titleLarge)
            }
        }
    }
}

@Composable
private fun MonthlySummaryCard(
    totalDays: Int,
    totalHours: Float,
    requiredDays: Int,
    requiredHours: Float
) {
    val totalMinutes = (totalHours * 60).toInt()
    val totalH = totalMinutes / 60
    val totalM = totalMinutes % 60

    val requiredMinutes = (requiredHours * 60).toInt()
    val reqH = requiredMinutes / 60
    val reqM = requiredMinutes % 60

    val avgMinutes = if (totalDays > 0) totalMinutes / totalDays else 0
    val avgH = avgMinutes / 60
    val avgM = avgMinutes % 60

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
                text = "Monthly Summary",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                SummaryItem(
                    label = "Days",
                    value = "$totalDays",
                    subtitle = "of $requiredDays required"
                )
                SummaryItem(
                    label = "Hours",
                    value = "${totalH}h ${totalM}m",
                    subtitle = "of ${reqH}h ${reqM}m required"
                )
                SummaryItem(
                    label = "Avg/Day",
                    value = "${avgH}h ${avgM}m",
                    subtitle = "per day"
                )
            }

            val daysProgress = if (requiredDays > 0) totalDays.toFloat() / requiredDays else 0f
            val hoursProgress = if (requiredHours > 0) totalHours / requiredHours else 0f

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Days Progress", style = MaterialTheme.typography.labelMedium)
                    Text("${(daysProgress * 100).toInt()}%", style = MaterialTheme.typography.labelMedium)
                }
                LinearProgressIndicator(
                    progress = { daysProgress.coerceIn(0f, 1f) },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Hours Progress", style = MaterialTheme.typography.labelMedium)
                    Text("${(hoursProgress * 100).toInt()}%", style = MaterialTheme.typography.labelMedium)
                }
                LinearProgressIndicator(
                    progress = { hoursProgress.coerceIn(0f, 1f) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun SummaryItem(
    label: String,
    value: String,
    subtitle: String
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
        )
    }
}

@Composable
private fun DailyHistoryCard(
    date: LocalDate,
    hours: Float,
    wasInOffice: Boolean,
    sessions: List<OfficeSession>,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (wasInOffice) {
                MaterialTheme.colorScheme.secondaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }
        )
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = date.format(DateTimeFormatter.ofPattern("EEEE")),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = date.format(DateTimeFormatter.ofPattern("dd MMMM yyyy")),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (wasInOffice) {
                    val totalMinutes = (hours * 60).toInt()
                    val h = totalMinutes / 60
                    val m = totalMinutes % 60
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "${h}h ${m}m",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        if (sessions.isNotEmpty()) {
                            Text(
                                text = "${sessions.size} ${if (sessions.size == 1) "session" else "sessions"}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        } else {
                            Text(
                                text = "✓ In Office",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    if (sessions.isNotEmpty()) {
                        IconButton(onClick = onToggleExpand) {
                            Icon(
                                imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = if (isExpanded) "Collapse" else "Expand"
                            )
                        }
                    }
                } else {
                    Text(
                        text = "—",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            AnimatedVisibility(
                visible = isExpanded && sessions.isNotEmpty(),
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    HorizontalDivider()
                    Text(
                        text = "Sessions",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                    sessions.forEachIndexed { index, session ->
                        SessionRow(session = session, index = index + 1)
                    }
                }
            }
        }
    }
}

@Composable
private fun SessionRow(session: OfficeSession, index: Int) {
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    val entryTimeStr = session.entryTime.format(timeFormatter)
    val exitTimeStr = session.exitTime?.format(timeFormatter) ?: "Active"

    val durationMinutes = session.durationMinutes
    val durationH = durationMinutes / 60
    val durationM = durationMinutes % 60
    val durationStr = if (session.isActive) {
        "ongoing"
    } else {
        "${durationH}h ${durationM}m"
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = MaterialTheme.shapes.small,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
            ) {
                Text(
                    text = "#$index",
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Column {
                Text(
                    text = "$entryTimeStr → $exitTimeStr",
                    style = MaterialTheme.typography.bodyMedium
                )
                if (session.isAutoDetected) {
                    Text(
                        text = "Auto-detected",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        Text(
            text = durationStr,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = if (session.isActive) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onSurface
        )
    }
}

