# 🤖 Auto-Detection Feature - Design Document

## Overview

The Auto-Detection feature allows Go2Office to **automatically detect** when you arrive at and leave the office, eliminating the need for manual entry. The app uses **location services** and **activity recognition** to intelligently track your office presence.

---

## 🎯 Key Capabilities

### What Gets Detected Automatically

1. **Office Arrival** 
   - Detects when you enter the office geofence
   - Records entry timestamp
   - Begins tracking duration

2. **Office Departure**
   - Detects when you leave the office geofence
   - Records exit timestamp
   - Calculates total hours worked

3. **Daily Summary**
   - Automatically aggregates all office sessions for the day
   - Updates daily entry with total hours
   - Marks day as "in office"

4. **Smart Detection**
   - Filters out brief visits (< 15 minutes by default)
   - Handles multiple entries per day (lunch breaks, meetings outside)
   - Provides confidence score for detections

---

## 🏗️ Architecture

### Components

```
┌─────────────────────────────────────────────────────┐
│                  USER'S DEVICE                      │
├─────────────────────────────────────────────────────┤
│                                                     │
│  ┌──────────────────────────────────────────────┐ │
│  │  Geofencing Service (Play Services)          │ │
│  │  - Monitors office location                  │ │
│  │  - Triggers on enter/exit                    │ │
│  └──────────────────────────────────────────────┘ │
│                        ↓                            │
│  ┌──────────────────────────────────────────────┐ │
│  │  Location Receiver (BroadcastReceiver)       │ │
│  │  - Receives geofence events                  │ │
│  │  - Validates location accuracy               │ │
│  └──────────────────────────────────────────────┘ │
│                        ↓                            │
│  ┌──────────────────────────────────────────────┐ │
│  │  Foreground Service                          │ │
│  │  - Processes detection events                │ │
│  │  - Updates database                          │ │
│  │  - Shows notification                        │ │
│  └──────────────────────────────────────────────┘ │
│                        ↓                            │
│  ┌──────────────────────────────────────────────┐ │
│  │  Room Database                               │ │
│  │  - office_locations table                    │ │
│  │  - office_presence table                     │ │
│  │  - daily_entries table (updated)             │ │
│  └──────────────────────────────────────────────┘ │
│                                                     │
└─────────────────────────────────────────────────────┘
```

---

## 📱 User Experience Flow

### Initial Setup

1. **Enable Auto-Detection** (Settings)
   ```
   Settings → Auto-Detection → Enable
   ```

2. **Set Office Location**
   ```
   - Option A: Use current location
   - Option B: Search for address
   - Option C: Pick on map
   ```

3. **Configure Geofence**
   ```
   - Radius: 50m to 500m (default: 100m)
   - Name: "Main Office", "Home Office", etc.
   ```

4. **Grant Permissions**
   ```
   - Location (Always/Background)
   - Activity Recognition
   - Notification
   ```

### Daily Usage

**Completely Automatic - No User Action Required!**

```
┌─────────────────────────────────────────────────┐
│  8:30 AM - Arrive at Office                     │
│  ✓ Detected entry                               │
│  📍 "You've arrived at Main Office"             │
└─────────────────────────────────────────────────┘
           ↓
┌─────────────────────────────────────────────────┐
│  12:00 PM - Leave for Lunch                     │
│  ✓ Detected exit (session paused)               │
│  ⏱️  Session 1: 3.5 hours (8:30 AM - 12:00 PM)  │
└─────────────────────────────────────────────────┘
           ↓
┌─────────────────────────────────────────────────┐
│  1:00 PM - Return from Lunch                    │
│  ✓ Detected re-entry (session resumed)          │
└─────────────────────────────────────────────────┘
           ↓
┌─────────────────────────────────────────────────┐
│  5:30 PM - Leave Office                         │
│  ✓ Detected exit (session ended)                │
│  ⏱️  Session 2: 4.5 hours (1:00 PM - 5:30 PM)   │
│  📊 "Total: 8.0 hours at office today"          │
│  ✅ Within work hours (7 AM - 7 PM)             │
│  ✅ Under 10-hour daily cap                     │
└─────────────────────────────────────────────────┘
```

