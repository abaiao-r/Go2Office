# Go2Office Architecture & File Organization

## 📋 Complete File Location Guide

### Data Layer (`data/`)

#### Local Database (`data/local/`)

**`data/local/dao/`** - Room Database Access Objects
- `OfficeSettingsDao.kt` - User settings CRUD operations
- `MonthlyLogDao.kt` - Monthly summary CRUD
- `DailyEntryDao.kt` - Daily attendance records CRUD
- `HolidayDao.kt` - Custom holidays/non-required days

**`data/local/entities/`** - Room Database Tables
- `OfficeSettingsEntity.kt` - User requirements & preferences
- `MonthlyLogEntity.kt` - Monthly aggregated data
- `DailyEntryEntity.kt` - Individual day records
- `HolidayEntity.kt` - Holiday/exception days
- `OfficeDatabase.kt` - Room Database definition

**`data/mapper/`** - Entity ↔ Domain Model Converters
- `SettingsMapper.kt` - Maps OfficeSettingsEntity ↔ OfficeSettings
- `MonthlyLogMapper.kt` - Maps MonthlyLogEntity ↔ MonthlyLog
- `DailyEntryMapper.kt` - Maps DailyEntryEntity ↔ DailyEntry
- `HolidayMapper.kt` - Maps HolidayEntity ↔ Holiday

**`data/repository/`** - Repository Implementations
- `OfficeRepositoryImpl.kt` - Implements domain repository interface
- Repository coordinates between DAOs and domain layer

---

### Domain Layer (`domain/`)

#### Business Models (`domain/model/`)
- `OfficeSettings.kt` - User requirements (days/week, hours, preferences)
- `MonthlyRequirements.kt` - Calculated monthly targets
- `MonthlyLog.kt` - Monthly summary (required vs completed)
- `DailyEntry.kt` - Single day attendance record
- `Holiday.kt` - Non-required day
- `SuggestedDay.kt` - Algorithm output for suggested days
- `WeekdayPreference.kt` - Ordered weekday preferences
- `MonthProgress.kt` - Progress tracking data

#### Repository Interfaces (`domain/repository/`)
- `OfficeRepository.kt` - Interface defining data operations
  - Settings CRUD
  - Daily entry operations
  - Monthly log queries
  - Holiday management

#### Use Cases (`domain/usecase/`)

**Settings & Onboarding:**
- `SaveOfficeSettingsUseCase.kt` - Save user preferences
- `GetOfficeSettingsUseCase.kt` - Retrieve settings
- `ValidateSettingsUseCase.kt` - Validate input requirements

**Monthly Calculations:**
- `CalculateMonthlyRequirementsUseCase.kt` - Compute required days/hours for a month
- `GetMonthProgressUseCase.kt` - Track current month progress
- `GetMonthlyLogsUseCase.kt` - Historical monthly data

**Daily Operations:**
- `MarkDayAsOfficeUseCase.kt` - Record office attendance
- `UpdateDailyHoursUseCase.kt` - Update hours for a day
- `GetDailyEntriesUseCase.kt` - Retrieve entries for date range

**Suggestions:**
- `GetSuggestedOfficeDaysUseCase.kt` - Calculate next recommended days
- `GetRemainingRequirementsUseCase.kt` - What's left this month

**Holidays:**
- `AddHolidayUseCase.kt` - Mark day as non-required
- `GetHolidaysForMonthUseCase.kt` - Get holidays in range

**Data Management:**
- `ExportDataUseCase.kt` - Export to JSON
- `ImportDataUseCase.kt` - Import from JSON

---

### Presentation Layer (`presentation/`)

#### Shared Components (`presentation/components/`)
- `CalendarCard.kt` - Monthly calendar view
- `ProgressCard.kt` - Progress indicator component
- `DayChip.kt` - Day of week chip/button
- `RequirementSummaryCard.kt` - Summary card component
- `HourEntryField.kt` - Hours input field
- `EmptyState.kt` - Empty state placeholder
- `LoadingIndicator.kt` - Loading states
- `ErrorDialog.kt` - Error message dialog
- `ConfirmationDialog.kt` - Confirmation prompts

#### Navigation (`presentation/navigation/`)
- `NavGraph.kt` - Compose Navigation setup
- `Screen.kt` - Sealed class/enum for routes
- `NavigationActions.kt` - Navigation helper

#### Onboarding Feature (`presentation/onboarding/`)
- `OnboardingScreen.kt` - First-run setup UI
- `OnboardingViewModel.kt` - Manages onboarding flow
- `OnboardingUiState.kt` - UI state data class
- `OnboardingEvent.kt` - User interaction events
- `components/`
  - `RequiredDaysSelector.kt` - Days per week picker
  - `WeekdayPreferenceList.kt` - Drag-and-drop preference order
  - `HoursSetupCard.kt` - Hours configuration

#### Dashboard Feature (`presentation/dashboard/`)
- `DashboardScreen.kt` - Main screen UI
- `DashboardViewModel.kt` - Dashboard business logic
- `DashboardUiState.kt` - Dashboard state
- `DashboardEvent.kt` - User actions
- `components/`
  - `MonthSelector.kt` - Month navigation
  - `RequirementsOverview.kt` - Requirements display
  - `ProgressSection.kt` - Progress bars/stats
  - `SuggestedDaysSection.kt` - Suggested days list
  - `QuickActionButtons.kt` - Quick entry actions

