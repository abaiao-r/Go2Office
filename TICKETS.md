# Go2Office Implementation Tickets

**Epic: Build Go2Office - In-Office Days/Hours Tracker for Android**

---

## 🏗️ Sprint 0: Foundation & Setup (Priority: Critical)

### Ticket #1: Project Setup & Dependencies
**Priority**: P0 - Critical  
**Story Points**: 3  
**Type**: Setup

**Description:**
Configure project dependencies and build configuration for Jetpack Compose, Hilt, Room, and testing frameworks.

**Tasks:**
- [ ] Update `build.gradle.kts` with required dependencies:
  - Jetpack Compose (BOM)
  - Hilt for DI
  - Room database
  - Navigation Compose
  - Kotlin Coroutines & Flow
  - JUnit, Mockito, Robolectric for testing
  - Compose Testing libraries
- [ ] Add Hilt Gradle plugin
- [ ] Configure Kotlin compiler options for Compose
- [ ] Set up proguard rules for Room and Hilt
- [ ] Verify build succeeds

**Acceptance Criteria:**
- Project builds successfully
- All dependencies resolve
- No version conflicts

**Files to Create/Modify:**
- `build.gradle.kts` (root)
- `app/build.gradle.kts`
- `gradle/libs.versions.toml`

---

### Ticket #2: Hilt Setup & Application Class
**Priority**: P0 - Critical  
**Story Points**: 2  
**Type**: Setup

**Description:**
Set up Hilt dependency injection framework and create Application class.

**Tasks:**
- [ ] Create `Go2OfficeApplication.kt` with `@HiltAndroidApp`
- [ ] Update `AndroidManifest.xml` to reference Application class
- [ ] Create `AppModule.kt` in `di/` package
- [ ] Create `DatabaseModule.kt` skeleton
- [ ] Create `RepositoryModule.kt` skeleton

**Acceptance Criteria:**
- Hilt graph compiles successfully
- Application class initializes without errors
- DI modules are recognized

**Files to Create:**
- `com/example/go2office/Go2OfficeApplication.kt`
- `di/AppModule.kt`
- `di/DatabaseModule.kt`
- `di/RepositoryModule.kt`

---

### Ticket #3: Room Database Schema Definition
**Priority**: P0 - Critical  
**Story Points**: 5  
**Type**: Data Layer

**Description:**
Define Room database entities, DAOs, and database class for local persistence.

**Tasks:**
- [ ] Create `OfficeSettingsEntity.kt` (table: office_settings)
  - Fields: id, requiredDaysPerWeek, requiredHoursPerWeek, weekdayPreferences (JSON/List), createdAt, updatedAt
- [ ] Create `DailyEntryEntity.kt` (table: daily_entries)
  - Fields: id, date, wasInOffice, hoursWorked, notes, createdAt
- [ ] Create `MonthlyLogEntity.kt` (table: monthly_logs)
  - Fields: id, yearMonth, requiredDays, requiredHours, completedDays, completedHours, createdAt
- [ ] Create `HolidayEntity.kt` (table: holidays)
  - Fields: id, date, description, createdAt
- [ ] Create `OfficeDatabase.kt` with all entities and version 1
- [ ] Create `OfficeSettingsDao.kt` with CRUD operations
- [ ] Create `DailyEntryDao.kt` with CRUD + date range queries
- [ ] Create `MonthlyLogDao.kt` with CRUD + year-month queries
- [ ] Create `HolidayDao.kt` with CRUD + date range queries
- [ ] Create type converters for custom types (List, LocalDate, YearMonth)

**Acceptance Criteria:**
- Database compiles and can be instantiated
- All DAOs have necessary queries
- Type converters handle custom types correctly

**Files to Create:**
- `data/local/entities/OfficeSettingsEntity.kt`
- `data/local/entities/DailyEntryEntity.kt`
- `data/local/entities/MonthlyLogEntity.kt`
- `data/local/entities/HolidayEntity.kt`
- `data/local/entities/OfficeDatabase.kt`
- `data/local/entities/Converters.kt`
- `data/local/dao/OfficeSettingsDao.kt`
- `data/local/dao/DailyEntryDao.kt`
- `data/local/dao/MonthlyLogDao.kt`
- `data/local/dao/HolidayDao.kt`

---

## 🎯 Sprint 1: Domain Layer (Priority: High)

### Ticket #4: Domain Models Definition
**Priority**: P1 - High  
**Story Points**: 3  
**Type**: Domain Layer

**Description:**
Define domain models representing business entities (framework-agnostic).

**Tasks:**
- [ ] Create `OfficeSettings.kt` data class
- [ ] Create `MonthlyRequirements.kt` data class
- [ ] Create `MonthlyLog.kt` data class
- [ ] Create `DailyEntry.kt` data class
- [ ] Create `Holiday.kt` data class
- [ ] Create `SuggestedDay.kt` data class
- [ ] Create `WeekdayPreference.kt` enum/sealed class
- [ ] Create `MonthProgress.kt` data class
- [ ] Add documentation for each model

**Acceptance Criteria:**
- All models are immutable data classes
- Models have no Android dependencies
- Clear documentation for each field

**Files to Create:**
- `domain/model/OfficeSettings.kt`
- `domain/model/MonthlyRequirements.kt`
- `domain/model/MonthlyLog.kt`
- `domain/model/DailyEntry.kt`
- `domain/model/Holiday.kt`
- `domain/model/SuggestedDay.kt`
- `domain/model/WeekdayPreference.kt`
- `domain/model/MonthProgress.kt`

---

### Ticket #5: Repository Interface Definition
**Priority**: P1 - High  
**Story Points**: 2  
**Type**: Domain Layer

**Description:**
Define repository interface for data operations (abstract, to be implemented in data layer).

**Tasks:**
- [ ] Create `OfficeRepository.kt` interface in `domain/repository/`
- [ ] Define suspend functions for:
  - Settings: save, get, update
  - Daily entries: insert, update, delete, getByDate, getForMonth
  - Monthly logs: insert, update, getByYearMonth, getAll
  - Holidays: insert, delete, getForMonth
- [ ] Use Flow for observable queries
- [ ] Return Result/sealed class for error handling

