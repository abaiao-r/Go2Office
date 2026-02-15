# API Reference

Code documentation for Go2Office.

## Domain Models

### OfficeSettings
```kotlin
data class OfficeSettings(
    val daysPerWeek: Int,           // 1-5
    val hoursPerDay: Double,        // 1-12
    val weekdayPreferences: List<DayOfWeek>,
    val autoDetectionEnabled: Boolean,
    val officeLatitude: Double?,
    val officeLongitude: Double?,
    val isOnboardingComplete: Boolean
)
```

### DailyEntry
```kotlin
data class DailyEntry(
    val date: LocalDate,
    val hours: Double,
    val isManual: Boolean
)
```

### MonthProgress
```kotlin
data class MonthProgress(
    val requiredDays: Int,
    val completedDays: Int,
    val requiredHours: Double,
    val completedHours: Double,
    val status: ProgressStatus
)
```

### SuggestedDay
```kotlin
data class SuggestedDay(
    val date: LocalDate,
    val dayOfWeek: DayOfWeek,
    val isPreferred: Boolean
)
```

## Use Cases

| Use Case | Purpose |
|----------|---------|
| `GetMonthProgressUseCase` | Get current month's progress |
| `GetSuggestedDaysUseCase` | Get suggested office days |
| `CalculateRequirementsUseCase` | Calculate monthly requirements |
| `SaveOfficeSettingsUseCase` | Save user settings |
| `MarkDayAsOfficeUseCase` | Mark a day with hours |
| `AggregateSessionsUseCase` | Aggregate geofence sessions |
| `AddHolidayUseCase` | Add holiday or vacation |

## Repository Interface

```kotlin
interface OfficeRepository {
    fun getSettings(): Flow<OfficeSettings?>
    suspend fun saveSettings(settings: OfficeSettings)
    fun getDailyEntries(month: YearMonth): Flow<List<DailyEntry>>
    suspend fun saveDailyEntry(entry: DailyEntry)
    fun getHolidays(year: Int): Flow<List<Holiday>>
    suspend fun fetchPublicHolidays(countryCode: String, year: Int)
    // ... more methods
}
```

## ViewModels

Each screen has its own ViewModel:

| ViewModel | Screen | Key State |
|-----------|--------|-----------|
| `DashboardViewModel` | Main | progress, suggestions |
| `SettingsViewModel` | Settings | settings form |
| `OnboardingViewModel` | Setup | step, form data |
| `CalendarViewModel` | Calendar | holidays, vacations |
| `DayEntryViewModel` | Day Entry | date, hours |

## Events Pattern

```kotlin
sealed interface DashboardEvent {
    data class MarkDay(val date: LocalDate, val hours: Double) : DashboardEvent
    object RefreshProgress : DashboardEvent
}

// In ViewModel
fun onEvent(event: DashboardEvent) {
    when (event) {
        is DashboardEvent.MarkDay -> markDay(event.date, event.hours)
        DashboardEvent.RefreshProgress -> loadProgress()
    }
}
```

## Constants

```kotlin
object Constants {
    const val WORK_START_HOUR = 7      // 7 AM
    const val WORK_END_HOUR = 19       // 7 PM
    const val MAX_DAILY_HOURS = 10.0
    const val GEOFENCE_RADIUS = 100f   // meters
}
```

