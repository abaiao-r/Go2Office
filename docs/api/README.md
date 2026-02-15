# 📚 API Documentation

Complete reference for Go2Office code APIs.

---

## Use Cases

### GetMonthProgressUseCase

**Purpose**: Calculate monthly progress (required vs. completed)

```kotlin
class GetMonthProgressUseCase @Inject constructor(
    private val repository: OfficeRepository,
    private val calculateRequirements: CalculateMonthlyRequirementsUseCase
) {
    suspend operator fun invoke(yearMonth: YearMonth): Result<MonthProgress>
}
```

**Parameters**:
- `yearMonth`: The month to calculate progress for

**Returns**: `Result<MonthProgress>`
- `Success`: MonthProgress with requirements and completion
- `Failure`: Exception if settings not configured

**Example**:
```kotlin
viewModelScope.launch {
    val result = getMonthProgress(YearMonth.of(2026, 3))
    result.onSuccess { progress ->
        println("${progress.completedDays}/${progress.requiredDays} days")
    }
}
```

---

### GetSuggestedOfficeDaysUseCase

**Purpose**: Generate smart day suggestions

```kotlin
class GetSuggestedOfficeDaysUseCase @Inject constructor(
    private val repository: OfficeRepository,
    private val getMonthProgress: GetMonthProgressUseCase
) {
    suspend operator fun invoke(
        yearMonth: YearMonth
    ): Result<List<SuggestedDay>>
}
```

**Algorithm**:
1. Get remaining days needed
2. Filter available weekdays
3. Exclude holidays, vacations, completed days
4. Distribute evenly across weeks
5. Sort by weekday preference
6. Return chronological list

**Returns**: List of `SuggestedDay` sorted by date

**Example**:
```kotlin
val suggestions = getSuggestedDays(YearMonth.now())
suggestions.onSuccess { days ->
    days.forEach { println("Suggested: ${it.date} - ${it.reason}") }
}
```

---

### CalculateMonthlyRequirementsUseCase

**Purpose**: Calculate required days and hours for a month

```kotlin
class CalculateMonthlyRequirementsUseCase @Inject constructor() {
    operator fun invoke(
        yearMonth: YearMonth,
        settings: OfficeSettings,
        holidays: List<Holiday> = emptyList(),
        vacations: List<Vacation> = emptyList()
    ): MonthlyRequirements
}
```

**Formula**:
```kotlin
val weekdays = yearMonth.countWeekdays()
val excludedDays = (holidays + vacations).count { it.isWeekday }
val workingDays = weekdays - excludedDays

val requiredDays = ceil(
    (settings.requiredDaysPerWeek * workingDays) / 5.0
).toInt()

val requiredHours = requiredDays * settings.hoursPerDay
```

**Returns**: `MonthlyRequirements`

**Example**:
```kotlin
val requirements = calculateRequirements(
    yearMonth = YearMonth.of(2026, 3),
    settings = OfficeSettings(
        requiredDaysPerWeek = 3,
        hoursPerDay = 8f
    )
)
println("Need ${requirements.requiredDays} days")
```

---

### AggregateSessionsToDailyUseCase

**Purpose**: Combine multiple presence sessions into daily entry

```kotlin
class AggregateSessionsToDailyUseCase @Inject constructor(
    private val presenceDao: OfficePresenceDao,
    private val dailyEntryDao: DailyEntryDao
) {
    suspend operator fun invoke(date: LocalDate): Result<DailyEntry>
}
```

**Process**:
1. Get all presence records for date
2. Pair entry/exit times
3. Calculate hours per session (7 AM - 7 PM window)
4. Sum total hours
5. Apply 10-hour daily cap
6. Save aggregated entry

**Returns**: `Result<DailyEntry>` with total hours

---

## Domain Models

### MonthProgress

```kotlin
data class MonthProgress(
    val requiredDays: Int,
    val requiredHours: Float,
    val completedDays: Int,
    val completedHours: Float
) {
    val daysRemaining: Int
        get() = (requiredDays - completedDays).coerceAtLeast(0)
    
    val hoursRemaining: Float
        get() = (requiredHours - completedHours).coerceAtLeast(0f)
    
    val daysProgress: Float
        get() = if (requiredDays > 0) {
            (completedDays.toFloat() / requiredDays) * 100
        } else 100f
    
    val hoursProgress: Float
        get() = if (requiredHours > 0) {
            (completedHours / requiredHours) * 100
        } else 100f
    
    val overallProgress: Float
        get() = min(daysProgress, hoursProgress)
    
    val isComplete: Boolean
        get() = completedDays >= requiredDays && 
                completedHours >= requiredHours
}
```

---

### SuggestedDay

```kotlin
data class SuggestedDay(
    val date: LocalDate,
    val dayOfWeek: DayOfWeek,
    val reason: String,
    val priority: Int  // 1 = highest
)
```