**Acceptance Criteria:**
- Interface has all necessary operations
- Uses Kotlin Flow for reactive data
- No implementation details (pure interface)

**Files to Create:**
- `domain/repository/OfficeRepository.kt`

---

### Ticket #6: Data Mappers Implementation
**Priority**: P1 - High  
**Story Points**: 3  
**Type**: Data Layer

**Description:**
Create mappers to convert between Room entities and domain models.

**Tasks:**
- [ ] Create `SettingsMapper.kt`
  - `toEntity()` and `toDomain()` functions
- [ ] Create `MonthlyLogMapper.kt`
- [ ] Create `DailyEntryMapper.kt`
- [ ] Create `HolidayMapper.kt`
- [ ] Handle nullable fields appropriately
- [ ] Add unit tests for each mapper

**Acceptance Criteria:**
- All mappers have bidirectional conversion
- Edge cases (nulls, empty lists) handled
- Unit tests pass

**Files to Create:**
- `data/mapper/SettingsMapper.kt`
- `data/mapper/MonthlyLogMapper.kt`
- `data/mapper/DailyEntryMapper.kt`
- `data/mapper/HolidayMapper.kt`
- `test/.../data/mapper/SettingsMapperTest.kt` (and others)

---

### Ticket #7: Repository Implementation
**Priority**: P1 - High  
**Story Points**: 5  
**Type**: Data Layer

**Description:**
Implement the OfficeRepository interface using Room DAOs and mappers.

**Tasks:**
- [ ] Create `OfficeRepositoryImpl.kt` in `data/repository/`
- [ ] Inject all DAOs via Hilt constructor
- [ ] Implement all interface methods
- [ ] Use mappers to convert entities ↔ domain models
- [ ] Handle exceptions and wrap in Result/sealed class
- [ ] Add Dispatchers.IO for database operations
- [ ] Update `RepositoryModule.kt` to bind implementation
- [ ] Create repository unit tests with fake DAOs

**Acceptance Criteria:**
- All repository methods implemented
- Proper error handling
- Unit tests with fakes/mocks pass
- Hilt binding works

**Files to Create:**
- `data/repository/OfficeRepositoryImpl.kt`
- `test/.../data/repository/OfficeRepositoryImplTest.kt`

**Files to Modify:**
- `di/RepositoryModule.kt`

---

## 🧮 Sprint 2: Business Logic / Use Cases (Priority: High)

### Ticket #8: Utility Functions for Date Calculations
**Priority**: P1 - High  
**Story Points**: 5  
**Type**: Utilities

**Description:**
Create utility functions for date/calendar calculations (weekday counting, month logic, timezone handling).

**Tasks:**
- [ ] Create `DateUtils.kt` with functions:
  - `getWeekdaysInMonth(yearMonth, zoneId): Int` - Count Mon-Fri in month
  - `isWeekend(date): Boolean` - Check if Sat/Sun
  - `getWorkingDaysInRange(start, end): List<LocalDate>` - Filter weekdays
  - `getCurrentMonth(zoneId): YearMonth`
  - `getDaysUntilEndOfMonth(date): Int`
- [ ] Create `TimeZoneUtils.kt` for timezone conversions
- [ ] Add extensive unit tests for edge cases:
  - Months with different weekday counts
  - February (leap year vs non-leap)
  - Year boundaries
  - Timezone changes

**Acceptance Criteria:**
- All date calculations are correct
- Handles edge cases (leap years, etc.)
- Timezone-aware
- 100% test coverage

**Files to Create:**
- `util/DateUtils.kt`
- `util/TimeZoneUtils.kt`
- `test/.../util/DateUtilsTest.kt`
- `test/.../util/TimeZoneUtilsTest.kt`

---

### Ticket #9: Monthly Requirements Calculation Use Case
**Priority**: P1 - High  
**Story Points**: 5  
**Type**: Use Case

**Description:**
Implement use case to calculate required days/hours for a given month based on user settings.

**Formula:**
```
requiredDays = weekdaysInMonth * (requiredDaysPerWeek / 5.0)
requiredHours = requiredDays * hoursPerDay
(Round up for required days)
```

**Tasks:**
- [ ] Create `CalculateMonthlyRequirementsUseCase.kt`
- [ ] Inject repository and DateUtils
- [ ] Implement invoke function: `(YearMonth, ZoneId) -> MonthlyRequirements`
- [ ] Fetch settings from repository
- [ ] Count weekdays in month (excluding holidays)
- [ ] Calculate required days and hours
- [ ] Handle edge cases (no settings, invalid data)
- [ ] Create comprehensive unit tests with multiple month examples

**Test Examples:**
- March 2026: 22 weekdays, 3 days/week required → 14 days required
- February 2026: 20 weekdays, 2 days/week → 8 days required

**Acceptance Criteria:**
- Calculation is accurate for any month
- Handles holidays exclusion
- Unit tests cover edge cases
- Returns proper error if settings not found

**Files to Create:**
- `domain/usecase/CalculateMonthlyRequirementsUseCase.kt`
- `test/.../domain/usecase/CalculateMonthlyRequirementsUseCaseTest.kt`

---

### Ticket #10: Get Month Progress Use Case
**Priority**: P1 - High  
**Story Points**: 3  
**Type**: Use Case

**Description:**
Calculate current month's progress (completed vs required days/hours).

**Tasks:**
- [ ] Create `GetMonthProgressUseCase.kt`
- [ ] Inject repository and CalculateMonthlyRequirementsUseCase
- [ ] Get required days/hours for current month
- [ ] Query daily entries for current month
- [ ] Sum completed days and hours
- [ ] Return `MonthProgress` with:
  - requiredDays, completedDays
  - requiredHours, completedHours
  - remainingDays, remainingHours
  - percentComplete
- [ ] Add unit tests

**Acceptance Criteria:**
- Accurate progress calculation
- Handles partial month
- Unit tests pass

**Files to Create:**
- `domain/usecase/GetMonthProgressUseCase.kt`
- `test/.../domain/usecase/GetMonthProgressUseCaseTest.kt`

---

