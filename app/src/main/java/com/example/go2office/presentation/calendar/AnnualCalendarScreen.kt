package com.example.go2office.presentation.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.go2office.domain.model.Holiday
import com.example.go2office.domain.model.HolidayType
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnnualCalendarScreen(
    viewModel: AnnualCalendarViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var showCountryDialog by remember { mutableStateOf(false) }
    var showAddDialog by remember { mutableStateOf(false) }
    var showAddVacationDialog by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Holidays & Vacation ${uiState.selectedYear}") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.changeYear(uiState.selectedYear - 1) }) {
                        Icon(Icons.Default.KeyboardArrowLeft, "Previous")
                    }
                    Text(uiState.selectedYear.toString(), style = MaterialTheme.typography.titleMedium)
                    IconButton(onClick = { viewModel.changeYear(uiState.selectedYear + 1) }) {
                        Icon(Icons.Default.KeyboardArrowRight, "Next")
                    }
                }
            )
        },
        floatingActionButton = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.End
            ) {
                // Add Vacation button - CLEAR
                ExtendedFloatingActionButton(
                    onClick = { showAddVacationDialog = true },
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                ) {
                    Icon(Icons.Default.DateRange, "Vacation")
                    Spacer(Modifier.width(8.dp))
                    Text("Add Vacation")
                }

                // Add Holiday button - CLEAR
                ExtendedFloatingActionButton(
                    onClick = { showAddDialog = true },
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                ) {
                    Icon(Icons.Default.Add, "Holiday")
                    Spacer(Modifier.width(8.dp))
                    Text("Add Holiday")
                }

                // Load Country button - CLEAR
                ExtendedFloatingActionButton(
                    onClick = { showCountryDialog = true },
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ) {
                    Icon(Icons.Default.Place, "Country")
                    Spacer(Modifier.width(8.dp))
                    Text("Load Country")
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                SummaryCard(
                    publicHolidays = uiState.holidays.count { it.type == HolidayType.PUBLIC_HOLIDAY },
                    vacationDays = uiState.holidays.count { it.type == HolidayType.VACATION },
                    country = uiState.selectedCountry,
                    onUnloadCountry = { viewModel.unloadCountryHolidays() }
                )
            }

            items((1..12).toList()) { month ->
                MonthCard(
                    yearMonth = YearMonth.of(uiState.selectedYear, month),
                    holidays = uiState.holidays.filter { it.date.year == uiState.selectedYear && it.date.monthValue == month },
                    onDateClick = { date ->
                        selectedDate = date
                        val existing = uiState.holidays.find { it.date == date }
                        if (existing != null) {
                            viewModel.removeHoliday(existing.id)
                        } else {
                            showAddDialog = true
                        }
                    }
                )
            }
        }
    }

    if (showCountryDialog) {
        AlertDialog(
            onDismissRequest = { showCountryDialog = false },
            title = { Text("Load Country Holidays") },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    if (uiState.isLoadingCountries) {
                        Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    } else if (uiState.availableCountries.isNotEmpty()) {
                        // Popular countries at top
                        val popularCodes = setOf("PT", "ES", "BR", "US", "GB", "FR", "DE", "IT")
                        val popularCountries = uiState.availableCountries.filter { it.countryCode in popularCodes }
                        val otherCountries = uiState.availableCountries.filter { it.countryCode !in popularCodes }

                        if (popularCountries.isNotEmpty()) {
                            Text("Popular:", style = MaterialTheme.typography.labelMedium, modifier = Modifier.padding(vertical = 8.dp))
                            popularCountries.forEach { country ->
                                TextButton(
                                    onClick = {
                                        viewModel.loadCountryHolidays(country.countryCode, country.name, uiState.selectedYear)
                                        showCountryDialog = false
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text(country.name)
                                        Text(country.countryCode, style = MaterialTheme.typography.bodySmall)
                                    }
                                }
                            }

                            if (otherCountries.isNotEmpty()) {
                                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                                Text("All countries:", style = MaterialTheme.typography.labelMedium)
                            }
                        }

                        LazyColumn(modifier = Modifier.heightIn(max = 300.dp)) {
                            items(otherCountries) { country ->
                                TextButton(
                                    onClick = {
                                        viewModel.loadCountryHolidays(country.countryCode, country.name, uiState.selectedYear)
                                        showCountryDialog = false
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text(country.name)
                                        Text(country.countryCode, style = MaterialTheme.typography.bodySmall)
                                    }
                                }
                            }
                        }
                    } else {
                        Text("No countries available. Check internet connection.", color = MaterialTheme.colorScheme.error)
                    }
                }
            },
            confirmButton = {},
            dismissButton = { TextButton(onClick = { showCountryDialog = false }) { Text("Cancel") } }
        )
    }

    // Show loading indicator when loading holidays
    if (uiState.isLoadingHolidays) {
        AlertDialog(
            onDismissRequest = {},
            title = { Text("Loading Holidays...") },
            text = {
                Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            },
            confirmButton = {}
        )
    }

    // Show error if any
    uiState.error?.let { error ->
        AlertDialog(
            onDismissRequest = { viewModel.clearError() },
            title = { Text("Error") },
            text = { Text(error) },
            confirmButton = { TextButton(onClick = { viewModel.clearError() }) { Text("OK") } }
        )
    }

    if (showAddDialog) {
        var description by remember { mutableStateOf("") }
        var isVacation by remember { mutableStateOf(false) }
        var pickedDate by remember { mutableStateOf(selectedDate ?: LocalDate.now()) }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Add ${if (isVacation) "Vacation" else "Holiday"}") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Date picker with arrows - WORKING!
                    Text("Date:", style = MaterialTheme.typography.labelMedium)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { pickedDate = pickedDate.minusDays(1) }) {
                            Icon(Icons.Default.KeyboardArrowLeft, "Previous day")
                        }
                        Text(
                            text = pickedDate.format(DateTimeFormatter.ofPattern("dd MMMM yyyy")),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        IconButton(onClick = { pickedDate = pickedDate.plusDays(1) }) {
                            Icon(Icons.Default.KeyboardArrowRight, "Next day")
                        }
                    }

                    // Quick date buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { pickedDate = LocalDate.now() },
                            modifier = Modifier.weight(1f)
                        ) { Text("Today", style = MaterialTheme.typography.labelSmall) }

                        OutlinedButton(
                            onClick = { pickedDate = LocalDate.now().plusDays(1) },
                            modifier = Modifier.weight(1f)
                        ) { Text("Tomorrow", style = MaterialTheme.typography.labelSmall) }
                    }

                    HorizontalDivider()

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
                        Text(if (isVacation) "🏖️ Vacation" else "🎉 Public Holiday")
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.addHoliday(pickedDate, description, if (isVacation) HolidayType.VACATION else HolidayType.PUBLIC_HOLIDAY)
                        showAddDialog = false
                        selectedDate = null
                    },
                    enabled = description.isNotBlank()
                ) { Text("Add") }
            },
            dismissButton = { TextButton(onClick = { showAddDialog = false; selectedDate = null }) { Text("Cancel") } }
        )
    }

    // Add vacation range dialog
    if (showAddVacationDialog) {
        var startDate by remember { mutableStateOf(LocalDate.now()) }
        var endDate by remember { mutableStateOf(LocalDate.now().plusDays(4)) }
        var description by remember { mutableStateOf("Vacation") }

        AlertDialog(
            onDismissRequest = { showAddVacationDialog = false },
            title = { Text("Add Vacation Period") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Description") },
                        placeholder = { Text("e.g., Summer Vacation") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Start date picker with arrows
                    Text("Start Date:", style = MaterialTheme.typography.labelMedium)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { startDate = startDate.minusDays(1) }) {
                            Icon(Icons.Default.KeyboardArrowLeft, "Previous")
                        }
                        Text(
                            text = startDate.format(DateTimeFormatter.ofPattern("dd MMM yyyy")),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        IconButton(onClick = { startDate = startDate.plusDays(1) }) {
                            Icon(Icons.Default.KeyboardArrowRight, "Next")
                        }
                    }

                    // End date picker with arrows
                    Text("End Date:", style = MaterialTheme.typography.labelMedium)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { endDate = endDate.minusDays(1) }) {
                            Icon(Icons.Default.KeyboardArrowLeft, "Previous")
                        }
                        Text(
                            text = endDate.format(DateTimeFormatter.ofPattern("dd MMM yyyy")),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        IconButton(onClick = { endDate = endDate.plusDays(1) }) {
                            Icon(Icons.Default.KeyboardArrowRight, "Next")
                        }
                    }

                    HorizontalDivider()

                    val days = java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate).toInt() + 1
                    val workDays = (0 until days).count {
                        startDate.plusDays(it.toLong()).dayOfWeek.value <= 5
                    }

                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = MaterialTheme.shapes.small
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("Duration", style = MaterialTheme.typography.labelSmall)
                            Text(
                                text = "$workDays workdays",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Text("($days total)", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.addVacationRange(startDate, endDate, description)
                        showAddVacationDialog = false
                    },
                    enabled = description.isNotBlank() && !startDate.isAfter(endDate)
                ) { Text("Add") }
            },
            dismissButton = { TextButton(onClick = { showAddVacationDialog = false }) { Text("Cancel") } }
        )
    }
}