---

### OfficeSettings

```kotlin
data class OfficeSettings(
    val requiredDaysPerWeek: Int,  // 1-5
    val hoursPerDay: Float,         // 1-12
    val weekdayPreferences: List<DayOfWeek>  // Ordered Mon-Fri
) {
    val requiredHoursPerWeek: Float
        get() = requiredDaysPerWeek * hoursPerDay
    
    fun validate(): Result<Unit> {
        return when {
            requiredDaysPerWeek !in 1..5 ->
                Result.failure(Exception("Days must be 1-5"))
            hoursPerDay !in 1f..12f ->
                Result.failure(Exception("Hours must be 1-12"))
            weekdayPreferences.size != 5 ->
                Result.failure(Exception("Must specify 5 weekdays"))
            weekdayPreferences.toSet().size != 5 ->
                Result.failure(Exception("Weekdays must be unique"))
            else ->
                Result.success(Unit)
        }
    }
}
```

---

### DailyEntry

```kotlin
data class DailyEntry(
    val date: LocalDate,
    val hoursWorked: Float,
    val isOfficeDay: Boolean,
    val notes: String? = null
) {
    val isComplete: Boolean
        get() = hoursWorked >= 8f  // Full day
    
    val isPartialDay: Boolean
        get() = hoursWorked > 0f && hoursWorked < 8f
}
```

---

## Repository Interface

### OfficeRepository

```kotlin
interface OfficeRepository {
    // Settings
    fun getSettings(): Flow<OfficeSettings?>
    suspend fun saveSettings(settings: OfficeSettings): Result<Unit>
    
    // Daily Entries
    suspend fun getDailyEntry(date: LocalDate): DailyEntry?
    suspend fun getDailyEntriesForMonth(yearMonth: YearMonth): List<DailyEntry>
    suspend fun saveDailyEntry(entry: DailyEntry): Result<Unit>
    
    // Holidays
    suspend fun getHolidays(yearMonth: YearMonth): List<Holiday>
    suspend fun addHoliday(holiday: Holiday): Result<Unit>
    suspend fun deleteHoliday(id: Long): Result<Unit>
    suspend fun fetchPublicHolidays(
        countryCode: String,
        year: Int
    ): Result<List<Holiday>>
    
    // Office Presence (Auto-Detection)
    suspend fun getPresenceForDate(date: LocalDate): List<OfficePresenceEntity>
    suspend fun insertPresence(presence: OfficePresenceEntity): Result<Unit>
    fun getActiveSession(): Flow<OfficePresenceEntity?>
    
    // Office Location
    suspend fun getOfficeLocation(): OfficeLocation?
    suspend fun saveOfficeLocation(location: OfficeLocation): Result<Unit>
}
```

---

## ViewModels

### DashboardViewModel

```kotlin
@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val getMonthProgress: GetMonthProgressUseCase,
    private val getSuggestedDays: GetSuggestedOfficeDaysUseCase,
    private val getDailyEntries: GetDailyEntriesUseCase,
    private val repository: OfficeRepository
) : ViewModel() {
    
    // State
    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()
    
    // Events
    fun onEvent(event: DashboardEvent) {
        when (event) {
            DashboardEvent.Refresh -> loadDashboardData()
            is DashboardEvent.SelectMonth -> selectMonth(event.yearMonth)
            is DashboardEvent.QuickMarkDay -> markDay(event.date, event.hours)
        }
    }
    
    private fun loadDashboardData() { /* ... */ }
}
```

**UI State**:
```kotlin
data class DashboardUiState(
    val isLoading: Boolean = false,
    val selectedMonth: YearMonth = YearMonth.now(),
    val monthProgress: MonthProgress? = null,
    val suggestedDays: List<SuggestedDay> = emptyList(),
    val recentEntries: List<DailyEntry> = emptyList(),
    val activeSession: OfficePresenceEntity? = null,
    val errorMessage: String? = null
)
```

**Events**:
```kotlin
sealed class DashboardEvent {
    object Refresh : DashboardEvent()
    data class SelectMonth(val yearMonth: YearMonth) : DashboardEvent()
    data class QuickMarkDay(
        val date: LocalDate,
        val hours: Float
    ) : DashboardEvent()
}
```

---

## Utilities

### DateUtils

```kotlin
object DateUtils {
    fun YearMonth.getAllWeekdays(): List<LocalDate> {
        return (1..lengthOfMonth()).map { atDay(it) }
            .filter { it.dayOfWeek !in listOf(SATURDAY, SUNDAY) }
    }
    
    fun YearMonth.countWeekdays(): Int {
        return getAllWeekdays().size
    }
    
    fun LocalDate.isWeekday(): Boolean {
        return dayOfWeek !in listOf(SATURDAY, SUNDAY)
    }
    
    fun LocalDate.formatForDisplay(): String {
        return format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))
    }
}
```