### Ticket #11: Suggested Office Days Algorithm Use Case
**Priority**: P1 - High  
**Story Points**: 8  
**Type**: Use Case

**Description:**
Implement algorithm to suggest next N office days based on weekday preferences and current progress.

**Algorithm Logic:**
1. Get remaining required days for month
2. Get user's weekday preference order
3. Filter out past dates and weekends
4. Filter out already-marked office days
5. Score remaining dates by preference order
6. Return top N dates

**Tasks:**
- [ ] Create `GetSuggestedOfficeDaysUseCase.kt`
- [ ] Inject repository and GetMonthProgressUseCase
- [ ] Implement suggestion algorithm
- [ ] Consider:
  - Weekday preference order
  - Days already marked as in-office
  - Days remaining in month
  - Spread suggestions across weeks
- [ ] Return `List<SuggestedDay>` with date and reason
- [ ] Add comprehensive unit tests with various scenarios

**Acceptance Criteria:**
- Suggests days matching user preferences
- Doesn't suggest past dates
- Doesn't suggest already-marked days
- Unit tests cover multiple scenarios

**Files to Create:**
- `domain/usecase/GetSuggestedOfficeDaysUseCase.kt`
- `test/.../domain/usecase/GetSuggestedOfficeDaysUseCaseTest.kt`

---

### Ticket #12: Mark Day as Office Use Case
**Priority**: P1 - High  
**Story Points**: 3  
**Type**: Use Case

**Description:**
Use case to mark a day as in-office with hours worked.

**Tasks:**
- [ ] Create `MarkDayAsOfficeUseCase.kt`
- [ ] Validate inputs (hours >= 0, date not in future)
- [ ] Check if entry already exists (update vs insert)
- [ ] Save to repository
- [ ] Update monthly log if needed
- [ ] Return success/error Result
- [ ] Add unit tests

**Acceptance Criteria:**
- Validates inputs
- Updates existing entries
- Handles errors gracefully
- Unit tests pass

**Files to Create:**
- `domain/usecase/MarkDayAsOfficeUseCase.kt`
- `test/.../domain/usecase/MarkDayAsOfficeUseCaseTest.kt`

---

### Ticket #13: Settings Management Use Cases
**Priority**: P1 - High  
**Story Points**: 3  
**Type**: Use Case

**Description:**
Create use cases for saving, retrieving, and validating user settings.

**Tasks:**
- [ ] Create `SaveOfficeSettingsUseCase.kt`
- [ ] Create `GetOfficeSettingsUseCase.kt`
- [ ] Create `ValidateSettingsUseCase.kt`
  - Validate requiredDaysPerWeek (1-5)
  - Validate requiredHoursPerWeek (> 0)
  - Validate weekday preferences (all 5 weekdays present)
- [ ] Add unit tests for each

**Acceptance Criteria:**
- Settings can be saved and retrieved
- Validation catches invalid inputs
- Unit tests pass

**Files to Create:**
- `domain/usecase/SaveOfficeSettingsUseCase.kt`
- `domain/usecase/GetOfficeSettingsUseCase.kt`
- `domain/usecase/ValidateSettingsUseCase.kt`
- `test/.../domain/usecase/SaveOfficeSettingsUseCaseTest.kt`
- `test/.../domain/usecase/GetOfficeSettingsUseCaseTest.kt`
- `test/.../domain/usecase/ValidateSettingsUseCaseTest.kt`

---

### Ticket #14: Holiday Management Use Cases
**Priority**: P2 - Medium  
**Story Points**: 2  
**Type**: Use Case

**Description:**
Use cases for adding and retrieving holidays (office-not-required days).

**Tasks:**
- [ ] Create `AddHolidayUseCase.kt`
- [ ] Create `GetHolidaysForMonthUseCase.kt`
- [ ] Create `DeleteHolidayUseCase.kt`
- [ ] Add unit tests

**Acceptance Criteria:**
- Holidays can be added/deleted
- Holidays are factored into monthly calculations
- Unit tests pass

**Files to Create:**
- `domain/usecase/AddHolidayUseCase.kt`
- `domain/usecase/GetHolidaysForMonthUseCase.kt`
- `domain/usecase/DeleteHolidayUseCase.kt`
- `test/.../domain/usecase/AddHolidayUseCaseTest.kt`
- (similar for others)

---

## 🎨 Sprint 3: UI Foundation (Priority: High)

### Ticket #15: Navigation Setup
**Priority**: P1 - High  
**Story Points**: 3  
**Type**: Presentation Layer

**Description:**
Set up Jetpack Compose Navigation with all app screens.

**Tasks:**
- [ ] Create `Screen.kt` sealed class/object for routes:
  - Onboarding
  - Dashboard
  - DayEntry (with date parameter)
  - Settings
- [ ] Create `NavGraph.kt` with NavHost and composable destinations
- [ ] Configure MainActivity to host navigation
- [ ] Add navigation arguments parsing
- [ ] Create `NavigationActions.kt` helper

**Acceptance Criteria:**
- All screens are navigable
- Arguments pass correctly
- Back stack behaves properly

**Files to Create:**
- `presentation/navigation/Screen.kt`
- `presentation/navigation/NavGraph.kt`
- `presentation/navigation/NavigationActions.kt`

**Files to Modify:**
- `MainActivity.kt`

---

### Ticket #16: Compose Theme & Design System
**Priority**: P1 - High  
**Story Points**: 3  
**Type**: UI/Theme

**Description:**
Define Material 3 theme with colors, typography, and common component styles.

**Tasks:**
- [ ] Update `Color.kt` with app color palette (light + dark mode)
- [ ] Update `Type.kt` with typography scale
- [ ] Update `Theme.kt` to support dynamic colors and dark mode
- [ ] Create `Shape.kt` with corner radius definitions
- [ ] Define common dimensions in `Dimen.kt`
- [ ] Test theme switching (light/dark)

**Acceptance Criteria:**
- Theme supports light and dark mode
- Colors are accessible (WCAG AA)
- Typography is readable

**Files to Modify:**
- `ui/theme/Color.kt`
- `ui/theme/Type.kt`
- `ui/theme/Theme.kt`

**Files to Create:**
- `ui/theme/Shape.kt`
- `ui/theme/Dimen.kt`