**Dashboard automatically updates with:**
- Day marked as "in office"
- Total hours: 8.0 hours (calculated from arrival to departure)
- Only time between 7 AM - 7 PM counted
- Capped at 10 hours maximum per day
- Progress bars updated
- No manual entry needed!

---

## 🔒 Privacy & Battery

### Privacy Protections

1. **Local Only**
   - All location data stored locally on device
   - Never sent to cloud/server
   - Can be deleted anytime

2. **Minimal Tracking**
   - Only monitors configured office location
   - No continuous GPS tracking
   - No location history outside office

3. **User Control**
   - Can disable anytime
   - Can delete detection history
   - Can switch to manual mode

### Battery Optimization

1. **Geofencing (Efficient)**
   - Uses Google Play Services geofencing
   - Hardware-accelerated detection
   - Minimal battery impact (~1-2% per day)

2. **Smart Monitoring**
   - No continuous GPS polling
   - Event-driven (only activates on enter/exit)
   - Background optimization

3. **Configurable**
   - Adjust geofence radius (larger = less accurate, better battery)
   - Set quiet hours (disable at night)
   - Configure update frequency

---

## ⚙️ Settings & Configuration

### Auto-Detection Settings Screen

```
┌─────────────────────────────────────────────────┐
│  Auto-Detection                                 │
├─────────────────────────────────────────────────┤
│                                                 │
│  Enable Auto-Detection          [●─────] ON    │
│                                                 │
│  ─────────────────────────────────────────────  │
│                                                 │
│  Office Location                                │
│  📍 Main Office                                 │
│  123 Business St, City                          │
│  [Change Location]                              │
│                                                 │
│  Geofence Radius                                │
│  ●────────────── 100 meters                     │
│  50m ←                        → 500m            │
│                                                 │
│  ─────────────────────────────────────────────  │
│                                                 │
│  Work Hours Tracking                            │
│  ⏰ Counted: 7:00 AM - 7:00 PM                  │
│  📊 Daily Cap: 10 hours maximum                 │
│  ℹ️  Time outside this window is not counted    │
│                                                 │
│  Minimum Visit Duration                         │
│  15 minutes (filters brief visits)              │
│                                                 │
│  Auto-Mark Days                 [●─────] ON    │
│  Automatically mark detected days               │
│                                                 │
│  Notifications                  [●─────] ON    │
│  Show arrival/departure alerts                  │
│                                                 │
│  ─────────────────────────────────────────────  │
│                                                 │
│  Detection History                              │
│  📊 View all detections                         │
│  🗑️  Clear history                              │
│                                                 │
└─────────────────────────────────────────────────┘
```

---

## 🧮 Detection Algorithm

### Work Hours Tracking Rules

**Important Constraints:**

1. **Work Hours Window**: 7:00 AM - 7:00 PM
   - Only time within this window counts toward daily hours
   - Arrival before 7 AM → counted from 7 AM
   - Departure after 7 PM → counted until 7 PM
   - Arrival after 7 PM → no hours counted for that session

2. **Daily Cap**: 10 hours maximum
   - Even if you're at office for 12+ hours, only 10 hours count
   - Applies after summing all sessions for the day
   - Prevents unrealistic hour tracking

3. **Multiple Sessions**: Automatically handled
   - Example: 8 AM - 12 PM (4h) + 1 PM - 6 PM (5h) = 9 hours total
   - Lunch breaks and exits are tracked separately
   - All sessions within work hours are summed

**Examples:**

