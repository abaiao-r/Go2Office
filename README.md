# Go2Office

Android app to help users plan and track required in-office days and hours per month, built with Jetpack Compose and Clean Architecture (MVVM).

## 📁 Project Structure

### Main Source (`app/src/main/java/com/example/go2office/`)

```
├── data/                          # Data Layer (Framework & Infrastructure)
│   ├── local/
│   │   ├── dao/                   # Room Database DAOs (Data Access Objects)
│   │   └── entities/              # Room Database Entities
│   ├── mapper/                    # Mappers between Entity ↔ Domain Model
│   └── repository/                # Repository Implementations
│
├── domain/                        # Domain Layer (Business Logic - Framework Independent)
│   ├── model/                     # Domain Models (Business entities)
│   ├── repository/                # Repository Interfaces
│   └── usecase/                   # Use Cases (Business rules/interactors)
│
├── presentation/                  # Presentation Layer (UI)
│   ├── components/                # Reusable Compose Components
│   ├── dashboard/                 # Dashboard Screen + ViewModel
│   ├── dayentry/                  # Day Entry Screen + ViewModel
│   ├── navigation/                # Navigation Graph & Routes
│   ├── onboarding/                # Onboarding Screen + ViewModel
│   └── settings/                  # Settings Screen + ViewModel
│
├── di/                            # Dependency Injection (Hilt Modules)
├── ui/theme/                      # Compose Theme (Colors, Typography, Theme)
└── util/                          # Utilities & Extensions

MainActivity.kt                    # Main Activity (Navigation Host)
```

### Unit Tests (`app/src/test/java/com/example/go2office/`)

```
├── data/
│   └── repository/                # Repository Unit Tests
├── domain/
│   └── usecase/                   # Use Case Unit Tests (Business Logic Tests)
├── presentation/
│   ├── dashboard/                 # Dashboard ViewModel Tests
│   ├── dayentry/                  # Day Entry ViewModel Tests
│   ├── onboarding/                # Onboarding ViewModel Tests
│   └── settings/                  # Settings ViewModel Tests
└── util/                          # Utility Tests
```

### Android Instrumented Tests (`app/src/androidTest/java/com/example/go2office/`)

```
├── data/
│   └── local/
│       └── dao/                   # Room DAO Tests
├── di/                            # DI Test Modules
└── presentation/
    ├── dashboard/                 # Dashboard UI Tests (Compose Tests)
    ├── dayentry/                  # Day Entry UI Tests
    ├── onboarding/                # Onboarding UI Tests
    └── settings/                  # Settings UI Tests
```

## 🏗️ Architecture Overview

**Clean Architecture with MVVM Pattern**

- **Data Layer**: Room Database, DAOs, Entities, Repository Implementations
- **Domain Layer**: Business Models, Repository Interfaces, Use Cases
- **Presentation Layer**: Compose Screens, ViewModels (one per screen), UI Components

**Key Principles:**
- One ViewModel per screen
- Unidirectional data flow (UDF)
- Dependency Injection with Hilt
- Repository pattern for data access
- Use Cases encapsulate business logic

## 🗂️ File Organization by Feature

Each screen follows this structure:

### Example: Dashboard Feature
```
presentation/dashboard/
├── DashboardScreen.kt             # Composable UI
├── DashboardViewModel.kt          # ViewModel (StateFlow, Events, Business Logic)
└── DashboardUiState.kt            # UI State Data Class

domain/usecase/
├── GetMonthlyRequirementsUseCase.kt
├── GetSuggestedDaysUseCase.kt
└── CalculateMonthProgressUseCase.kt

test/.../presentation/dashboard/
└── DashboardViewModelTest.kt      # Unit Tests

androidTest/.../presentation/dashboard/
└── DashboardScreenTest.kt         # UI/Compose Tests
```

## 🎯 Screens & ViewModels

| Screen | ViewModel | Purpose |
|--------|-----------|---------|
| OnboardingScreen | OnboardingViewModel | First-run setup (required days, weekday preferences) |
| DashboardScreen | DashboardViewModel | Main view: monthly requirements, progress, suggested days |
| DayEntryScreen | DayEntryViewModel | Mark day as in-office, enter hours |
| SettingsScreen | SettingsViewModel | Edit requirements, preferences, export data |

## 📦 Dependencies & Technologies

- **Jetpack Compose**: UI Framework
- **Hilt**: Dependency Injection
- **Room**: Local Database (persistence)
- **ViewModel & StateFlow**: State management
- **Navigation Compose**: Screen navigation
- **JUnit & Robolectric**: Testing
- **Compose Testing**: UI Tests

## 🧪 Testing Strategy

- **Unit Tests**: Use Cases, ViewModels, Repositories (JUnit)
- **Instrumented Tests**: Room DAOs, Compose UI (Robolectric/Espresso)
- Focus on business logic in domain layer tests
- UI tests for critical user flows