---

### Ticket #17: Reusable Compose Components
**Priority**: P1 - High  
**Story Points**: 5  
**Type**: Presentation Layer

**Description:**
Create reusable Compose components used across screens.

**Tasks:**
- [ ] Create `ProgressCard.kt` - Card showing progress with bars
- [ ] Create `RequirementSummaryCard.kt` - Summary display card
- [ ] Create `DayChip.kt` - Chip for weekday selection
- [ ] Create `HourEntryField.kt` - Text field for hours input
- [ ] Create `EmptyState.kt` - Empty state placeholder
- [ ] Create `LoadingIndicator.kt` - Loading spinner
- [ ] Create `ErrorDialog.kt` - Error message dialog
- [ ] Create `ConfirmationDialog.kt` - Confirmation prompt
- [ ] Add preview functions for each component

**Acceptance Criteria:**
- All components are reusable
- Components follow Material 3 guidelines
- Preview functions work in Android Studio

**Files to Create:**
- `presentation/components/ProgressCard.kt`
- `presentation/components/RequirementSummaryCard.kt`
- `presentation/components/DayChip.kt`
- `presentation/components/HourEntryField.kt`
- `presentation/components/EmptyState.kt`
- `presentation/components/LoadingIndicator.kt`
- `presentation/components/ErrorDialog.kt`
- `presentation/components/ConfirmationDialog.kt`

---

## 🚀 Sprint 4: Onboarding Feature (Priority: High)

### Ticket #18: Onboarding UI State & Events
**Priority**: P1 - High  
**Story Points**: 2  
**Type**: Presentation Layer

**Description:**
Define UI state and event classes for onboarding flow.

**Tasks:**
- [ ] Create `OnboardingUiState.kt` data class:
  - currentStep: Int
  - requiredDaysPerWeek: Int?
  - requiredHoursPerWeek: Float?
  - weekdayPreferences: List<WeekdayPreference>
  - isLoading: Boolean
  - errorMessage: String?
  - isComplete: Boolean
- [ ] Create `OnboardingEvent.kt` sealed class:
  - UpdateRequiredDays(days: Int)
  - UpdateRequiredHours(hours: Float)
  - UpdateWeekdayPreferences(preferences: List)
  - NextStep
  - PreviousStep
  - Complete

**Acceptance Criteria:**
- State covers all onboarding steps
- Events cover all user actions

**Files to Create:**
- `presentation/onboarding/OnboardingUiState.kt`
- `presentation/onboarding/OnboardingEvent.kt`

---

### Ticket #19: Onboarding ViewModel
**Priority**: P1 - High  
**Story Points**: 5  
**Type**: Presentation Layer

**Description:**
Implement ViewModel for onboarding flow with validation and state management.

**Tasks:**
- [ ] Create `OnboardingViewModel.kt` extending ViewModel
- [ ] Inject use cases: SaveOfficeSettingsUseCase, ValidateSettingsUseCase
- [ ] Expose StateFlow<OnboardingUiState>
- [ ] Implement event handling function
- [ ] Handle step navigation (3 steps total)
- [ ] Validate inputs before allowing next step
- [ ] Save settings on completion
- [ ] Add unit tests with fake use cases

**Acceptance Criteria:**
- State updates correctly on events
- Validation prevents invalid progression
- Settings saved successfully on completion
- Unit tests pass with 80%+ coverage

**Files to Create:**
- `presentation/onboarding/OnboardingViewModel.kt`
- `test/.../presentation/onboarding/OnboardingViewModelTest.kt`

---

### Ticket #20: Onboarding Screen UI
**Priority**: P1 - High  
**Story Points**: 8  
**Type**: Presentation Layer

**Description:**
Build Onboarding screen UI with multi-step flow.

**Steps:**
1. Welcome + Required days per week selector (1-5)
2. Required hours per week input
3. Weekday preference ordering (drag/reorder Mon-Fri)

**Tasks:**
- [ ] Create `OnboardingScreen.kt` composable
- [ ] Observe ViewModel state
- [ ] Create step 1: Welcome + days selector
- [ ] Create step 2: Hours input
- [ ] Create step 3: Weekday preference drag-drop list
- [ ] Add step indicator (e.g., 1/3, 2/3)
- [ ] Add Next/Previous buttons
- [ ] Add "Complete" button on final step
- [ ] Handle loading and error states
- [ ] Add accessibility labels
- [ ] Create preview functions

**Acceptance Criteria:**
- All three steps are navigable
- Validation errors shown clearly
- Weekday reordering works smoothly
- UI is responsive on different screen sizes
- Accessibility labels present

**Files to Create:**
- `presentation/onboarding/OnboardingScreen.kt`
- `presentation/onboarding/components/RequiredDaysSelector.kt`
- `presentation/onboarding/components/HoursSetupCard.kt`
- `presentation/onboarding/components/WeekdayPreferenceList.kt`

---

### Ticket #21: Onboarding UI Tests
**Priority**: P2 - Medium  
**Story Points**: 3  
**Type**: Testing

**Description:**
Write Compose UI tests for onboarding flow.

**Tasks:**
- [ ] Create `OnboardingScreenTest.kt`
- [ ] Test step navigation
- [ ] Test input validation
- [ ] Test weekday reordering
- [ ] Test save success
- [ ] Test error display
- [ ] Use test ViewModel with fake use cases

**Acceptance Criteria:**
- All critical user flows tested
- Tests are stable and fast

**Files to Create:**
- `androidTest/.../presentation/onboarding/OnboardingScreenTest.kt`

---

## 📊 Sprint 5: Dashboard Feature (Priority: High)

### Ticket #22: Dashboard UI State & Events
**Priority**: P1 - High  
**Story Points**: 2  
**Type**: Presentation Layer

**Description:**
Define UI state and events for Dashboard screen.

**Tasks:**
- [ ] Create `DashboardUiState.kt`:
  - selectedMonth: YearMonth
  - monthProgress: MonthProgress?
  - suggestedDays: List<SuggestedDay>
  - recentEntries: List<DailyEntry>
  - isLoading: Boolean
  - errorMessage: String?
