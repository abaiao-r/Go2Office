# Technical Guide

Build, test, and deploy Go2Office.

## Requirements

| Requirement | Version |
|-------------|---------|
| Android Studio | Hedgehog+ |
| JDK | 17+ |
| Android SDK | 26+ (target 35) |
| Kotlin | 2.0+ |

## Build

```bash
# Clone
git clone https://github.com/abaiao-r/Go2Office.git
cd Go2Office

# Build debug
./gradlew assembleDebug

# Install on device
./gradlew installDebug
```

## Project Structure

```
app/src/main/java/com/example/go2office/
├── data/
│   ├── local/          # Room database, DAOs, entities
│   ├── mapper/         # Entity ↔ Domain mappers
│   ├── remote/         # Holiday API service
│   └── repository/     # Repository implementation
├── di/                 # Hilt modules
├── domain/
│   ├── model/          # Domain models
│   ├── repository/     # Repository interface
│   └── usecase/        # Business logic
├── presentation/
│   ├── dashboard/      # Main screen
│   ├── settings/       # Settings screen
│   ├── onboarding/     # First-run setup
│   ├── calendar/       # Annual calendar
│   ├── dayentry/       # Manual hour entry
│   ├── autodetection/  # Auto-detection settings
│   ├── components/     # Reusable UI components
│   └── navigation/     # Navigation graph
├── service/            # Geofencing, notifications
└── util/               # Constants, date utils
```

## Testing

```bash
# Unit tests
./gradlew test

# Instrumented tests
./gradlew connectedAndroidTest
```

## Key Dependencies

| Library | Purpose |
|---------|---------|
| Jetpack Compose | UI |
| Hilt | Dependency injection |
| Room | Local database |
| Coroutines/Flow | Async & reactive |
| Play Services Location | Geofencing |

## Permissions

| Permission | Purpose |
|------------|---------|
| ACCESS_FINE_LOCATION | Geofencing |
| ACCESS_BACKGROUND_LOCATION | Background tracking |
| POST_NOTIFICATIONS | Arrival/departure alerts |

## Release Build

```bash
./gradlew assembleRelease
```

APK location: `app/build/outputs/apk/release/`

