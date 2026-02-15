# 🏗️ Architecture Documentation

## Overview

Go2Office implements **Clean Architecture** with **MVVM** pattern, ensuring separation of concerns, testability, and maintainability.

---

## System Architecture

![System Architecture](../diagrams/system-architecture.png)

### High-Level Layers

```plantuml
@startuml system-architecture
!define RECTANGLE class

skinparam component {
    BackgroundColor<<presentation>> LightBlue
    BackgroundColor<<domain>> LightGreen
    BackgroundColor<<data>> LightYellow
    BorderColor Black
    FontSize 14
}

package "Presentation Layer" <<presentation>> {
    [Compose UI]
    [ViewModel]
    [UI State]
}

package "Domain Layer" <<domain>> {
    [Use Cases]
    [Domain Models]
    [Repository Interface]
}

package "Data Layer" <<data>> {
    [Repository Impl]
    [Room Database]
    [Remote API]
    [Geofencing]
}

[Compose UI] --> [ViewModel] : Events
[ViewModel] --> [Compose UI] : State
[ViewModel] --> [Use Cases]
[Use Cases] --> [Repository Interface]
[Repository Impl] ..|> [Repository Interface] : implements
[Repository Impl] --> [Room Database]
[Repository Impl] --> [Remote API]
[Repository Impl] --> [Geofencing]

note right of [Domain Models]
  Pure Kotlin
  No Android dependencies
  Business logic only
end note

@enduml
```

---

## Layer Responsibilities

### 1. Presentation Layer

**Purpose**: User interface and user interaction handling

**Components**:
- **Composable Screens** - UI built with Jetpack Compose
- **ViewModels** - Manage UI state, coordinate use cases
- **UI State** - Immutable data classes
- **Events** - User actions (sealed classes)

**Flow**:
```
User Interaction → Event → ViewModel → Use Case → Update State → UI Recomposes
```

---

### 2. Domain Layer

**Purpose**: Business logic (framework-independent)

**Components**:
- **Use Cases** - Single-responsibility business operations
- **Domain Models** - Pure Kotlin business entities
- **Repository Interfaces** - Data access abstractions

**Principles**:
- ✅ No Android dependencies
- ✅ Pure Kotlin
- ✅ Easily testable
- ✅ Reusable

---

### 3. Data Layer

**Purpose**: Data persistence and external data sources

**Components**:
- **Repository Implementations** - Concrete data access
- **Room Database** - Local SQLite database
- **DAOs** - Data access objects
- **Remote APIs** - Holiday data service
- **Geofencing Service** - Location tracking

---

## Component Diagram

```plantuml
@startuml components
!theme plain

package "App" {
    [MainActivity] <<Activity>>
    [Go2OfficeApp] <<Application>>
}

package "Presentation" {
    [DashboardScreen]
    [OnboardingScreen]
    [SettingsScreen]
    [CalendarScreen]
    
    [DashboardViewModel]
    [OnboardingViewModel]
    [SettingsViewModel]
    [CalendarViewModel]
}

package "Domain" {
    [GetMonthProgressUseCase]
    [GetSuggestedDaysUseCase]
    [CalculateRequirementsUseCase]
    [AggregateSessionsUseCase]
    
    interface "OfficeRepository" as IRepo
}

package "Data" {
    [OfficeRepositoryImpl]
    [OfficeDatabase]
    [GeofencingManager]
    [HolidayApiService]
}

package "DI (Hilt)" {
    [AppModule]
    [DatabaseModule]
    [RepositoryModule]
}

[MainActivity] --> [DashboardScreen]
[DashboardScreen] --> [DashboardViewModel]
[DashboardViewModel] --> [GetMonthProgressUseCase]
[DashboardViewModel] --> [GetSuggestedDaysUseCase]
[GetMonthProgressUseCase] --> IRepo
[GetSuggestedDaysUseCase] --> IRepo
[OfficeRepositoryImpl] ..|> IRepo
[OfficeRepositoryImpl] --> [OfficeDatabase]
[OfficeRepositoryImpl] --> [GeofencingManager]
[OfficeRepositoryImpl] --> [HolidayApiService]

[AppModule] ..> [Go2OfficeApp] : provides
[DatabaseModule] ..> [OfficeDatabase] : provides
[RepositoryModule] ..> [OfficeRepositoryImpl] : binds

@enduml
```

---

## Data Flow

```plantuml
@startuml data-flow
!theme plain

actor User
participant "Compose\nScreen" as UI
participant ViewModel
participant "Use\nCase" as UC
participant Repository
database "Room\nDatabase" as DB

User -> UI : Click/Input
activate UI
UI -> ViewModel : Send Event
activate ViewModel
ViewModel -> UC : Execute
activate UC
UC -> Repository : Query Data
activate Repository
Repository -> DB : SQL Query
activate DB
DB --> Repository : Entities
deactivate DB
Repository --> UC : Domain Models
deactivate Repository
UC --> ViewModel : Result
deactivate UC
ViewModel -> ViewModel : Update State
ViewModel --> UI : StateFlow
deactivate ViewModel
UI -> UI : Recompose
UI --> User : Display
deactivate UI

@enduml
```

---

## State Management

```plantuml
@startuml state-management
!theme plain

package "Unidirectional Data Flow" {
    state "UI State" as UIState
    state "User Event" as Event
    state "ViewModel" as VM
    state "Use Cases" as UC
    state "Repository" as Repo
    
    [*] --> UIState
    UIState --> Event : User\nInteraction
    Event --> VM : Handle\nEvent
    VM --> UC : Execute
    UC --> Repo : Query/Mutate
    Repo --> UC : Result
    UC --> VM : Success/Error
    VM --> UIState : Update\nState
    UIState --> UIState : UI\nRecomposes
}

note right of UIState
  Immutable data class
  Single source of truth
  Observed via StateFlow
end note

@enduml
```

