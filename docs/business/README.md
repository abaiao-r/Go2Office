# 📊 Business Logic Documentation

> **📊 Diagrams**: All diagrams are PNG images generated from PlantUML. See [DIAGRAM_INDEX](../diagrams/DIAGRAM_INDEX.md) for the complete catalog.

## Overview

This document explains the core business rules, calculations, and algorithms that power Go2Office.

---

## Monthly Requirements

### Required Days Calculation

**Business Rule**: User specifies days per week (e.g., 3 of 5). App calculates monthly requirement proportionally.

**Formula**:
```
requiredDays = ceil((daysPerWeek × weekdaysInMonth) / 5)
```

**Flowchart**:

![Requirements Calculation](../diagrams/images/requirements-calculation.png)

*Example: 3 days/week in March (22 weekdays) = 14 days required*

<details>
<summary>📄 View PlantUML source</summary>

```plantuml
@startuml requirements-calculation
!theme plain

start
:User sets\ndays per week;
:Count weekdays\nin month;
:Calculate:\nrequired = (days/week × weekdays) / 5;
:Round up\n(ceiling);
:Subtract holidays\nand vacations;
:Display\nfinal requirement;
stop

note right
  Example:
  3 days/week
  March: 22 weekdays
  = ceil((3 × 22) / 5)
  = ceil(13.2)
  = 14 days required
end note

@enduml
```

</details>

**Example Calculations**:

| Month | Weekdays | 3 days/week | 4 days/week | 5 days/week |
|-------|----------|-------------|-------------|-------------|
| Jan 2026 | 22 | 14 days | 18 days | 22 days |
| Feb 2026 | 20 | 12 days | 16 days | 20 days |
| Mar 2026 | 22 | 14 days | 18 days | 22 days |

---

### Required Hours Calculation

**Business Rule**: Hours per day × required days

**Formula**:
```
requiredHours = daysPerWeek × hoursPerDay × weekdaysInMonth / 5
```

**Example**:
```
Settings: 3 days/week, 8 hours/day
March 2026: 22 weekdays

requiredHours = (3 × 8 × 22) / 5
              = 528 / 5
              = 105.6 hours
```

---

## Smart Suggestions Algorithm

### Goal
Suggest optimal office days that:
1. Respect user's weekday preferences
2. Distribute evenly across weeks
3. Avoid holidays and vacations
4. Adapt to current progress

### Algorithm Flow

![Suggestion Algorithm](../diagrams/images/suggestion-algorithm.png)

*Algorithm distributes days evenly across weeks based on user preferences*

<details>
<summary>📄 View PlantUML source</summary>

```plantuml
@startuml suggestion-algorithm
!theme plain

start
:Get remaining\ndays needed;
:Get available\nweekdays;
:Filter out:\n- Holidays\n- Vacations\n- Completed days\n- Past dates;
:Group by\nweek number;

repeat
    :Calculate days\nfor this week;
    note right
        daysForWeek = ceil(remaining / weeksLeft)
    end note
    
    :Sort available days\nby preference;
    :Take top N days\nfor this week;
    :Add to suggestions;
    :Decrement remaining;
    
repeat while (remaining > 0\nAND weeks left?)

:Sort suggestions\nchronologically;
:Return suggested days;
stop

@enduml
```

</details>

### Example Scenario

**Setup**:
- Required: 12 days for March
- Completed: 4 days
- Remaining: 8 days
- Preferences: Mon > Tue > Wed > Thu > Fri
- Holiday: March 17 (Tuesday)

**Week-by-Week Distribution**:

```plantuml
@startuml weekly-distribution
!theme plain

object "Week 1 (Mar 2-8)" as W1 {
    Available: Mon 2, Tue 3, Wed 4, Thu 5 (completed), Fri 6
    Remaining: 8 days
    Weeks left: 4
    For this week: ceil(8/4) = 2
    Suggested: Mon 2, Tue 3
}

object "Week 2 (Mar 9-15)" as W2 {
    Available: Mon 9, Tue 10 (completed), Wed 11, Thu 12, Fri 13
    Remaining: 6 days
    Weeks left: 3
    For this week: ceil(6/3) = 2
    Suggested: Mon 9, Wed 11
}

object "Week 3 (Mar 16-22)" as W3 {
    Available: Mon 16, Wed 18, Thu 19, Fri 20
    (Tue 17 is holiday)
    Remaining: 4 days
    Weeks left: 2
    For this week: ceil(4/2) = 2
    Suggested: Mon 16, Wed 18
}

object "Week 4 (Mar 23-29)" as W4 {
    Available: Mon 23, Tue 24, Wed 25, Thu 26, Fri 27
    Remaining: 2 days
    Weeks left: 1
    For this week: 2
    Suggested: Mon 23, Tue 24
}

W1 -[hidden]down-> W2
W2 -[hidden]down-> W3
W3 -[hidden]down-> W4

@enduml
```

