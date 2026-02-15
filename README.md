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

#### Option 1: Download from GitHub Releases (Easiest)

1. Go to [**Releases**](https://github.com/abaiao-r/Go2Office/releases)
2. Download the latest `Go2Office-x.x.x.apk`
3. On your phone, enable "Install from unknown sources" in Settings
4. Open the APK file to install

#### Option 2: Build from Source

```bash
git clone https://github.com/abaiao-r/Go2Office.git
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

- **Save Time** - No manual tracking or calculations
- **Reduce Stress** - Never worry about missing requirements
- **Better Planning** - Know your schedule weeks in advance
- **Work-Life Balance** - Optimize your office days around your life

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

---

## 📚 Documentation

| Document | Description |
|----------|-------------|
| [Architecture](docs/architecture) | System design, layers, patterns |
| [Business Logic](docs/business) | Calculations, algorithms, rules |
| [User Guide](docs/user-guide) | Features, screens, workflows |
| [Technical Guide](docs/technical) | Setup, build, deployment |
| [API Reference](docs/api) | Code documentation |

---

## Author

**André Francisco** - [GitHub](https://github.com/abaiao-r)

---

## 📄 License

MIT License - see [LICENSE](LICENSE)

---