- [ ] Create `DashboardEvent.kt`:
  - SelectMonth(yearMonth: YearMonth)
  - Refresh
  - NavigateToDayEntry(date: LocalDate)
  - NavigateToSettings
  - MarkQuickEntry(date: LocalDate, hours: Float)

**Acceptance Criteria:**
- State captures all dashboard data
- Events cover all user actions

**Files to Create:**
- `presentation/dashboard/DashboardUiState.kt`
- `presentation/dashboard/DashboardEvent.kt`

---

### Ticket #23: Dashboard ViewModel
**Priority**: P1 - High  
**Story Points**: 8  
**Type**: Presentation Layer

**Description:**
Implement Dashboard ViewModel with business logic for main screen.

**Tasks:**
- [ ] Create `DashboardViewModel.kt`
- [ ] Inject use cases:
  - GetMonthProgressUseCase
  - GetSuggestedOfficeDaysUseCase
  - GetDailyEntriesUseCase
  - MarkDayAsOfficeUseCase
- [ ] Initialize with current month
- [ ] Load month progress on init
- [ ] Load suggested days
- [ ] Load recent entries
- [ ] Handle month selection (previous/next)
- [ ] Implement quick entry functionality
- [ ] Handle errors gracefully
- [ ] Add comprehensive unit tests

**Acceptance Criteria:**
- Dashboard loads current month data
- Month navigation works
- Suggestions update correctly
- Quick entry saves properly
- Unit tests achieve 80%+ coverage

**Files to Create:**
- `presentation/dashboard/DashboardViewModel.kt`
- `test/.../presentation/dashboard/DashboardViewModelTest.kt`

---

### Ticket #24: Dashboard Screen UI
**Priority**: P1 - High  
**Story Points**: 13  
**Type**: Presentation Layer

**Description:**
Build the main Dashboard screen with month overview and suggestions.

**UI Sections:**
1. Month selector (previous/next buttons)
2. Requirements overview card (required vs completed days/hours)
3. Progress section (visual progress bars)
4. Suggested next days list
5. Recent entries list
6. FAB for quick day entry

**Tasks:**
- [ ] Create `DashboardScreen.kt`
- [ ] Observe ViewModel state
- [ ] Create `MonthSelector.kt` component (header with ◀ Month ▶)
- [ ] Create `RequirementsOverview.kt` card
- [ ] Create `ProgressSection.kt` with circular/linear progress
- [ ] Create `SuggestedDaysSection.kt` with list of chips/cards
- [ ] Create `RecentEntriesList.kt`
- [ ] Add FloatingActionButton for quick entry
- [ ] Handle loading and error states
- [ ] Add pull-to-refresh
- [ ] Add accessibility support
- [ ] Create preview functions

**Acceptance Criteria:**
- All sections display correctly
- Month navigation is smooth
- Suggested days are clickable
- FAB opens day entry
- Responsive layout
- Dark mode works

**Files to Create:**
- `presentation/dashboard/DashboardScreen.kt`
- `presentation/dashboard/components/MonthSelector.kt`
- `presentation/dashboard/components/RequirementsOverview.kt`
- `presentation/dashboard/components/ProgressSection.kt`
- `presentation/dashboard/components/SuggestedDaysSection.kt`
- `presentation/dashboard/components/RecentEntriesList.kt`

---

### Ticket #25: Dashboard UI Tests
**Priority**: P2 - Medium  
**Story Points**: 5  
**Type**: Testing

**Description:**
Write Compose UI tests for Dashboard screen.

**Tasks:**
- [ ] Create `DashboardScreenTest.kt`
- [ ] Test initial data load
- [ ] Test month navigation
- [ ] Test suggested day click
- [ ] Test FAB click
- [ ] Test pull-to-refresh
- [ ] Test empty state
- [ ] Test error state

**Acceptance Criteria:**
- All user interactions tested
- Tests are maintainable

**Files to Create:**
- `androidTest/.../presentation/dashboard/DashboardScreenTest.kt`

---

## ✏️ Sprint 6: Day Entry Feature (Priority: High)

### Ticket #26: Day Entry UI State & Events
**Priority**: P1 - High  
**Story Points**: 2  
**Type**: Presentation Layer

**Description:**
Define UI state and events for Day Entry screen.

**Tasks:**
- [ ] Create `DayEntryUiState.kt`:
  - selectedDate: LocalDate
  - wasInOffice: Boolean
  - hoursWorked: Float
  - notes: String
  - existingEntry: DailyEntry?
  - isLoading: Boolean
  - isSaved: Boolean
  - errorMessage: String?
- [ ] Create `DayEntryEvent.kt`:
  - SelectDate(date: LocalDate)
  - ToggleWasInOffice(value: Boolean)
  - UpdateHours(hours: Float)
  - UpdateNotes(notes: String)
  - Save
  - Delete

**Acceptance Criteria:**
- State covers all entry fields
- Events cover all actions

**Files to Create:**
- `presentation/dayentry/DayEntryUiState.kt`
- `presentation/dayentry/DayEntryEvent.kt`

---

### Ticket #27: Day Entry ViewModel
**Priority**: P1 - High  
**Story Points**: 5  
**Type**: Presentation Layer

**Description:**
Implement ViewModel for day entry/edit functionality.

**Tasks:**
- [ ] Create `DayEntryViewModel.kt`
- [ ] Inject use cases: MarkDayAsOfficeUseCase, GetDailyEntryUseCase
- [ ] Accept date as saved state parameter
- [ ] Load existing entry if available
- [ ] Validate inputs (hours 0-24, date not future)
- [ ] Save entry on Save event
- [ ] Handle delete (if existing entry)
- [ ] Emit success/error states
- [ ] Add unit tests

**Acceptance Criteria:**
- Loads existing entries
- Validates inputs
- Saves successfully
- Unit tests pass

**Files to Create:**
- `presentation/dayentry/DayEntryViewModel.kt`
- `test/.../presentation/dayentry/DayEntryViewModelTest.kt`

---

### Ticket #28: Day Entry Screen UI
**Priority**: P1 - High  
**Story Points**: 5  
**Type**: Presentation Layer