**Result**: 8 days suggested, evenly distributed, respecting Monday preference

---

## Hour Tracking Rules

### Work Hours Window

**Business Rule**: Only hours between 7 AM and 7 PM count

![Work Hours Window](../diagrams/images/work-hours-window.png)

*Hour tracking rules with 7am-7pm window and 10h daily cap*

<details>
<summary>📄 View PlantUML source</summary>

```plantuml
@startuml work-hours-window
!theme plain

start
:Office entry\ndetected;

if (Entry before 7 AM?) then (yes)
    :Clamp to 7:00 AM;
else (no)
    :Use actual entry time;
endif

if (Entry after 7 PM?) then (yes)
    :Count 0 hours;
    stop
endif

:User works...;

:Office exit\ndetected;

if (Exit after 7 PM?) then (yes)
    :Clamp to 7:00 PM;
else (no)
    :Use actual exit time;
endif

:Calculate duration\nbetween entry and exit;

if (Duration > 10 hours?) then (yes)
    :Cap at 10 hours;
else (no)
    :Use actual duration;
endif

:Record hours\nfor the day;
stop

@enduml
```

</details>

### Examples

| Entry | Exit | Clamped Entry | Clamped Exit | Hours |
|-------|------|---------------|--------------|-------|
| 8:30 AM | 5:45 PM | 8:30 AM | 5:45 PM | 9.25h ✅ |
| 6:00 AM | 3:00 PM | 7:00 AM | 3:00 PM | 8.0h ✅ |
| 9:00 AM | 9:00 PM | 9:00 AM | 7:00 PM | 10.0h ✅ |
| 8:00 PM | 10:00 PM | - | - | 0.0h ❌ |
| 7:00 AM | 8:00 PM | 7:00 AM | 7:00 PM | 10.0h (capped) |

---

## Session Aggregation

**Business Rule**: Multiple office visits in one day are combined

![Session Aggregation](../diagrams/images/session-aggregation.png)

*Multiple sessions aggregated into single daily entry with 10h cap*

<details>
<summary>📄 View PlantUML source</summary>

```plantuml
@startuml session-aggregation
!theme plain

start
:Get all presence\nrecords for date;

partition "Pair Entry/Exit" {
    :Sort by timestamp;
    :Match entries\nwith exits;
}

partition "Calculate Each Session" {
    repeat
        :Get session\nentry and exit;
        :Apply work hours\nwindow (7 AM - 7 PM);
        :Calculate\nsession hours;
        :Add to total;
    repeat while (more sessions?)
}

partition "Apply Daily Cap" {
    if (Total > 10 hours?) then (yes)
        :Cap at 10 hours;
    else (no)
        :Keep actual total;
    endif
}

:Save aggregated\ndaily entry;
stop

note right
  Example:
  Session 1: 8:30 AM - 12:00 PM = 3.5h
  Session 2: 1:00 PM - 2:00 PM = 1.0h
  Session 3: 2:30 PM - 6:00 PM = 3.5h
  Total: 8.0 hours
end note

@enduml
```

</details>

---

## Holiday & Vacation Handling

### Exclusion Rules

![Exclusion Rules](../diagrams/images/exclusion-rules.png)

*Decision flowchart for including/excluding days from requirements*

<details>
<summary>📄 View PlantUML source</summary>

```plantuml
@startuml exclusion-rules
!theme plain

start
:Check date;

if (Is Saturday\nor Sunday?) then (yes)
    :Mark as\nweekend;
    :Exclude from\nrequirements;
    stop
endif

if (Is public\nholiday?) then (yes)
    :Mark as\nholiday;
    :Exclude from\nrequirements;
    stop
endif

if (Is vacation\nday?) then (yes)
    :Mark as\nvacation;
    :Exclude from\nrequirements;
    stop
endif

:Mark as\nworking day;
:Include in\nrequirements;
stop

@enduml
```

</details>

### Impact on Requirements

![Holiday Impact](../diagrams/images/holiday-impact.png)

*How holidays and vacations reduce monthly requirements*

<details>
<summary>📄 View PlantUML source</summary>

```plantuml
@startuml holiday-impact
!theme plain

object "Base Calculation" as Base {
    March 2026: 31 days
    Weekdays: 22
    Requirement (3 days/week): 14 days
}

object "With 2 Public Holidays" as WithHolidays {
    Working weekdays: 20
    Adjusted requirement: 12 days
    Reduction: 2 days
}

object "With 3 Vacation Days" as WithVacation {
    Working weekdays: 17
    Adjusted requirement: 11 days
    Reduction: 3 days
}

Base -down-> WithHolidays : Apply\nholidays
WithHolidays -down-> WithVacation : Apply\nvacations

@enduml
```

</details>

---

## Progress Tracking

### Completion Calculation

