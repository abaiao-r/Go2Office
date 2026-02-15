# 🏢 Go2Office

**Never Miss Your Office Requirements Again**

An Android app that helps you meet hybrid work requirements by automatically tracking office attendance and suggesting optimal days to work on-site.

---

## 🎯 Why Go2Office?

### The Problem

Modern hybrid work policies require employees to work from the office a certain number of days per month. Managing this manually is difficult:

- ❌ **Forgetting to track** office days leads to scrambling at month-end
- ❌ **Poor planning** causes awkward schedules and missed requirements
- ❌ **Manual logging** is tedious and error-prone
- ❌ **No visibility** into monthly progress until it's too late

### The Solution

Go2Office solves this by:

- ✅ **Automatic tracking** via GPS geofencing - set it and forget it
- ✅ **Smart suggestions** that respect your preferences and holidays
- ✅ **Real-time progress** so you always know where you stand
- ✅ **Intelligent planning** that adapts to your schedule changes

---

## 🚀 Quick Start

### Requirements

- Android 8.0+ (API 26)
- Location permissions
- ~10MB storage

### Installation

```bash
# Clone and build
git clone https://github.com/yourusername/Go2Office.git
cd Go2Office
./gradlew installDebug
```

### First Time Setup (30 seconds)

1. **Set your requirements** - e.g., "3 days per week"
2. **Choose preferred days** - e.g., "Monday > Tuesday > Wednesday..."
3. **Enable auto-detection** (optional) - Set your office location
4. **Done!** The app handles the rest

---

## 📱 How It Works

### 1. Configure Your Requirements

Tell the app what your company requires:
- Days per week (1-5)
- Hours per day (1-12)

### 2. Get Smart Suggestions

The app suggests optimal days based on:
- Your weekday preferences
- Public holidays
- Your vacation plans
- Fair distribution across the month

### 3. Automatic Tracking (Optional)

Enable geofencing to automatically detect when you're at the office:
- Tracks entry/exit times
- Calculates hours (7am-7pm only, max 10h/day)
- Works in background - no manual logging needed

### 4. Monitor Progress

Dashboard shows at a glance:
- Days completed vs. required
- Hours completed vs. required
- Suggested upcoming days
- Real-time alerts if you're falling behind

---

## ✨ Key Features

| Feature | Description |
|---------|-------------|
| 🤖 **Auto-Detection** | Geofencing automatically tracks office presence |
| 📊 **Smart Planning** | AI suggests optimal days based on your preferences |
| 📅 **Holiday Support** | Loads 100+ countries' holidays automatically |
| 🎯 **Progress Tracking** | Real-time dashboard with visual indicators |
| 🔔 **Reminders** | Notifications when you need to plan office days |
| 🌙 **Dark Mode** | Full dark theme support |
| 🔒 **Privacy First** | All data stored locally, no cloud sync |
| 💯 **100% Free** | No subscriptions, no ads, no hidden costs |

---

## 📊 Business Value

### For Employees

- **Save Time** - No manual tracking or calculations
- **Reduce Stress** - Never worry about missing requirements
- **Better Planning** - Know your schedule weeks in advance
- **Work-Life Balance** - Optimize your office days around your life

### For Managers

- **Compliance** - Ensure team meets attendance policies
- **Visibility** - Track team's office presence patterns
- **Data-Driven** - Make informed decisions about hybrid policies

### ROI

**Time saved per employee**: ~30 minutes/month
- Manual tracking: 15 min
- Planning: 10 min
- Last-minute corrections: 5 min

**For a 100-person team**: 50 hours/month saved

---

## 🏗️ Technical Overview

**Architecture**: Clean Architecture + MVVM  
**Language**: Kotlin  
**UI**: Jetpack Compose (Material 3)  
**Database**: Room (SQLite)  
**Dependency Injection**: Hilt  

**Key Technologies**:
- Geofencing API for automatic detection
- Coroutines & Flow for reactive data
- Room for local persistence
- Compose for modern, declarative UI

For detailed technical documentation, see [docs/](docs/).

---