| Scenario | Arrival | Departure | Counted Hours | Explanation |
|----------|---------|-----------|---------------|-------------|
| Normal day | 9:00 AM | 5:00 PM | 8.0h | Full duration within window |
| Early bird | 6:30 AM | 3:00 PM | 8.0h | Counted from 7 AM to 3 PM |
| Night owl | 2:00 PM | 10:00 PM | 5.0h | Counted from 2 PM to 7 PM |
| Long day | 7:00 AM | 9:00 PM | 10.0h | Capped at 10h (7 AM - 7 PM = 12h) |
| Split shift | 8 AM-12 PM + 2 PM-6 PM | 8.0h | 4h + 4h = 8h total |
| Outside hours | 8:00 PM | 10:00 PM | 0.0h | Completely outside window |

### Entry Detection

```kotlin
fun onGeofenceEnter(location: Location, timestamp: LocalDateTime) {
    // 1. Validate location accuracy
    if (location.accuracy > 100f) return // Too inaccurate
    
    // 2. Check if already in office
    val currentSession = getCurrentActiveSession()
    if (currentSession != null) return // Already tracking
    
    // 3. Create new session
    val session = OfficePresence(
        entryTime = timestamp,
        isAutoDetected = true,
        confidence = calculateConfidence(location)
    )
    
    // 4. Save to database
    saveSession(session)
    
    // 5. Show notification
    showNotification("You've arrived at ${officeName}")
}
```

### Exit Detection

```kotlin
fun onGeofenceExit(location: Location, timestamp: LocalDateTime) {
    // 1. Get current session
    val session = getCurrentActiveSession() ?: return
    
    // 2. Calculate duration
    val durationMinutes = Duration.between(
        session.entryTime, 
        timestamp
    ).toMinutes()
    
    // 3. Validate minimum duration
    if (durationMinutes < minimumVisitMinutes) {
        // Too short - likely false positive
        deleteSession(session)
        return
    }
    
    // 4. Update session with exit time
    val updatedSession = session.copy(exitTime = timestamp)
    updateSession(updatedSession)
    
    // 5. Aggregate to daily entry
    aggregateToDailyEntry(session.entryTime.toLocalDate())
    
    // 6. Show notification
    showNotification(
        "Session ended: ${session.durationHours}h at office"
    )
}
```

### Daily Aggregation

```kotlin
fun aggregateToDailyEntry(date: LocalDate) {
    // 1. Get all sessions for the day
    val sessions = getSessionsForDate(date)
    
    // 2. Calculate total hours with work hours window (7 AM - 7 PM)
    val totalHours = sessions
        .filter { it.exitTime != null }
        .sumOf { calculateWorkHours(it) }
    
    // 3. Apply daily cap of 10 hours maximum
    val cappedHours = totalHours.coerceAtMost(10.0f)
    
    // 4. Update or create daily entry
    val entry = DailyEntry(
        date = date,
        wasInOffice = true,
        hoursWorked = cappedHours,
        notes = "Auto-detected (${sessions.size} sessions, ${totalHours}h recorded, ${cappedHours}h counted)"
    )
    
    saveDailyEntry(entry)
}

/**
 * Calculate work hours for a session, only counting time between 7 AM and 7 PM.
 */
fun calculateWorkHours(session: OfficePresence): Float {
    val exitTime = session.exitTime ?: LocalDateTime.now()
    
    // Define work hours window (7 AM - 7 PM)
    val workStartTime = LocalTime.of(7, 0)  // 7:00 AM
    val workEndTime = LocalTime.of(19, 0)   // 7:00 PM
    
    // Get entry and exit times
    var effectiveEntry = session.entryTime
    var effectiveExit = exitTime
    
    // Adjust entry time if before 7 AM
    if (effectiveEntry.toLocalTime().isBefore(workStartTime)) {
        effectiveEntry = LocalDateTime.of(effectiveEntry.toLocalDate(), workStartTime)
    }
    
    // Adjust exit time if after 7 PM
    if (effectiveExit.toLocalTime().isAfter(workEndTime)) {
        effectiveExit = LocalDateTime.of(effectiveExit.toLocalDate(), workEndTime)
    }
    
    // If entry is after 7 PM or exit is before 7 AM, no work hours counted
    if (effectiveEntry.toLocalTime().isAfter(workEndTime) || 
        effectiveExit.toLocalTime().isBefore(workStartTime)) {
        return 0f
    }
    
    // Calculate duration in hours
    val duration = Duration.between(effectiveEntry, effectiveExit)
    val hours = duration.toMinutes() / 60f
    
    return hours.coerceAtLeast(0f)
}
```