**Description:**
Build Day Entry screen for marking attendance and entering hours.

**UI Elements:**
1. Date selector (read-only display or picker)
2. "Was in office?" toggle/switch
3. Hours worked input (slider + text field)
4. Optional notes field
5. Save button
6. Delete button (if existing entry)

**Tasks:**
- [ ] Create `DayEntryScreen.kt`
- [ ] Observe ViewModel state
- [ ] Create date display/picker
- [ ] Create in-office toggle switch
- [ ] Create hours input (slider with text display)
- [ ] Create notes TextField
- [ ] Add Save and Delete buttons
- [ ] Show validation errors
- [ ] Handle loading state
- [ ] Navigate back on save success
- [ ] Add previews

**Acceptance Criteria:**
- Form is intuitive and quick to use
- Validation errors clear
- Save and delete work
- Accessible
- Responsive layout

**Files to Create:**
- `presentation/dayentry/DayEntryScreen.kt`
- `presentation/dayentry/components/DatePicker.kt`
- `presentation/dayentry/components/HoursSlider.kt`
- `presentation/dayentry/components/NotesField.kt`

---

### Ticket #29: Day Entry UI Tests
**Priority**: P2 - Medium  
**Story Points**: 3  
**Type**: Testing

**Description:**
Write Compose UI tests for Day Entry screen.

**Tasks:**
- [ ] Create `DayEntryScreenTest.kt`
- [ ] Test form input
- [ ] Test save action
- [ ] Test validation errors
- [ ] Test delete action
- [ ] Test loading existing entry

**Acceptance Criteria:**
- All form interactions tested
- Tests are reliable

**Files to Create:**
- `androidTest/.../presentation/dayentry/DayEntryScreenTest.kt`

---

## ⚙️ Sprint 7: Settings Feature (Priority: Medium)

### Ticket #30: Settings UI State & Events
**Priority**: P2 - Medium  
**Story Points**: 2  
**Type**: Presentation Layer

**Description:**
Define UI state and events for Settings screen.

**Tasks:**
- [ ] Create `SettingsUiState.kt`:
  - officeSettings: OfficeSettings?
  - isLoading: Boolean
  - isSaved: Boolean
  - exportStatus: ExportStatus?
  - errorMessage: String?
- [ ] Create `SettingsEvent.kt`:
  - UpdateRequiredDays(days: Int)
  - UpdateRequiredHours(hours: Float)
  - UpdateWeekdayPreferences(preferences: List)
  - Save
  - ExportData
  - ImportData(uri: Uri)

**Acceptance Criteria:**
- State covers settings and export
- Events cover all actions

**Files to Create:**
- `presentation/settings/SettingsUiState.kt`
- `presentation/settings/SettingsEvent.kt`

---

### Ticket #31: Settings ViewModel
**Priority**: P2 - Medium  
**Story Points**: 5  
**Type**: Presentation Layer

**Description:**
Implement ViewModel for settings management.

**Tasks:**
- [ ] Create `SettingsViewModel.kt`
- [ ] Inject use cases: GetOfficeSettingsUseCase, SaveOfficeSettingsUseCase, ExportDataUseCase
- [ ] Load current settings on init
- [ ] Handle settings updates
- [ ] Implement save functionality
- [ ] Implement export to JSON
- [ ] Add unit tests

**Acceptance Criteria:**
- Settings load correctly
- Updates save successfully
- Export generates valid JSON
- Unit tests pass

**Files to Create:**
- `presentation/settings/SettingsViewModel.kt`
- `test/.../presentation/settings/SettingsViewModelTest.kt`

---

### Ticket #32: Settings Screen UI
**Priority**: P2 - Medium  
**Story Points**: 5  
**Type**: Presentation Layer

**Description:**
Build Settings screen for editing requirements and preferences.

**UI Sections:**
1. Requirements section (days/week, hours/week)
2. Weekday preferences editor
3. Data management (export/import)
4. About section

**Tasks:**
- [ ] Create `SettingsScreen.kt`
- [ ] Observe ViewModel state
- [ ] Create requirements editing section
- [ ] Create weekday preferences editor
- [ ] Add export button with file picker
- [ ] Add import button (optional)
- [ ] Add About section with app version
- [ ] Handle save confirmation
- [ ] Add previews

**Acceptance Criteria:**
- All settings editable
- Export works
- UI is clear and organized
- Responsive

**Files to Create:**
- `presentation/settings/SettingsScreen.kt`
- `presentation/settings/components/RequirementsSection.kt`
- `presentation/settings/components/PreferencesSection.kt`
- `presentation/settings/components/DataManagementSection.kt`
- `presentation/settings/components/AboutSection.kt`

---

### Ticket #33: Data Export/Import Use Cases
**Priority**: P2 - Medium  
**Story Points**: 5  
**Type**: Use Case

**Description:**
Implement export and import functionality for backup/restore.

**Tasks:**
- [ ] Create `ExportDataUseCase.kt`
  - Export settings, daily entries, holidays to JSON
  - Use Gson/Kotlinx Serialization
  - Return file URI
- [ ] Create `ImportDataUseCase.kt`
  - Parse JSON
  - Validate data
  - Import into database
- [ ] Add unit tests with sample JSON

**Acceptance Criteria:**
- Export generates valid JSON
- Import validates and loads data
- Handles errors gracefully

**Files to Create:**
- `domain/usecase/ExportDataUseCase.kt`
- `domain/usecase/ImportDataUseCase.kt`
- `test/.../domain/usecase/ExportDataUseCaseTest.kt`
- `test/.../domain/usecase/ImportDataUseCaseTest.kt`

---

## 🧪 Sprint 8: Testing & Polish (Priority: Medium)

### Ticket #34: Room DAO Instrumented Tests
**Priority**: P2 - Medium  
**Story Points**: 5  
**Type**: Testing

**Description:**
Write instrumented tests for all Room DAOs.

**Tasks:**
- [ ] Create `OfficeSettingsDaoTest.kt`
- [ ] Create `DailyEntryDaoTest.kt`
- [ ] Create `MonthlyLogDaoTest.kt`
- [ ] Create `HolidayDaoTest.kt`
- [ ] Test all CRUD operations
- [ ] Test queries with various inputs
- [ ] Use in-memory database for tests