---

## Dependency Injection

```plantuml
@startuml dependency-injection
!theme plain

package "Hilt Modules" {
    [AppModule] <<@Module>>
    [DatabaseModule] <<@Module>>
    [RepositoryModule] <<@Module>>
}

package "Scopes" {
    [SingletonComponent]
    [ViewModelComponent]
}

package "Injectable" {
    [Application] <<@HiltAndroidApp>>
    [Activity] <<@AndroidEntryPoint>>
    [ViewModel] <<@HiltViewModel>>
}

[AppModule] --> [SingletonComponent] : @InstallIn
[DatabaseModule] --> [SingletonComponent] : @InstallIn
[RepositoryModule] --> [SingletonComponent] : @InstallIn

[SingletonComponent] ..> [Application] : provides
[SingletonComponent] ..> [Activity] : injects
[ViewModelComponent] ..> [ViewModel] : injects

note right of [SingletonComponent]
  Lifetime: Application
  Provides: Database, Repository,
            Services, Context
end note

note right of [ViewModelComponent]
  Lifetime: ViewModel
  Provides: Use Cases
end note

@enduml
```

---

## Database Schema

```plantuml
@startuml database-schema
!theme plain

entity "office_settings" {
    * id : Long <<PK>>
    --
    required_days_per_week : Int
    required_hours_per_week : Float
    weekday_preferences : String
}

entity "daily_entries" {
    * date : LocalDate <<PK>>
    --
    hours_worked : Float
    is_office_day : Boolean
    notes : String?
}

entity "office_presence" {
    * id : Long <<PK>>
    --
    date : LocalDate
    entry_time : DateTime?
    exit_time : DateTime?
}

entity "holidays" {
    * id : Long <<PK>>
    --
    date : LocalDate
    name : String
    country_code : String
    is_public : Boolean
}

entity "office_location" {
    * id : Long <<PK>>
    --
    latitude : Double
    longitude : Double
    radius_meters : Float
    name : String
}

office_presence }o--|| daily_entries : aggregates to
office_location ||--o{ office_presence : tracks at

@enduml
```

---

## Navigation Flow

```plantuml
@startuml navigation
!theme plain

state "Onboarding" as Onboarding
state "Dashboard" as Dashboard
state "Settings" as Settings
state "Calendar" as Calendar
state "Day Entry" as DayEntry
state "Auto-Detection" as AutoDetect
state "Permissions" as Permissions

[*] --> Onboarding : First Launch
Onboarding --> Permissions : Enable\nAuto-Detect
Permissions --> AutoDetect : Granted
AutoDetect --> Dashboard : Complete
Onboarding --> Dashboard : Skip\nAuto-Detect

Dashboard --> Settings : Settings Icon
Dashboard --> Calendar : Calendar Icon
Dashboard --> DayEntry : Click Day

Settings --> Dashboard : Back
Settings --> Calendar : Holidays
Settings --> AutoDetect : Setup Location

Calendar --> Settings : Back

DayEntry --> Dashboard : Save

note right of Dashboard
  Main screen
  Shows progress
  and suggestions
end note

@enduml
```

---

## Key Design Patterns

### 1. Repository Pattern
- Abstracts data sources
- Single source of truth
- Easy to swap implementations

### 2. Observer Pattern
- Flow/StateFlow for reactive data
- UI observes state changes
- Automatic updates

### 3. Dependency Injection
- Hilt for compile-time safety
- Singleton and ViewModelScoped
- Testability through interfaces

### 4. Use Case Pattern
- Single responsibility
- Reusable business logic
- Framework-independent

### 5. Mapper Pattern
- Separate data and domain models
- Clean layer boundaries
- Type safety

---

## Testing Strategy

```plantuml
@startuml testing-pyramid
!theme plain

rectangle "UI Tests\n(Compose Testing)" as UI #LightBlue
rectangle "Integration Tests\n(Repository + Room)" as Integration #LightGreen
rectangle "Unit Tests\n(ViewModels + Use Cases)" as Unit #LightYellow

UI -[hidden]down-> Integration
Integration -[hidden]down-> Unit

note right of UI
  10% - Slow
  End-to-end flows
  User interactions
end note

note right of Integration
  20% - Medium
  Data layer
  Database operations
end note

note right of Unit
  70% - Fast
  Business logic
  State changes
  No dependencies
end note

@enduml
```

---

## Performance Considerations

### Caching Strategy
- Room as single source of truth
- In-memory caching for calculations
- Flow-based reactive updates

### Background Processing
- Geofencing (system-managed, battery efficient)
- Coroutines for async operations
- No continuous polling

### UI Optimization
- LazyColumn for lists
- Remember/derivedStateOf for computations
- Immutable state for efficient recomposition

---

## Security

- All data stored locally (Room encrypted at rest)
- No cloud sync (privacy by design)
- Location permissions explicitly requested
- No analytics or tracking

---

## Scalability

### Current Support
- Single user
- Local data only
- No backend

### Future Extensions
- Multi-user support
- Cloud sync (optional)
- Team analytics
- API for HR systems

---

**See Also**:
- [Business Logic](../business/README.md)
- [Use Case Flows](../diagrams/use-case-flows.md)
- [API Documentation](../api/README.md)

