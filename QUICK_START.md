# Go2Office - Quick Start Guide

## 🚀 Running the App

### Option 1: Android Studio (Recommended)

1. **Open the project**:
   ```bash
   cd /Users/ctw03933/Go2Office
   open -a "Android Studio" .
   ```

2. **Wait for Gradle sync** to complete

3. **Run the app**:
   - Click the green "Run" button (▶️)
   - Or press `Ctrl+R` (Mac) / `Shift+F10` (Windows/Linux)
   - Select a device or emulator

### Option 2: Command Line

1. **Install on connected device**:
   ```bash
   cd /Users/ctw03933/Go2Office
   ./gradlew installDebug
   ```

2. **Build APK only**:
   ```bash
   ./gradlew assembleDebug
   ```
   - APK will be at: `app/build/outputs/apk/debug/app-debug.apk`

---

## 📱 First Launch

### Onboarding Flow
When you first launch the app, you'll see:

**Step 1: Required Days**
- Use slider to select how many days per week you need to work from office (1-5)
- Example: 3 days per week

**Step 2: Required Hours**
- Use slider to select total office hours per week
- Example: 24 hours per week

**Step 3: Weekday Preferences**
- Order your preferred office days (Monday-Friday)
- Use ↑ ↓ buttons to reorder
- Example: Tuesday → Wednesday → Monday → Thursday → Friday

Click "Complete" to save settings.

---

## 🎯 Using the App

### Dashboard (Main Screen)

**Month Navigation**:
- ◀ Previous month
- ▶ Next month (disabled for future months)

**Progress Card** shows:
- Office Days: X / Y completed
- Office Hours: X / Y completed
- Progress bars for visual feedback
- 🎉 Celebration message when complete

**Suggested Days**:
- Smart suggestions based on your preferences
- Tap a day to open entry screen
- "Top" badge on your #1 preferred day

**Recent Entries**:
- Last 10 entries
- ✓ checkmark for office days
- Tap to edit

**FAB (+ button)**:
- Quick entry for today
- Bottom-right corner

**Settings** (⚙️ icon):
- Top-right corner
- Edit your requirements

---

### Day Entry Screen

1. **Toggle "Was in office?"**:
   - ON = Mark as office day
   - OFF = Mark as not in office

2. **Enter Hours** (if in office):
   - Drag slider (0-24 hours)
   - Or tap quick chips (4h, 6h, 8h, 10h)

3. **Add Notes** (optional):
   - Any context about the day

4. **Save**:
   - Tap "Save Entry" or "Update Entry"
   - Returns to dashboard

5. **Delete** (🗑️ icon):
   - Only visible for existing entries
   - Confirms before deleting

---

### Settings Screen

**Edit Requirements**:
- Adjust required days per week (slider)
- Adjust required hours per week (slider)

**View Preferences**:
- See your current weekday order
- (Future: will allow editing)

**Save Changes**:
- Tap "Save Changes"
- Returns to dashboard

---

## 💡 Pro Tips

### Efficient Workflow
1. Use **FAB** for quick today entries
2. Use **Suggested Days** to plan ahead
3. **Dashboard** shows everything at a glance

### Understanding Progress
- **Days**: Count of days you were in office
- **Hours**: Total hours worked in office
- Both must meet minimum requirements

### Weekday Preferences
- Algorithm suggests days in your preferred order
- Change preferences in Settings (future feature)
- System automatically excludes weekends

### Edge Cases
- **Future dates**: Cannot mark (prevented)
- **Weekends**: Can mark but don't count toward requirement
- **Holidays**: Add via Settings (future feature)
- **Past months**: Can navigate and view history

---

## 🎨 UI Elements

### Color Coding
- **Primary Color**: Important actions, progress
- **Surface Variant**: Cards and containers
- **Tertiary**: Highlights and badges
- **Error**: Validation errors

### Icons
- ◀ ▶ Navigation arrows
- ⚙️ Settings
- + Add entry (FAB)
- 🗑️ Delete
- ✓ Completed/saved

### Progress Bars
- **Blue bar**: Days progress
- **Percentage**: Shown as decimal
- **Linear**: Easy to read

---

## 🐛 Troubleshooting

### App won't build
```bash
cd /Users/ctw03933/Go2Office
./gradlew clean
./gradlew assembleDebug
```

### Gradle sync issues
```bash
./gradlew --refresh-dependencies
```

### Clear app data (reset)
- Android: Settings → Apps → Go2Office → Clear Data
- Or uninstall and reinstall

### Database issues
- Clearing app data resets everything
- You'll go through onboarding again

---

## 📊 Example Usage

### Scenario: 3 days/week requirement

**Monday** (Dashboard):
- See: "0 / 13 days" for the month
- Suggested: Your top 3 preferred days this week

**Tuesday** (Tap suggested day):
- Toggle "Was in office?" ON
- Slider to 8 hours
- Add note: "Team meeting day"
- Save

**Wednesday** (Dashboard):
- Now shows: "1 / 13 days"
- Progress bar updated
- Tuesday appears in Recent Entries

**End of month**:
- Dashboard shows: "13 / 13 days" ✓
- 🎉 "You've met your monthly requirement!"

---

## 🔍 Key Features

### Smart Suggestions
- Based on YOUR preferences
- Excludes already-marked days
- Ignores past dates
- Shows your top choice with "Top" badge

### Flexible Entry
- Can mark any day (even weekends)
- Can edit entries later
- Can delete mistakes
- Quick chips for common hours (4, 6, 8, 10)

### Visual Feedback
- Progress bars
- Percentage complete
- Color-coded states
- Clear indicators

### Month Navigation
- View any past month
- See historical data
- Cannot view future months (realistic)

---

## 📱 Supported Devices

- **Minimum**: Android 8.0 (API 26)
- **Recommended**: Android 10+ (API 29+)
- **Screen sizes**: Phone (optimized)
- **Orientations**: Portrait (primary), Landscape (supported)

---

## 🎯 Monthly Calculation

### How Requirements Are Calculated

**Formula**: `requiredDays = ceil(weekdaysInMonth × (requiredDaysPerWeek ÷ 5))`

**Example** (3 days/week, March 2026):
- March has 22 weekdays (Mon-Fri)
- Calculation: 22 × (3 ÷ 5) = 13.2
- Rounded up: **14 days required**

### Exclusions
- **Weekends** (Sat/Sun): Never counted
- **Holidays**: Reduce weekday count (future)

### Hours Calculation
- Based on proportional distribution
- Example: 24 hours/week, 14 days = ~1.7 hours per day minimum
- But you can work 8 hours some days, 4 others, etc.

---

## 🎓 Understanding the App

### Why Weekday Preferences?
- Not everyone likes Monday!
- Some prefer mid-week collaboration
- Algorithm respects YOUR optimal schedule

### Why Exclude Weekends?
- Most offices don't count weekend work
- Focuses on standard workweek
- You CAN still mark weekends if needed

### Why Monthly View?
- Most office policies are monthly
- Easy to track compliance
- Historical records by month

---

## 🚀 Next Steps

After using the app:
1. ⭐ Check **IMPLEMENTATION_COMPLETE.md** for full details
2. 📝 Read **ARCHITECTURE.md** to understand structure
3. 🎫 See **TICKETS.md** for all features
4. 📖 Review **README.md** for overview

---

## 💬 Feedback

Love the app? Want features?
- Check TICKETS.md for planned features
- Optional enhancements (#44-46) can be implemented
- Code is well-structured for easy extension

---

**Enjoy tracking your office days! 🎉**

*Built with Kotlin, Jetpack Compose, and Clean Architecture*