**Acceptance Criteria:**
- All DAO methods tested
- Tests run reliably
- Coverage > 90%

**Files to Create:**
- `androidTest/.../data/local/dao/OfficeSettingsDaoTest.kt`
- `androidTest/.../data/local/dao/DailyEntryDaoTest.kt`
- `androidTest/.../data/local/dao/MonthlyLogDaoTest.kt`
- `androidTest/.../data/local/dao/HolidayDaoTest.kt`

---

### Ticket #35: Integration Tests for Critical Flows
**Priority**: P2 - Medium  
**Story Points**: 5  
**Type**: Testing

**Description:**
Write end-to-end integration tests for critical user flows.

**Flows to Test:**
1. Onboarding → Dashboard (first-time user)
2. Dashboard → Day Entry → Save → Back to Dashboard (mark day)
3. Dashboard → Settings → Update → Save (change settings)

**Tasks:**
- [ ] Create integration test suite
- [ ] Set up test navigation
- [ ] Test onboarding flow
- [ ] Test day entry flow
- [ ] Test settings update flow
- [ ] Use Hilt test modules

**Acceptance Criteria:**
- All critical flows covered
- Tests are stable
- Tests use test doubles for dependencies

**Files to Create:**
- `androidTest/.../integration/OnboardingFlowTest.kt`
- `androidTest/.../integration/DayEntryFlowTest.kt`
- `androidTest/.../integration/SettingsFlowTest.kt`
- `androidTest/.../di/TestAppModule.kt`

---

### Ticket #36: Accessibility Improvements
**Priority**: P2 - Medium  
**Story Points**: 3  
**Type**: Enhancement

**Description:**
Enhance app accessibility for screen readers and assistive technologies.

**Tasks:**
- [ ] Add content descriptions to all interactive elements
- [ ] Add semantic labels to custom components
- [ ] Ensure touch targets are >= 48dp
- [ ] Test with TalkBack
- [ ] Add accessibility tests
- [ ] Improve focus order

**Acceptance Criteria:**
- All screens navigable with TalkBack
- Content descriptions are meaningful
- Touch targets meet guidelines
- Accessibility scanner shows no errors

**Files to Modify:**
- All screen composables
- All component composables

---

### Ticket #37: Dark Mode & Theme Testing
**Priority**: P2 - Medium  
**Story Points**: 2  
**Type**: Enhancement

**Description:**
Ensure app looks good in dark mode and test theme consistency.

**Tasks:**
- [ ] Test all screens in dark mode
- [ ] Adjust colors for better contrast
- [ ] Test dynamic colors (Material You)
- [ ] Ensure accessibility contrast ratios
- [ ] Add theme switching in dev menu (optional)

**Acceptance Criteria:**
- Dark mode looks good on all screens
- Contrast ratios meet WCAG AA
- Dynamic colors work properly

**Files to Modify:**
- `ui/theme/Color.kt`
- `ui/theme/Theme.kt`

---

### Ticket #38: Performance Optimization
**Priority**: P3 - Low  
**Story Points**: 5  
**Type**: Enhancement

**Description:**
Optimize app performance (database queries, Compose recompositions).

**Tasks:**
- [ ] Add database indexes for frequently queried fields
- [ ] Optimize Compose recompositions with `remember`, `derivedStateOf`
- [ ] Use `LazyColumn` for lists
- [ ] Profile app with Android Profiler
- [ ] Reduce unnecessary ViewModel recreations
- [ ] Add pagination for large data sets (if needed)

**Acceptance Criteria:**
- App feels smooth and responsive
- No dropped frames on mid-range devices
- Database queries are fast

**Files to Modify:**
- Various DAOs (add indexes)
- Various Composables (optimization)

---

### Ticket #39: Error Handling & Edge Cases
**Priority**: P2 - Medium  
**Story Points**: 3  
**Type**: Bug/Enhancement

**Description:**
Improve error handling and cover edge cases.

**Edge Cases to Handle:**
- No settings configured (redirect to onboarding)
- Month with all weekends (edge case)
- Leap year February
- Future date entry attempts
- Negative hours
- Network time vs device time mismatch

**Tasks:**
- [ ] Add null/empty checks in ViewModels
- [ ] Show user-friendly error messages
- [ ] Handle database errors gracefully
- [ ] Add error logging (optional: Crashlytics)
- [ ] Test all edge cases

**Acceptance Criteria:**
- App doesn't crash on edge cases
- Error messages are user-friendly
- Errors are logged for debugging

**Files to Modify:**
- All ViewModels
- All Use Cases

---

## 📦 Sprint 9: Release Preparation (Priority: Medium)

### Ticket #40: App Icon & Branding
**Priority**: P2 - Medium  
**Story Points**: 2  
**Type**: Design

**Description:**
Create app icon and finalize branding.

**Tasks:**
- [ ] Design app icon (or use placeholder)
- [ ] Generate icon variants (adaptive, round, etc.)
- [ ] Update launcher icon resources
- [ ] Choose app name (if different from "Go2Office")
- [ ] Update strings.xml with final app name

**Acceptance Criteria:**
- App icon looks professional
- Icon works on all Android versions
- App name is finalized

**Files to Modify:**
- `res/drawable/ic_launcher_foreground.xml`
- `res/mipmap-*/`
- `res/values/strings.xml`

---

### Ticket #41: ProGuard Rules & Release Build
**Priority**: P1 - High  
**Story Points**: 3  
**Type**: Configuration

**Description:**
Configure ProGuard/R8 rules for release build and test release APK.

**Tasks:**
- [ ] Add ProGuard rules for Room, Hilt, Gson/Serialization
- [ ] Test release build
- [ ] Verify app works correctly after obfuscation
- [ ] Enable code shrinking and obfuscation
- [ ] Test on multiple devices

**Acceptance Criteria:**
- Release build compiles successfully
- App functions correctly in release mode
- APK size is reasonable

**Files to Modify:**
- `app/proguard-rules.pro`
- `app/build.gradle.kts`

---

### Ticket #42: Documentation & README
**Priority**: P2 - Medium  
**Story Points**: 2  
**Type**: Documentation