---

### WorkHoursCalculator

```kotlin
object WorkHoursCalculator {
    const val WORK_START_HOUR = 7  // 7 AM
    const val WORK_END_HOUR = 19   // 7 PM
    const val MAX_DAILY_HOURS = 10f
    
    fun calculateSessionHours(
        entryTime: LocalDateTime,
        exitTime: LocalDateTime
    ): Float {
        // Clamp to work hours window
        val effectiveEntry = entryTime.clampToWorkHours()
        val effectiveExit = exitTime.clampToWorkHours()
        
        if (effectiveExit.isBefore(effectiveEntry)) return 0f
        
        val duration = Duration.between(effectiveEntry, effectiveExit)
        return (duration.toMinutes() / 60f).coerceAtLeast(0f)
    }
    
    fun calculateDailyHours(
        sessions: List<Pair<LocalDateTime, LocalDateTime>>
    ): Float {
        val total = sessions.sumOf { (entry, exit) ->
            calculateSessionHours(entry, exit).toDouble()
        }.toFloat()
        
        return total.coerceAtMost(MAX_DAILY_HOURS)
    }
    
    private fun LocalDateTime.clampToWorkHours(): LocalDateTime {
        val workStart = toLocalDate().atTime(WORK_START_HOUR, 0)
        val workEnd = toLocalDate().atTime(WORK_END_HOUR, 0)
        
        return when {
            isBefore(workStart) -> workStart
            isAfter(workEnd) -> workEnd
            else -> this
        }
    }
}
```

---

## Constants

```kotlin
object Constants {
    // Work Hours
    const val WORK_START_HOUR = 7
    const val WORK_END_HOUR = 19
    const val MAX_DAILY_HOURS = 10f
    
    // Geofencing
    const val GEOFENCE_RADIUS_METERS = 150f
    const val GEOFENCE_EXPIRATION_DURATION = Geofence.NEVER_EXPIRE
    
    // Notifications
    const val NOTIFICATION_CHANNEL_ID = "office_tracking"
    const val NOTIFICATION_CHANNEL_NAME = "Office Tracking"
    
    // Validation
    const val MIN_DAYS_PER_WEEK = 1
    const val MAX_DAYS_PER_WEEK = 5
    const val MIN_HOURS_PER_DAY = 1f
    const val MAX_HOURS_PER_DAY = 12f
    
    // API
    const val HOLIDAY_API_BASE_URL = "https://date.nager.at/api/v3/"
    const val API_TIMEOUT_SECONDS = 10L
}
```

---

## Testing Helpers

### FakeOfficeRepository

```kotlin
class FakeOfficeRepository : OfficeRepository {
    private val settings = MutableStateFlow<OfficeSettings?>(null)
    private val dailyEntries = mutableListOf<DailyEntry>()
    private val holidays = mutableListOf<Holiday>()
    
    override fun getSettings() = settings.asStateFlow()
    
    override suspend fun saveSettings(settings: OfficeSettings) = 
        runCatching { this.settings.value = settings }
    
    override suspend fun getDailyEntriesForMonth(yearMonth: YearMonth) =
        dailyEntries.filter { YearMonth.from(it.date) == yearMonth }
    
    // ... other methods
}
```

### Test Fixtures

```kotlin
object TestFixtures {
    val defaultSettings = OfficeSettings(
        requiredDaysPerWeek = 3,
        hoursPerDay = 8f,
        weekdayPreferences = listOf(
            MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY
        )
    )
    
    fun createDailyEntry(
        date: LocalDate = LocalDate.now(),
        hours: Float = 8f
    ) = DailyEntry(
        date = date,
        hoursWorked = hours,
        isOfficeDay = hours > 0f
    )
    
    fun createMonthProgress(
        required: Int = 12,
        completed: Int = 6
    ) = MonthProgress(
        requiredDays = required,
        requiredHours = required * 8f,
        completedDays = completed,
        completedHours = completed * 8f
    )
}
```

---

## Extension Functions

### Flow Extensions

```kotlin
fun <T> Flow<T>.throttleLatest(periodMillis: Long): Flow<T> {
    return flow {
        conflate().collect {
            emit(it)
            delay(periodMillis)
        }
    }
}
```

### Compose Extensions

```kotlin
@Composable
fun <T> Flow<T>.collectAsStateWithLifecycle(
    initial: T
): State<T> {
    val lifecycleOwner = LocalLifecycleOwner.current
    return produceState(initial, this, lifecycleOwner) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            this@collectAsStateWithLifecycle.collect { value = it }
        }
    }
}
```

---

**See Also**:
- [Architecture](../architecture/README.md)
- [Business Logic](../business/README.md)
- [Technical Guide](../technical/README.md)