### Confidence Calculation

```kotlin
fun calculateConfidence(location: Location): Float {
    // Factors:
    // - Location accuracy (better accuracy = higher confidence)
    // - Time of day (work hours = higher confidence)
    // - Day of week (weekdays = higher confidence)
    // - Historical patterns (consistent behavior = higher)
    
    var confidence = 1.0f
    
    // Accuracy penalty
    if (location.accuracy > 50f) {
        confidence *= (100f / location.accuracy).coerceIn(0.5f, 1.0f)
    }
    
    // Time of day bonus
    val hour = LocalTime.now().hour
    if (hour in 8..18) { // Work hours
        confidence *= 1.1f
    }
    
    // Weekday bonus
    if (LocalDate.now().dayOfWeek in DayOfWeek.MONDAY..DayOfWeek.FRIDAY) {
        confidence *= 1.1f
    }
    
    return confidence.coerceIn(0.0f, 1.0f)
}
```

---

## 📊 Dashboard Integration

### New Dashboard Elements

**Auto-Detection Status Card**
```
┌─────────────────────────────────────────────────┐
│  🤖 Auto-Detection                              │
├─────────────────────────────────────────────────┤
│  Status: Active                                 │
│  Last detection: 2 hours ago                    │
│                                                 │
│  📍 Currently: At Main Office                   │
│  ⏱️  Since: 8:30 AM (2.5 hours)                 │
│                                                 │
│  [View Details] [End Session]                   │
└─────────────────────────────────────────────────┘
```

**Detection History (Day Entry Screen)**
```
┌─────────────────────────────────────────────────┐
│  February 14, 2026                              │
├─────────────────────────────────────────────────┤
│  Auto-Detected Sessions:                        │
│                                                 │
│  Session 1                                      │
│  8:30 AM - 12:00 PM (3.5h)                      │
│  Confidence: 98%                                │
│                                                 │
│  Session 2                                      │
│  1:00 PM - 5:30 PM (4.5h)                       │
│  Confidence: 95%                                │
│                                                 │
│  Total: 8.0 hours at office                     │
│                                                 │
│  [Mark as Incorrect] [Edit Hours]               │
└─────────────────────────────────────────────────┘
```

---

## 🛡️ Edge Cases & Handling

### 1. Multiple Office Locations
```kotlin
// Support multiple offices
val locations = listOf(
    OfficeLocation(lat1, lon1, 100f, "Main Office"),
    OfficeLocation(lat2, lon2, 150f, "Branch Office"),
    OfficeLocation(lat3, lon3, 50f, "Home Office")
)

// Track which location was detected
data class OfficePresence(
    // ...existing fields...
    val locationId: Long // Reference to which office
)
```

### 2. Overnight Stays (rare)
```kotlin
// If session exceeds 18 hours, split into days
if (session.durationHours > 18) {
    splitSessionAcrossDays(session)
}
```

### 3. GPS Drift/Inaccuracy
```kotlin
// Require consistent location for entry/exit
fun confirmLocation(location: Location): Boolean {
    val samples = collectLocationSamples(count = 3, intervalSeconds = 10)
    return samples.all { it.accuracy < 100f }
}
```

### 4. Battery Saver Mode
```kotlin
// Gracefully handle when geofencing is disabled
fun checkGeofencingAvailability(): DetectionStatus {
    return when {
        !hasLocationPermission() -> DetectionStatus.MISSING_PERMISSION
        isBatterySaverMode() -> DetectionStatus.LIMITED_BY_BATTERY
        !isPlayServicesAvailable() -> DetectionStatus.PLAY_SERVICES_UNAVAILABLE
        else -> DetectionStatus.ACTIVE
    }
}
```