#### Day Entry Feature (`presentation/dayentry/`)
- `DayEntryScreen.kt` - Day detail/entry UI
- `DayEntryViewModel.kt` - Day entry logic
- `DayEntryUiState.kt` - Entry form state
- `DayEntryEvent.kt` - Form events
- `components/`
  - `DatePicker.kt` - Date selection
  - `HoursSlider.kt` - Hours input slider
  - `NotesField.kt` - Optional notes
  - `SaveButton.kt` - Save action

#### Settings Feature (`presentation/settings/`)
- `SettingsScreen.kt` - Settings UI
- `SettingsViewModel.kt` - Settings management
- `SettingsUiState.kt` - Settings state
- `SettingsEvent.kt` - Settings actions
- `components/`
  - `RequirementsSection.kt` - Edit requirements
  - `PreferencesSection.kt` - Edit preferences
  - `DataManagementSection.kt` - Export/import
  - `AboutSection.kt` - App info

---

### Dependency Injection (`di/`)
- `AppModule.kt` - App-level dependencies (Context, etc.)
- `DatabaseModule.kt` - Room database provision
- `RepositoryModule.kt` - Repository bindings
- `UseCaseModule.kt` - Use case provision (if needed)

---

### Utilities (`util/`)
- `DateUtils.kt` - Date/calendar calculations, weekday filtering
- `TimeZoneUtils.kt` - Timezone handling
- `PreferenceUtils.kt` - DataStore preferences helper
- `Extensions.kt` - Kotlin extensions
- `Constants.kt` - App constants
- `Formatters.kt` - Date/number formatters
- `ValidationRules.kt` - Input validation

---

### UI Theme (`ui/theme/`)
- `Color.kt` - Color palette
- `Theme.kt` - Material 3 theme setup
- `Type.kt` - Typography styles
- `Shape.kt` - Shape definitions

---

### Root (`com/example/go2office/`)
- `MainActivity.kt` - Single activity host
- `Go2OfficeApplication.kt` - Application class (Hilt entry point)

---

## 🧪 Test Structure

### Unit Tests (`app/src/test/.../`)

**`domain/usecase/`**
- `CalculateMonthlyRequirementsUseCaseTest.kt`
- `GetSuggestedOfficeDaysUseCaseTest.kt`
- `GetMonthProgressUseCaseTest.kt`
- `MarkDayAsOfficeUseCaseTest.kt`
- `ValidateSettingsUseCaseTest.kt`

**`data/repository/`**
- `OfficeRepositoryImplTest.kt` - Mock DAO tests

**`presentation/onboarding/`**
- `OnboardingViewModelTest.kt` - Onboarding flow tests

**`presentation/dashboard/`**
- `DashboardViewModelTest.kt` - Dashboard logic tests

**`presentation/dayentry/`**
- `DayEntryViewModelTest.kt` - Entry validation tests

**`presentation/settings/`**
- `SettingsViewModelTest.kt` - Settings update tests

**`util/`**
- `DateUtilsTest.kt` - Date calculation tests
- `TimeZoneUtilsTest.kt` - Timezone tests

---

### Android Instrumented Tests (`app/src/androidTest/.../`)

**`data/local/dao/`**
- `OfficeSettingsDaoTest.kt` - Room DAO tests
- `MonthlyLogDaoTest.kt`
- `DailyEntryDaoTest.kt`
- `HolidayDaoTest.kt`

**`presentation/onboarding/`**
- `OnboardingScreenTest.kt` - Compose UI tests

**`presentation/dashboard/`**
- `DashboardScreenTest.kt` - Dashboard UI tests

**`presentation/dayentry/`**
- `DayEntryScreenTest.kt` - Entry form UI tests

**`presentation/settings/`**
- `SettingsScreenTest.kt` - Settings UI tests

**`di/`**
- `TestAppModule.kt` - Test DI setup

---

## 🔄 Data Flow Example

**User marks a day as in-office:**

1. **UI** (`DayEntryScreen.kt`): User taps save button
2. **ViewModel** (`DayEntryViewModel.kt`): Validates input, calls use case
3. **Use Case** (`MarkDayAsOfficeUseCase.kt`): Business logic (weekend check, validation)
4. **Repository** (`OfficeRepositoryImpl.kt`): Coordinates data operations
5. **Mapper** (`DailyEntryMapper.kt`): Converts domain model → entity
6. **DAO** (`DailyEntryDao.kt`): Inserts into Room database
7. **StateFlow** updates UI via ViewModel

---

## 📦 Module Dependencies

```
presentation → domain → data

presentation:
  - Depends on: domain (models, use cases)
  - Does NOT depend on: data

domain:
  - Pure Kotlin module (no Android dependencies)
  - Defines interfaces only

data:
  - Implements domain interfaces
  - Depends on: domain
  - Contains: Room, DAOs, implementation details
```

---

## 🎯 Key Architecture Decisions

1. **Clean Architecture**: Clear separation of concerns
2. **One ViewModel per screen**: Simplified state management
3. **Use Cases**: Encapsulate business rules, testable
4. **Repository Pattern**: Abstract data sources
5. **Mapper Pattern**: Separate database entities from domain models
6. **Hilt DI**: Compile-time dependency injection
7. **Room Database**: Type-safe local persistence
8. **StateFlow**: Reactive state management
9. **Unidirectional Data Flow**: Predictable state updates
10. **Compose**: Modern declarative UI

---

## 📱 Minimum API Level

**Target: API 26+ (Android 8.0 Oreo)**
- Modern java.time APIs available
- Broad device coverage (~95%)
- Compose officially supports API 21+, but java.time simplifies date handling

---

## 🌍 Localization & Timezone

- Use `java.time.ZonedDateTime` and `ZoneId.systemDefault()`
- All date calculations respect user's locale
- Calendar weeks follow user's locale (first day of week)
- Month names and formats via Android resources