@Composable
private fun SummaryCard(publicHolidays: Int, vacationDays: Int, country: String, onUnloadCountry: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Annual Summary", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(publicHolidays.toString(), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                    Text("🎉 Public", style = MaterialTheme.typography.bodySmall)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(vacationDays.toString(), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                    Text("🏖️ Vacation", style = MaterialTheme.typography.bodySmall)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text((publicHolidays + vacationDays).toString(), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                    Text("Total", style = MaterialTheme.typography.bodySmall)
                }
            }
            if (country.isNotBlank()) {
                HorizontalDivider()
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Country Loaded:", style = MaterialTheme.typography.labelMedium)
                        Text("📍 $country", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    }
                    OutlinedButton(
                        onClick = onUnloadCountry,
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Icon(Icons.Default.Delete, "Remove", tint = MaterialTheme.colorScheme.error)
                        Spacer(Modifier.width(4.dp))
                        Text("Unload")
                    }
                }
            }
        }
    }
}

@Composable
private fun MonthCard(yearMonth: YearMonth, holidays: List<Holiday>, onDateClick: (LocalDate) -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(yearMonth.format(DateTimeFormatter.ofPattern("MMMM")), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                if (holidays.isNotEmpty()) {
                    Surface(shape = MaterialTheme.shapes.small, color = MaterialTheme.colorScheme.secondaryContainer) {
                        Text("${holidays.size} days", modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
            Spacer(Modifier.height(8.dp))

            if (holidays.isNotEmpty()) {
                holidays.sortedBy { it.date }.forEach { holiday ->
                    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(if (holiday.type == HolidayType.PUBLIC_HOLIDAY) "🎉" else "🏖️")
                        Spacer(Modifier.width(8.dp))
                        Text(holiday.date.format(DateTimeFormatter.ofPattern("dd")), fontWeight = FontWeight.Bold, modifier = Modifier.width(30.dp))
                        Text(holiday.description, modifier = Modifier.weight(1f))
                        IconButton(onClick = { onDateClick(holiday.date) }, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Default.Close, "Remove", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            } else {
                Text("No holidays this month", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