### 5. Manual Override
```kotlin
// User can always manually edit auto-detected entries
fun allowManualOverride(date: LocalDate) {
    val entry = getDailyEntry(date)
    entry?.let {
        // Convert auto-detected to manual
        val manual = it.copy(
            notes = "${it.notes}\n[Manually edited by user]"
        )
        saveDailyEntry(manual)
    }
}
```

---

## 🔔 Notifications

### Notification Types

1. **Arrival Notification**
   ```
   📍 You've arrived at Main Office
   Tap to view today's progress
   ```

2. **Departure Notification**
   ```
   ✅ Session ended: 8.5 hours at office
   Your progress has been updated
   ```

3. **Reminder Notification** (if enabled)
   ```
   🏢 Haven't been to office today
   2 office days remaining this week
   ```

4. **Foreground Service Notification**
   ```
   🤖 Auto-Detection Active
   Monitoring office location
   [Stop Detection]
   ```

---

## ⚡ Implementation Checklist

### Phase 1: Core Detection (Priority: P0)
- [ ] Add location permissions to manifest
- [ ] Create geofencing service
- [ ] Implement entry/exit detection
- [ ] Create database tables (office_locations, office_presence)
- [ ] Build location settings screen
- [ ] Add foreground service for tracking

### Phase 2: Integration (Priority: P1)
- [ ] Aggregate sessions to daily entries
- [ ] Update dashboard with auto-detection status
- [ ] Show detection history on day entry screen
- [ ] Add manual override capability
- [ ] Implement notifications

### Phase 3: Polish (Priority: P2)
- [ ] Add confidence scoring
- [ ] Handle multiple office locations
- [ ] Implement detection history viewer
- [ ] Add quiet hours feature
- [ ] Battery optimization settings

### Phase 4: Advanced (Priority: P3)
- [ ] Activity recognition (sitting at desk)
- [ ] Wi-Fi SSID detection (backup method)
- [ ] Bluetooth beacon support
- [ ] ML-based pattern learning
- [ ] Export detection data

---

## 📋 Required Permissions

### AndroidManifest.xml

```xml
<!-- Location (required) -->
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_BACKGROUND_LOCATION" />

<!-- Foreground Service (required for background detection) -->
<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE_LOCATION" />

<!-- Notifications (required for alerts) -->
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />

<!-- Activity Recognition (optional, for enhanced detection) -->
<uses-permission android:name="android.permission.ACTIVITY_RECOGNITION" />

<!-- Boot (optional, to restart detection after reboot) -->
<uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />
```

---

## 🎓 Benefits of Auto-Detection

### For Users
✅ **Zero Effort** - Completely hands-free  
✅ **Accurate** - No forgetting to log days  
✅ **Detailed** - Precise hour tracking  
✅ **Convenient** - Works in background  
✅ **Private** - All data stays on device  

### For Compliance
✅ **Reliable** - Consistent tracking  
✅ **Auditable** - Complete history with timestamps  
✅ **Transparent** - Shows confidence scores  
✅ **Verifiable** - Location-based proof  

---

## 🔮 Future Enhancements

1. **Smart Scheduling**
   - Learn your typical schedule
   - Predict office days
   - Suggest adjustments

2. **Multi-Location Support**
   - Track multiple office locations
   - Co-working spaces
   - Client sites

3. **Advanced Recognition**
   - Wi-Fi SSID detection
   - Bluetooth beacons
   - NFC badges

4. **Integration**
   - Calendar integration
   - Outlook/Google Calendar sync
   - Corporate badge systems

---

## 📖 Related Documentation

- **TICKETS.md** - See Ticket #47 (Auto-Detection Feature)
- **ARCHITECTURE.md** - Location service architecture
- **PRIVACY.md** - Privacy policy for location data

---

*This feature transforms Go2Office from a manual tracker to an intelligent assistant that knows when you're at the office—without you having to think about it!* 🚀