**Description:**
Update README and add user documentation.

**Tasks:**
- [ ] Update README with:
  - App description
  - Features list
  - Installation instructions
  - Screenshots (optional)
  - Architecture diagram
  - License info
- [ ] Add CHANGELOG.md
- [ ] Add CONTRIBUTING.md (if open source)
- [ ] Document known issues/limitations

**Acceptance Criteria:**
- README is comprehensive
- Easy for new developers to understand project

**Files to Modify:**
- `README.md`

**Files to Create:**
- `CHANGELOG.md`
- `CONTRIBUTING.md`

---

### Ticket #43: App Store Listing Preparation
**Priority**: P3 - Low  
**Story Points**: 3  
**Type**: Release

**Description:**
Prepare materials for Google Play Store listing.

**Tasks:**
- [ ] Write app description (short & full)
- [ ] Create feature graphic
- [ ] Take screenshots (phone + tablet)
- [ ] Write release notes
- [ ] Choose category and tags
- [ ] Add privacy policy (if needed)
- [ ] Create promo video (optional)

**Acceptance Criteria:**
- All Play Store assets ready
- Descriptions are clear and compelling

**Files to Create:**
- `docs/PlayStoreAssets/` (screenshots, graphics, etc.)
- `PRIVACY_POLICY.md`

---

## 🚀 Sprint 10: Optional Enhancements (Priority: Low)

### Ticket #44: Widget for Home Screen (Optional)
**Priority**: P3 - Low  
**Story Points**: 8  
**Type**: Feature

**Description:**
Create home screen widget showing monthly progress.

**Tasks:**
- [ ] Create Glance widget for Jetpack Compose
- [ ] Show required vs completed days/hours
- [ ] Show progress bar
- [ ] Add tap action to open app
- [ ] Support different widget sizes

**Acceptance Criteria:**
- Widget displays correctly
- Updates periodically
- Tapping opens app

**Files to Create:**
- `presentation/widget/MonthProgressWidget.kt`

---

### Ticket #45: Notifications for Reminders (Optional)
**Priority**: P3 - Low  
**Story Points**: 5  
**Type**: Feature

**Description:**
Add optional notifications to remind user to log office days.

**Tasks:**
- [ ] Create notification channel
- [ ] Schedule daily/weekly reminder
- [ ] Allow user to configure reminder time
- [ ] Add notification preferences in Settings
- [ ] Handle notification tap (open day entry)

**Acceptance Criteria:**
- Notifications work reliably
- User can enable/disable in settings
- Respects Do Not Disturb

**Files to Create:**
- `util/NotificationManager.kt`
- `domain/usecase/ScheduleReminderUseCase.kt`

---

### Ticket #46: Cloud Sync (Optional)
**Priority**: P3 - Low  
**Story Points**: 13  
**Type**: Feature

**Description:**
Add optional cloud sync for backup and multi-device support.

**Tasks:**
- [ ] Choose sync backend (Firebase, custom server)
- [ ] Implement sync logic
- [ ] Handle conflicts (last-write-wins)
- [ ] Add sync toggle in Settings
- [ ] Show sync status

**Acceptance Criteria:**
- Data syncs across devices
- Conflicts resolved appropriately
- Works offline and syncs when online

**Files to Create:**
- `data/remote/` (new package)
- Various sync-related classes

---

## 📊 Summary

**Total Tickets**: 46  
**Total Story Points**: ~180  

**Priority Breakdown:**
- **P0 (Critical)**: 3 tickets, ~10 points
- **P1 (High)**: 23 tickets, ~100 points
- **P2 (Medium)**: 17 tickets, ~55 points
- **P3 (Low)**: 3 tickets, ~15 points

**Sprint Breakdown:**
- **Sprint 0** (Foundation): 3 tickets, 10 points
- **Sprint 1** (Domain): 4 tickets, 13 points
- **Sprint 2** (Business Logic): 7 tickets, 31 points
- **Sprint 3** (UI Foundation): 3 tickets, 11 points
- **Sprint 4** (Onboarding): 4 tickets, 18 points
- **Sprint 5** (Dashboard): 4 tickets, 28 points
- **Sprint 6** (Day Entry): 4 tickets, 15 points
- **Sprint 7** (Settings): 4 tickets, 17 points
- **Sprint 8** (Testing): 6 tickets, 23 points
- **Sprint 9** (Release): 4 tickets, 10 points
- **Sprint 10** (Optional): 3 tickets, 26 points

**Estimated Timeline**: 10-12 weeks (at ~20-25 points per 2-week sprint)

---

## 🎯 Implementation Order Priority

**Phase 1 - Core (Must Have):**
1. Tickets #1-3 (Setup)
2. Tickets #4-7 (Data & Domain layers)
3. Tickets #8-13 (Business logic)
4. Tickets #15-17 (UI foundation)
5. Tickets #18-21 (Onboarding)
6. Tickets #22-25 (Dashboard)
7. Tickets #26-29 (Day Entry)

**Phase 2 - Polish (Should Have):**
8. Tickets #30-33 (Settings)
9. Tickets #34-39 (Testing & polish)

**Phase 3 - Release (Must Have for Production):**
10. Tickets #40-43 (Release prep)

**Phase 4 - Nice to Have (Optional):**
11. Tickets #44-46 (Enhancements)

---

## 📝 Notes for GitHub Issues

When creating these tickets in GitHub:

1. **Use Labels:**
   - `setup`, `data-layer`, `domain-layer`, `presentation-layer`, `testing`, `documentation`, `enhancement`, `bug`
   - `P0-critical`, `P1-high`, `P2-medium`, `P3-low`

2. **Use Milestones:**
   - Sprint 0: Foundation
   - Sprint 1: Domain Layer
   - Sprint 2: Business Logic
   - (etc.)

3. **Link Related Issues:**
   - Use "Depends on #X" or "Blocks #Y"
   - Link UI tickets to their ViewModel tickets

4. **Add Acceptance Criteria Checklists:**
   - Convert acceptance criteria to GitHub task lists

5. **Assign Story Points:**
   - Use GitHub Projects for story point tracking