![Progress Tracking](../diagrams/images/progress-tracking.png)

*Progress calculation algorithm with status indicators*

<details>
<summary>📄 View PlantUML source</summary>

```plantuml
@startuml progress-tracking
!theme plain

start
:Get required\ndays & hours;
:Get completed\ndays & hours;

:Calculate\ndays progress:;
note right
    daysProgress = (completed / required) × 100
end note

:Calculate\nhours progress:;
note right
    hoursProgress = (completed / required) × 100
end note

:Overall progress =\nmin(days%, hours%);

if (Days >= required\nAND hours >= required?) then (yes)
    :Status: COMPLETE ✅;
    stop
else (no)
    :Calculate days\nremaining;
    :Calculate hours\nremaining;
endif

if (Progress >= 80%?) then (yes)
    :Status: ON TRACK 🟢;
else if (Progress >= 50%?) then (yes)
    :Status: BEHIND 🟡;
else (no)
    :Status: CRITICAL 🔴;
endif

stop

@enduml
```

</details>

---

## Edge Cases

### Case 1: Insufficient Remaining Days

![Insufficient Days](../diagrams/images/insufficient-days.png)

*Handling scenario when not enough days remain to meet requirement*

<details>
<summary>📄 View PlantUML source</summary>

```plantuml
@startuml insufficient-days
!theme plain

start
:User has 7 days\nremaining needed;
:Only 4 workdays\nleft in month;

if (Remaining < Needed?) then (yes)
    :Show warning:\n"Impossible to meet\nrequirement";
    :Suggest all\nremaining days;
    :Mark status\nas CRITICAL;
else (no)
    :Normal suggestions;
endif

stop

@enduml
```

</details>

### Case 2: Mid-Month Settings Change

**Scenario**: User changes from 3 to 4 days/week on March 15

**Handling**:
1. Keep existing completed days
2. Recalculate requirement for remaining month
3. Adjust suggestions accordingly

```
Before change (Mar 1-15):
- Required: 7 days (first half)
- Completed: 6 days

After change (Mar 15-31):
- Remaining weekdays: 11
- New requirement: 4 days/week
- Additional needed: ceil((4 × 11) / 5) = 9 days

Total requirement: 6 (done) + 9 (remaining) = 15 days
```

### Case 3: Exceeded Hours but Not Days

**Scenario**: 
- Required: 12 days, 96 hours
- Completed: 10 days, 100 hours

**Handling**:
```
Days remaining: 2
Hours remaining: 0 (already exceeded)
Suggestions: 2 more days to meet day requirement
```

---

## Validation Rules

### Settings Validation

![Settings Validation](../diagrams/images/settings-validation.png)

*Validation rules for user settings input*

<details>
<summary>📄 View PlantUML source</summary>

```plantuml
@startuml settings-validation
!theme plain

start
:User inputs\nsettings;

if (Days per week\n1-5?) then (no)
    :Error: "Must be 1-5";
    stop
endif

if (Hours per day\n1-12?) then (no)
    :Error: "Must be 1-12";
    stop
endif

if (5 unique\nweekdays?) then (no)
    :Error: "Must order\nall 5 weekdays";
    stop
endif

:Save settings ✅;
stop

@enduml
```

</details>

---

## Business Metrics

### Key Performance Indicators

![KPIs](../diagrams/images/kpis.png)

*Key metrics for measuring app success and user compliance*

<details>
<summary>📄 View PlantUML source</summary>

```plantuml
@startuml kpis
!theme plain

rectangle "User Metrics" {
    (Completion Rate)
    (Average Office Days/Month)
    (Average Hours/Day)
}

rectangle "App Metrics" {
    (Auto-Detection Accuracy)
    (Suggestion Acceptance Rate)
    (Manual Overrides)
}

rectangle "Compliance Metrics" {
    (On-Time Completion)
    (Monthly Requirement Met)
    (Policy Adherence)
}

@enduml
```

</details>

---

## Calculation Examples

### Full Month Scenario

**User**: John Doe  
**Policy**: 3 days/week, 8 hours/day  
**Month**: March 2026 (22 weekdays)  
**Holidays**: March 17 (1 weekday)  
**Vacations**: March 24-28 (5 weekdays)

**Calculations**:
```
Base requirement:
- Days: ceil((3 × 22) / 5) = 14 days
- Hours: 14 × 8 = 112 hours

After holidays:
- Days: 14 - 1 = 13 days
- Hours: 13 × 8 = 104 hours

After vacations:
- Days: 13 - 5 = 8 days
- Hours: 8 × 8 = 64 hours

Final requirement:
- 8 days
- 64 hours
- Must be met by March 23
```

---

**See Also**:
- [Architecture](../architecture/README.md)
- [Use Case Flows](../diagrams/use-case-flows.md)
- [User Guide](../user-guide/README.md)

