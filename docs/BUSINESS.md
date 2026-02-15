# Business Logic

## Monthly Requirements

### Formula
```
requiredDays = ceil((daysPerWeek × weekdaysInMonth) / 5)
requiredHours = requiredDays × hoursPerDay
```

### Example
- Settings: 3 days/week, 8 hours/day
- March 2026: 22 weekdays
- Required: ceil((3 × 22) / 5) = 14 days = 112 hours

### Adjustments
- Holidays: reduce weekdays count
- Vacations: reduce weekdays count  
- Weekends: always excluded

## Smart Suggestions

Distributes remaining days evenly across weeks, respecting user's weekday preferences.

**Algorithm**: For each week, suggest `ceil(remaining / weeksLeft)` days from preferred weekdays.

## Hour Tracking

### Rules
- Count only 7 AM - 7 PM
- Max 10 hours per day
- Multiple visits per day are combined

### Examples
| Entry | Exit | Hours |
|-------|------|-------|
| 8:30 AM | 5:45 PM | 9.25h |
| 6:00 AM | 3:00 PM | 8.0h (clamped) |
| 9:00 AM | 9:00 PM | 10.0h (capped) |

## Progress Calculation

```
daysProgress = (completed / required) × 100
hoursProgress = (completed / required) × 100
overallProgress = min(days%, hours%)
```

**Status**:
- Complete: both requirements met
- On Track: ≥80% progress
- Behind: 50-80% progress
- Critical: <50% progress

## Edge Cases

### Insufficient Days
If not enough weekdays remain → show warning, suggest all remaining days

### Mid-Month Change
Keep completed days, recalculate requirement for remaining month

### Hours Met but Not Days
Must still complete remaining days