## 📚 Documentation

| Document | Description |
|----------|-------------|
| [Architecture](docs/architecture/README.md) | System design, layers, patterns |
| [Business Logic](docs/business/README.md) | Calculations, algorithms, rules |
| [User Guide](docs/user-guide/README.md) | Features, screens, workflows |
| [Technical Guide](docs/technical/README.md) | Setup, build, deployment |
| [API Reference](docs/api/README.md) | Code documentation |

---

## 🎨 Screenshots

| Dashboard | Suggestions | Calendar |
|-----------|-------------|----------|
| Monthly progress overview | Smart day recommendations | Holiday & vacation management |

---

## 🤝 Contributing

Contributions welcome! See [CONTRIBUTING.md](CONTRIBUTING.md) for guidelines.

---

## 📄 License

Apache License 2.0 - see [LICENSE](LICENSE)

---

## 🙏 Credits

- Built with [Jetpack Compose](https://developer.android.com/jetpack/compose)
- Holiday data from [Nager.Date API](https://date.nager.at/)
- Inspired by the needs of hybrid work

---

**Made with ❤️ for hybrid workers everywhere**

## 🚀 Quick Start

### Prerequisites
- Android Studio Hedgehog (2023.1.1) or later
- JDK 11 or later
- Android SDK (API 26+, target 36)
- Android device/emulator with location services

### Installation

```bash
# Clone the repository
git clone https://github.com/yourusername/Go2Office.git
cd Go2Office

# Build and install
./gradlew installDebug

# Or open in Android Studio and run
```

### First Run Setup
1. Grant location permissions (including "Always Allow")
2. Set required days per week (e.g., 3 days)
3. Set hours per day (e.g., 8 hours)
4. Order weekday preferences (Monday > Tuesday > Wednesday...)
5. (Optional) Enable auto-detection and set office location

## 📱 Screenshots

| Dashboard | Auto-Detection Setup | Monthly Calendar |
|-----------|---------------------|------------------|
| ![Dashboard](docs/screenshots/dashboard.png) | ![Auto-Detection](docs/screenshots/auto-detection.png) | ![Calendar](docs/screenshots/calendar.png) |

## 📁 Project Structure

### Main Source (`app/src/main/java/com/example/go2office/`)

```
├── data/                          # Data Layer (Framework & Infrastructure)
│   ├── local/
│   │   ├── dao/                   # Room Database DAOs (Data Access Objects)
│   │   ├── entities/              # Room Database Entities
│   │   └── OfficeDatabase.kt      # Room Database configuration
│   ├── mapper/                    # Mappers between Entity ↔ Domain Model
│   ├── remote/                    # Remote data sources (Holiday API)
│   └── repository/                # Repository Implementations
│
├── domain/                        # Domain Layer (Business Logic - Framework Independent)
│   ├── model/                     # Domain Models (Business entities)
│   ├── repository/                # Repository Interfaces
│   └── usecase/                   # Use Cases (Business rules/interactors)
│       ├── GetMonthProgressUseCase.kt
│       ├── GetSuggestedOfficeDaysUseCase.kt
│       ├── CalculateMonthlyRequirementsUseCase.kt
│       └── AggregateSessionsToDailyUseCase.kt
│
├── presentation/                  # Presentation Layer (UI)
│   ├── components/                # Reusable Compose Components
│   ├── dashboard/                 # Dashboard Screen + ViewModel
│   ├── dayentry/                  # Day Entry Screen + ViewModel
│   ├── onboarding/                # Onboarding Screen + ViewModel
│   ├── settings/                  # Settings Screen + ViewModel
│   ├── autodetection/             # Auto-Detection Setup Screen
│   ├── calendar/                  # Annual Calendar Screen
│   ├── permissions/               # Permission Setup Screen
│   └── navigation/                # Navigation Graph & Routes
│
├── service/                       # Background Services
│   ├── GeofencingManager.kt      # Geofence setup and management
│   ├── GeofenceBroadcastReceiver.kt # Handles geofence events
│   └── OfficeNotificationHelper.kt  # Notifications
│
├── di/                            # Dependency Injection (Hilt Modules)
│   ├── AppModule.kt
│   ├── DatabaseModule.kt
│   └── RepositoryModule.kt
│
├── ui/theme/                      # Compose Theme (Colors, Typography, Theme)
└── util/                          # Utilities & Extensions
    ├── Constants.kt
    ├── DateUtils.kt
    └── WorkHoursCalculator.kt

MainActivity.kt                    # Main Activity (Navigation Host)
Go2OfficeApplication.kt           # Application class (Hilt entry point)
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
├── data/local/                    # Room Database Tests
├── presentation/                  # UI Tests (Compose)
└── HiltTestRunner.kt             # Custom test runner with Hilt
```

## 🏗️ Architecture

Go2Office follows **Clean Architecture** principles with **MVVM** pattern:

```
┌─────────────────────────────────────────────────────────┐
│                   Presentation Layer                     │
│  ┌────────────┐      ┌──────────────┐                  │
│  │  Compose   │ ───> │  ViewModel   │                  │
│  │  UI Screen │      │  (StateFlow) │                  │
│  └────────────┘      └──────────────┘                  │
└────────────────────────────┬────────────────────────────┘
                             │
                             │ Events & State
                             ▼
┌─────────────────────────────────────────────────────────┐
│                    Domain Layer                          │
│  ┌──────────────┐    ┌─────────────────┐               │
│  │   Use Cases  │ -> │ Domain Models   │               │
│  │  (Business   │    │  (Pure Kotlin)  │               │
│  │   Logic)     │    └─────────────────┘               │
│  └──────────────┘                                       │
└────────────────────────────┬────────────────────────────┘
                             │
                             │ Repository Interface
                             ▼
┌─────────────────────────────────────────────────────────┐
│                     Data Layer                           │
│  ┌────────────────┐   ┌──────────────┐                 │
│  │  Repository    │   │    Mappers   │                 │
│  │  Implementation│   │  Entity<->Model                 │
│  └────────────────┘   └──────────────┘                 │
│         │                                                │
│         ├──> Room Database (Local)                      │
│         ├──> Nager.Date API (Remote)                    │
│         └──> Location Services (System)                 │
└─────────────────────────────────────────────────────────┘
```

### Key Principles

- ✅ **Separation of Concerns** - Each layer has a single responsibility
- ✅ **Dependency Inversion** - Domain layer has no dependencies on framework
- ✅ **Unidirectional Data Flow** - Events go up, state flows down
- ✅ **Testability** - Business logic is pure and easily testable
- ✅ **SOLID Principles** - Clean, maintainable, extensible code

For detailed architecture documentation, see [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md)

## 🔧 Tech Stack

### Core
- **Language**: Kotlin 2.0.21
- **UI**: Jetpack Compose (Material 3)
- **Architecture**: MVVM + Clean Architecture
- **Min SDK**: 26 (Android 8.0)
- **Target SDK**: 36 (Android 14)

### Jetpack Libraries
- **Room** - Local database with type-safe queries
- **Hilt** - Dependency injection
- **Navigation Compose** - Type-safe navigation
- **ViewModel** - Lifecycle-aware state management
- **Coroutines & Flow** - Asynchronous programming
- **DataStore** - Key-value storage (preferences)

### Location & Background
- **Google Play Services Location** - Geofencing API
- **BroadcastReceiver** - Background geofence event handling
- **Notifications** - Office entry/exit alerts

### Testing
- **JUnit 4** - Unit testing framework
- **Robolectric** - Android unit tests
- **Compose Testing** - UI tests
- **Hilt Testing** - DI testing

### External APIs
- **Nager.Date API** - Free public holiday data (100+ countries)

## 📊 Business Logic

### Monthly Requirements Calculation

```kotlin
// Formula for required days per month
requiredDays = (weeklyRequirement × weekdaysInMonth) / 5
excludes = holidays + vacations + weekends
actualRequired = requiredDays - excludes.count()

// Formula for required hours per month
requiredHours = weeklyRequirement × hoursPerDay × (weekdaysInMonth / 5)
```

### Smart Day Suggestions Algorithm

The app suggests optimal office days based on:

1. **Weekday Preferences** - User's preferred day order (Mon > Tue > Wed...)
2. **Monthly Distribution** - Evenly spread across weeks
3. **Remaining Requirements** - Days/hours still needed
4. **Holidays & Vacations** - Excludes non-working days
5. **Current Progress** - Adapts to already completed days

```kotlin
// Pseudocode
fun suggestOfficeDays(month, remainingDays, preferences):
    availableDays = month.weekdays - holidays - vacations - completedDays
    weeksRemaining = countWeeksRemaining()
    
    for each week in remainingWeeks:
        daysForWeek = distributeEvenly(remainingDays, weeksRemaining)
        bestDays = selectByPreference(availableDays, preferences, daysForWeek)
        suggestions.add(bestDays)
    
    return suggestions.sortedChronologically()
```

### Hour Tracking Rules

- **Work Hours Window**: 7:00 AM - 7:00 PM only
- **Daily Maximum**: 10 hours (capped)
- **Session Aggregation**: Multiple entries/exits per day are combined
- **Automatic Calculation**: Entry time - Exit time (within window)

For detailed business logic documentation, see [docs/BUSINESS_LOGIC.md](docs/BUSINESS_LOGIC.md)

## 🔄 Data Flow

### Auto-Detection Flow

```
User arrives at office
    ↓
Geofence ENTER triggered
    ↓
GeofenceBroadcastReceiver.onReceive()
    ↓
OfficePresenceDao.insert(entryTime)
    ↓
Room Database persists
    ↓
[User works...]
    ↓
User leaves office
    ↓
Geofence EXIT triggered
    ↓
GeofenceBroadcastReceiver.onReceive()
    ↓
OfficePresenceDao.insert(exitTime)
    ↓
AggregateSessionsToDailyUseCase
    ↓
Calculate hours (respecting 7am-7pm, max 10h)
    ↓
DailyEntryDao.insert(date, hours)
    ↓
Dashboard observes changes
    ↓
UI updates automatically
```

For sequence diagrams, see [docs/SEQUENCE_DIAGRAMS.md](docs/SEQUENCE_DIAGRAMS.md)

## 📖 Documentation

- [Architecture Overview](docs/ARCHITECTURE.md) - Detailed architecture documentation
- [Business Logic](docs/BUSINESS_LOGIC.md) - Requirements calculation, algorithms
- [Sequence Diagrams](docs/SEQUENCE_DIAGRAMS.md) - Flow diagrams for key features
- [API Documentation](docs/API.md) - Use case APIs and data models
- [Testing Guide](docs/TESTING.md) - Unit and UI testing strategies

## 🛠️ Development

### Build Variants
```bash
# Debug build
./gradlew assembleDebug

# Release build
./gradlew assembleRelease

# Run tests
./gradlew test              # Unit tests
./gradlew connectedAndroidTest  # Instrumented tests
```

### Code Style
This project follows [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html) and [Android Kotlin Style Guide](https://developer.android.com/kotlin/style-guide).

## 🧪 Testing Strategy

### Unit Tests
- **ViewModels**: Test state changes, event handling
- **Use Cases**: Test business logic, calculations
- **Repositories**: Test data operations with fake DAOs
- **Utilities**: Test helper functions, formatters

### Instrumented Tests
- **Database**: Room DAO operations
- **UI**: Compose screen interactions
- **Navigation**: Screen transitions

### Test Coverage
- Domain Layer: ~80%+
- Data Layer: ~70%+
- Presentation Layer: ~60%+

## 🤝 Contributing

Contributions are welcome! Please follow these guidelines:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- [Nager.Date API](https://date.nager.at/) - Free public holiday API
- [Jetpack Compose](https://developer.android.com/jetpack/compose) - Modern UI toolkit
- [Material Design 3](https://m3.material.io/) - Design system

## 📮 Contact

For questions or support, please open an issue on GitHub.

---

**Made with ❤️ using Jetpack Compose**
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
