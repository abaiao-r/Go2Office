# 🔄 Sequence Diagrams

This document contains detailed sequence diagrams for key user flows and system interactions.

---

## Table of Contents

1. [Auto-Detection Flow](#auto-detection-flow)
2. [Manual Day Entry](#manual-day-entry)
3. [Monthly Dashboard Load](#monthly-dashboard-load)
4. [Holiday Management](#holiday-management)
5. [Settings Update](#settings-update)

---

## Auto-Detection Flow

### Complete Office Visit Cycle

```plantuml
@startuml auto-detection-complete
!theme plain
title Auto-Detection: Complete Office Visit

actor User
participant "Android\nSystem" as System
participant "Geofence\nBroadcast\nReceiver" as Receiver
participant "Presence\nDAO" as DAO
participant "Room\nDatabase" as DB
participant "Notification\nHelper" as Notif
participant "Aggregate\nUseCase" as Aggregate

== User Arrives at Office ==

User -> System : Enters geofence area
activate System
System -> System : Detect ENTER event
System -> Receiver : onReceive(ENTER)
activate Receiver

Receiver -> DAO : insert(PresenceEntity\nwith entryTime)
activate DAO
DAO -> DB : INSERT entry
activate DB
DB --> DAO : Success
deactivate DB
DAO --> Receiver : Saved
deactivate DAO

Receiver -> Notif : showNotification\n("Arrived at office")
activate Notif
Notif --> User : 🔔 "Arrived at office\n8:30 AM"
deactivate Notif

Receiver --> System : Complete
deactivate Receiver
deactivate System

== User Works (App Sleeps) ==

User -> User : Works for several hours
note right: No app activity\nNo battery drain

== User Leaves Office ==

User -> System : Exits geofence area
activate System
System -> System : Detect EXIT event
System -> Receiver : onReceive(EXIT)
activate Receiver

Receiver -> DAO : insert(PresenceEntity\nwith exitTime)
activate DAO
DAO -> DB : INSERT exit
activate DB
DB --> DAO : Success
deactivate DB
DAO --> Receiver : Saved
deactivate DAO

Receiver -> Aggregate : aggregateSessions(date)
activate Aggregate
Aggregate -> DAO : getSessionsForDate(date)
activate DAO
DAO -> DB : SELECT * WHERE date
DB --> DAO : [entry, exit]
DAO --> Aggregate : List<PresenceEntity>
deactivate DAO

Aggregate -> Aggregate : Calculate hours\n(7 AM - 7 PM window)
Aggregate -> Aggregate : Apply 10h daily cap

Aggregate -> DAO : saveDailyEntry(date, hours)
activate DAO
DAO -> DB : INSERT daily_entry
DB --> DAO : Success
deactivate DAO
Aggregate --> Receiver : Hours: 9.25
deactivate Aggregate

Receiver -> Notif : showNotification\n("Left office - 9.2h")
activate Notif
Notif --> User : 🔔 "Left office\n9.2 hours worked"
deactivate Notif

Receiver --> System : Complete
deactivate Receiver
deactivate System

== Next Day Dashboard Check ==

User -> User : Opens app
note right: Dashboard shows\nyesterday's 9.2h\nautomatically

@enduml
```

---

## Manual Day Entry

### User Manually Logs Office Day

```plantuml
@startuml manual-entry
!theme plain
title Manual Day Entry Flow

actor User
participant "Day Entry\nScreen" as Screen
participant "Day Entry\nViewModel" as VM
participant "Update Daily\nHours UseCase" as UC
participant "Repository" as Repo
participant "Room\nDatabase" as DB

User -> Screen : Click day on Dashboard
activate Screen
Screen -> VM : Load entry for date
activate VM
VM -> Repo : getDailyEntry(date)
activate Repo
Repo -> DB : Query by date
DB --> Repo : DailyEntry or null
Repo --> VM : Domain model
deactivate Repo
VM --> Screen : Update state\n(existing hours)
deactivate VM
Screen --> User : Show form
deactivate Screen

User -> Screen : Enter hours: 8.5
activate Screen
User -> Screen : Tap "Save"
Screen -> VM : onEvent(SaveHours(8.5))
activate VM

VM -> VM : Validate input\n(0-10 hours)

VM -> UC : execute(date, 8.5)
activate UC
UC -> Repo : saveDailyEntry\n(date, 8.5, true)
activate Repo
Repo -> DB : INSERT OR REPLACE
activate DB
DB --> Repo : Success
deactivate DB
Repo --> UC : Result.success
deactivate Repo
UC --> VM : Success
deactivate UC

VM -> VM : Update state\n(saved = true)
VM --> Screen : State: Success
deactivate VM

Screen --> User : Show "Saved ✓"\nNavigate back
deactivate Screen

== Dashboard Auto-Updates ==

note over Screen
Dashboard observes
Flow<DailyEntry>
and automatically
shows new hours
end note

@enduml
```

---

## Monthly Dashboard Load

### Dashboard Screen Initialization

```plantuml
@startuml dashboard-load
!theme plain
title Dashboard: Load Monthly Progress

actor User
participant "Dashboard\nScreen" as Screen
participant "Dashboard\nViewModel" as VM
participant "Get Month\nProgress UC" as ProgressUC
participant "Get Suggested\nDays UC" as SuggestUC
participant "Repository" as Repo
participant "Room\nDatabase" as DB

User -> Screen : Open app
activate Screen
Screen -> VM : Observe uiState
activate VM

VM -> VM : init() {\n  loadDashboardData()\n}

VM -> ProgressUC : invoke(currentMonth)
activate ProgressUC

ProgressUC -> Repo : getSettings()
activate Repo
Repo -> DB : SELECT settings
DB --> Repo : SettingsEntity
Repo --> ProgressUC : OfficeSettings\n(3 days/week, 8h/day)
deactivate Repo

ProgressUC -> ProgressUC : Calculate requirements\nfor month

ProgressUC -> Repo : getDailyEntries(month)
activate Repo
Repo -> DB : SELECT WHERE month
DB --> Repo : List<DailyEntryEntity>
Repo --> ProgressUC : List<DailyEntry>
deactivate Repo

ProgressUC -> ProgressUC : Sum completed\ndays and hours

ProgressUC --> VM : MonthProgress\n(required: 14d/112h,\ncompleted: 6d/48h)
deactivate ProgressUC

VM -> SuggestUC : invoke(currentMonth)
activate SuggestUC

SuggestUC -> Repo : getHolidays(month)
activate Repo
Repo -> DB : SELECT holidays
DB --> Repo : List<Holiday>
Repo --> SuggestUC : Holidays
deactivate Repo

SuggestUC -> Repo : getSettings()
activate Repo
Repo -> DB : SELECT settings
DB --> Repo : Preferences
Repo --> SuggestUC : WeekdayPreferences
deactivate Repo

SuggestUC -> SuggestUC : Run suggestion\nalgorithm

SuggestUC --> VM : List<SuggestedDay>\n[Mon 9, Tue 10, Mon 16...]
deactivate SuggestUC

VM -> VM : Update state:\n- monthProgress\n- suggestedDays\n- isLoading = false

VM --> Screen : StateFlow emits\nnew state
deactivate VM

Screen -> Screen : Compose recomposes

Screen --> User : Display:\n- Progress: 6/14 days\n- Next: Mon Mar 9
deactivate Screen

@enduml
```

---

## Holiday Management

### Load Country Holidays

```plantuml
@startuml load-holidays
!theme plain
title Load Country Holidays from API

actor User
participant "Calendar\nScreen" as Screen
participant "Calendar\nViewModel" as VM
participant "Add Holiday\nUseCase" as AddUC
participant "Repository" as Repo
participant "Holiday\nAPI Service" as API
participant "Room\nDatabase" as DB

User -> Screen : Tap "Load Country"
activate Screen
Screen --> User : Show country picker
User -> Screen : Select "Portugal"
Screen -> VM : onEvent(LoadCountry("PT"))
activate VM

VM -> VM : Update state\n(loading = true)
VM --> Screen : Show loading
Screen --> User : 🔄 Loading...

VM -> Repo : fetchPublicHolidays("PT", 2026)
activate Repo

Repo -> API : GET /PublicHolidays/2026/PT
activate API
API --> Repo : JSON response\n[{date, name}...]
deactivate API

Repo -> Repo : Map DTOs to\nHolidayEntity

loop For each holiday
    Repo -> DB : INSERT holiday
    activate DB
    DB --> Repo : Saved
    deactivate DB
end

Repo --> VM : Result.success\n(10 holidays loaded)
deactivate Repo

VM -> VM : Update state:\n- loading = false\n- success message

VM --> Screen : State updated
deactivate VM

Screen -> Screen : Recompose calendar

Screen --> User : Show holidays\nmarked with 🎉
deactivate Screen

note over User
Calendar now shows:
- 1 Jan: New Year
- 25 Apr: Freedom Day
- 10 Jun: Portugal Day
etc.
end note

@enduml
```

---

## Settings Update

### Change Requirements Mid-Month

```plantuml
@startuml settings-update
!theme plain
title Update Settings and Recalculate

actor User
participant "Settings\nScreen" as Screen
participant "Settings\nViewModel" as VM
participant "Save Settings\nUseCase" as SaveUC
participant "Repository" as Repo
participant "Room\nDatabase" as DB
participant "Dashboard\nViewModel" as DashVM

User -> Screen : Open Settings
activate Screen
Screen -> VM : Load current settings
activate VM
VM -> Repo : getSettings()
activate Repo
Repo -> DB : SELECT settings
DB --> Repo : Current settings
Repo --> VM : OfficeSettings\n(3 days/week)
deactivate Repo
VM --> Screen : Display current
Screen --> User : Show: 3 days/week

User -> Screen : Change to 4 days/week
User -> Screen : Tap "Save"
Screen -> VM : onEvent(UpdateSettings\n(4 days/week))

VM -> VM : Validate:\n- Days 1-5 ✓\n- Hours 1-12 ✓

VM -> SaveUC : execute(newSettings)
activate SaveUC
SaveUC -> Repo : saveSettings\n(4 days/week)
activate Repo
Repo -> DB : UPDATE settings
activate DB
DB --> Repo : Updated
deactivate DB
Repo --> SaveUC : Success
deactivate Repo
SaveUC --> VM : Result.success
deactivate SaveUC

VM -> VM : Update state\n(saved = true)
VM --> Screen : Show "Saved ✓"
deactivate VM
Screen --> User : Settings updated

== Dashboard Auto-Recalculates ==

DB -> Repo : Settings changed\n(Flow emits)
activate Repo
Repo -> DashVM : Flow<Settings>
activate DashVM

DashVM -> DashVM : observeSettings() {\n  loadDashboardData()\n}

note over DashVM
Dashboard ViewModel
observes settings Flow
and reloads data when
settings change
end note

DashVM -> DashVM : Recalculate:\n- Requirements\n- Suggestions

DashVM --> Repo : New state ready
deactivate DashVM
deactivate Repo

note over User
Next time user opens
Dashboard, new requirements
are automatically shown
end note

deactivate Screen

@enduml
```

---

## Background Processing

### Geofence Event Handling

```plantuml
@startuml geofence-processing
!theme plain
title Background Geofence Processing

participant "Android\nSystem" as System
participant "Geofence\nBroadcast\nReceiver" as Receiver
participant "Hilt" as Hilt
participant "Coroutine\nScope" as Scope
participant "DAOs" as DAO
participant "Database" as DB

System -> Receiver : onReceive(Intent)
activate Receiver

Receiver -> Receiver : Parse GeofencingEvent

alt Geofence ENTER
    Receiver -> Hilt : Inject dependencies
    activate Hilt
    Hilt --> Receiver : OfficePresenceDao,\nNotificationHelper
    deactivate Hilt
    
    Receiver -> Scope : launch(Dispatchers.IO)
    activate Scope
    
    Scope -> DAO : insert(PresenceEntity(\n  entryTime = now\n))
    activate DAO
    DAO -> DB : INSERT
    DB --> DAO : Success
    deactivate DAO
    
    Scope -> Receiver : Show notification
    deactivate Scope
    
else Geofence EXIT
    Receiver -> Scope : launch(Dispatchers.IO)
    activate Scope
    
    Scope -> DAO : insert(PresenceEntity(\n  exitTime = now\n))
    activate DAO
    DAO -> DB : INSERT
    DB --> DAO : Success
    deactivate DAO
    
    Scope -> DAO : Get today's sessions
    DAO -> DB : SELECT WHERE date
    DB --> DAO : Sessions
    
    Scope -> Scope : Calculate total hours
    
    Scope -> DAO : Save daily entry
    DAO -> DB : INSERT daily_entry
    DB --> DAO : Success
    
    Scope -> Receiver : Show notification\nwith hours
    deactivate Scope
end

Receiver --> System : Complete\n(receiver exits)
deactivate Receiver

note right of Receiver
Receiver completes in <1s
App goes back to sleep
Battery impact: minimal
end note

@enduml
```

---

## Error Handling

### Failed API Call Recovery

```plantuml
@startuml error-handling
!theme plain
title Error Handling: Holiday API Failure

actor User
participant "Calendar\nScreen" as Screen
participant "Repository" as Repo
participant "Holiday\nAPI" as API
participant "Cache" as Cache

User -> Screen : Load holidays
Screen -> Repo : fetchHolidays()
activate Repo

Repo -> API : GET /holidays
activate API
API --> Repo : ❌ Network Error
deactivate API

Repo -> Repo : Catch exception

Repo -> Cache : Check local cache
activate Cache
Cache --> Repo : Cached data (1 day old)
deactivate Cache

alt Cache exists and valid
    Repo --> Screen : Result.success\n(from cache)
    note right: Fallback to\ncached data
else Cache invalid or missing
    Repo --> Screen : Result.failure\n(NetworkError)
    Screen --> User : Show error:\n"No internet.\nTry again later."
    Screen --> User : Offer retry button
end

deactivate Repo

@enduml
```

---

**See Also**:
- [Architecture](../architecture/README.md)
- [Business Logic](../business/README.md)
- [Use Cases](../api/README.md)

