# Go2Office

Android app to track and plan required office days per month.

## Why This App?

Many companies require employees to be in the office a certain number of days per week (e.g., 3 out of 5). Tracking this manually is tedious. Go2Office:

- **Calculates** monthly requirements automatically
- **Suggests** optimal office days based on your preferences
- **Tracks** hours automatically via geofencing (optional)
- **Adjusts** for holidays and vacations

## Quick Start

```bash
# Clone and build
git clone https://github.com/yourusername/Go2Office.git
cd Go2Office
./gradlew installDebug
```

## Setup (30 seconds)

1. Set requirements: "3 days/week, 8 hours/day"
2. Order preferred weekdays: Mon > Tue > Wed > Thu > Fri
3. (Optional) Enable auto-detection and set office location
4. Done!

## How It Works

### Monthly Calculation
```
Required days = ceil((days per week × weekdays in month) / 5)
Required hours = required days × hours per day

Example: 3 days/week in March (22 weekdays)
= ceil((3 × 22) / 5) = 14 days
= 14 × 8 = 112 hours
```

### Smart Suggestions
- Distributes days evenly across weeks
- Respects your weekday preferences  
- Avoids holidays and vacations
- Updates as you complete days

### Auto-Tracking (Optional)
- Detects office entry/exit via geofence
- Counts hours between 7 AM - 7 PM only
- Max 10 hours per day
- Works in background

## Tech Stack

- **UI**: Jetpack Compose + Material 3
- **Architecture**: MVVM + Clean Architecture
- **DI**: Hilt
- **Database**: Room
- **Background**: WorkManager + Geofencing API
- **API**: Nager.Date (free public holidays)

## Documentation

- [Architecture](docs/ARCHITECTURE.md) - System design and patterns
- [Business Logic](docs/BUSINESS.md) - Calculations and algorithms
- [Flows](docs/FLOWS.md) - Key sequence diagrams

## License

MIT License - See [LICENSE](LICENSE) file

