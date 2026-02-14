# 🧪 Testing Go2Office as New User - Guide

## Methods to Reset the App

### Method 1: Clear App Data (Recommended - Easiest)

**On Physical Device or Emulator:**

1. **Go to Android Settings**
   ```
   Settings → Apps → Go2Office
   ```

2. **Clear App Data**
   ```
   Storage → Clear Data → OK
   ```
   
3. **Also Clear Cache (optional)**
   ```
   Storage → Clear Cache
   ```

4. **Relaunch the App**
   - App will start fresh with onboarding screen
   - All database entries deleted
   - All settings reset
   - No previous user data

**Result:** ✅ App behaves exactly like first install

---

### Method 2: Uninstall and Reinstall

**Steps:**

1. **Uninstall the App**
   ```bash
   # Via ADB (from terminal)
   adb uninstall com.example.go2office
   
   # Or manually on device
   Long-press app icon → Uninstall → OK
   ```

2. **Reinstall the App**
   ```bash
   # From Android Studio
   Click "Run" button (▶️)
   
   # Or via terminal
   ./gradlew installDebug
   ```

**Result:** ✅ Complete fresh installation

---

### Method 3: Delete Database Directly (ADB)

**For rooted device or emulator:**

```bash
# Connect via ADB
adb shell

# Navigate to app data
cd /data/data/com.example.go2office/databases/

# List databases
ls -la

# Delete the database
rm office_database
rm office_database-shm
rm office_database-wal

# Exit shell
exit

# Force stop the app
adb shell am force-stop com.example.go2office

# Relaunch the app
adb shell am start -n com.example.go2office/.MainActivity
```

**Result:** ✅ Database cleared, app restarts with onboarding

---

### Method 4: Add Debug Menu (Code Change)

**Add a hidden debug option to clear data from within the app:**

Create a debug settings screen:

```kotlin
// In SettingsScreen.kt - add a developer options section

@Composable
private fun DeveloperOptions(
    viewModel: SettingsViewModel
) {
    var showConfirmation by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "⚠️ Developer Options",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
            
            Button(
                onClick = { showConfirmation = true },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Clear All Data (Reset App)")
            }
            
            Text(
                text = "This will delete ALL data and settings. App will restart with onboarding.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
        }
    }
    
    if (showConfirmation) {
        ConfirmationDialog(
            title = "Clear All Data?",
            message = "This will delete:\n• All settings\n• All daily entries\n• All detection history\n• All holidays\n\nThe app will restart with onboarding.",
            onConfirm = {
                viewModel.clearAllData()
                // Restart app or navigate to onboarding
            },
            onDismiss = { showConfirmation = false },
            confirmText = "Clear Everything",
            dismissText = "Cancel"
        )
    }
}
```

Then add to ViewModel:

```kotlin
// In SettingsViewModel
fun clearAllData() {
    viewModelScope.launch {
        repository.deleteAllData()
        // Trigger app restart or navigation to onboarding
    }
}
```

---

## 🧪 Testing Scenarios

### Scenario 1: Test Onboarding Flow

1. **Clear app data** (Method 1)
2. **Launch app**
3. **Expected behavior:**
   - ✅ Onboarding screen appears (Step 1/3)
   - ✅ Can select required days (1-5)
   - ✅ Can select required hours (slider)
   - ✅ Can order weekday preferences
   - ✅ Can complete setup
   - ✅ Navigates to dashboard after completion

### Scenario 2: Test Dashboard with No Data

1. **Clear app data**
2. **Complete onboarding** (set 3 days/week, 24h/week)
3. **Expected behavior:**
   - ✅ Dashboard shows current month
   - ✅ Progress shows 0/X days
   - ✅ Progress shows 0/X hours
   - ✅ Suggested days appear
   - ✅ No recent entries shown
   - ✅ All UI elements render correctly

### Scenario 3: Test Auto-Detection Setup

1. **Clear app data**
2. **Complete onboarding**
3. **Go to Settings → Auto-Detection**
4. **Expected behavior:**
   - ✅ Auto-detection toggle is OFF
   - ✅ No office location set
   - ✅ Can set office location
   - ✅ Can configure geofence radius
   - ✅ Can enable auto-detection
   - ✅ Requests location permissions

---

## 🔍 Verification Checklist

After clearing data, verify:

- [ ] Onboarding appears on app launch
- [ ] Can complete all onboarding steps
- [ ] Settings are empty/default
- [ ] Dashboard shows zero progress
- [ ] No daily entries exist
- [ ] No holidays configured
- [ ] Auto-detection is disabled
- [ ] No office location set
- [ ] App doesn't crash

---

## 📱 Quick Commands Reference

### Via Android Studio:

```bash
# Clear app data
adb shell pm clear com.example.go2office

# Then relaunch
Click Run (▶️) button
```

### Via Terminal:

```bash
# Clear data
adb shell pm clear com.example.go2office

# Relaunch app
adb shell am start -n com.example.go2office/.MainActivity
```

### Check Current Data:

```bash
# View databases
adb shell ls -la /data/data/com.example.go2office/databases/

# Check shared preferences
adb shell ls -la /data/data/com.example.go2office/shared_prefs/
```

---

## 🐛 Troubleshooting

### Problem: Onboarding doesn't appear after clearing data

**Solution:**
```bash
# Force stop the app first
adb shell am force-stop com.example.go2office

# Clear data
adb shell pm clear com.example.go2office

# Restart app
adb shell am start -n com.example.go2office/.MainActivity
```

### Problem: Old data still appears

**Solution:**
```bash
# Uninstall completely
adb uninstall com.example.go2office

# Reinstall
./gradlew installDebug
```

### Problem: Permission errors when accessing database

**Solution:**
- Use Method 1 (Clear App Data via Settings)
- This is the safest method and always works

---

## 🎯 Best Practice for Testing

**Recommended Testing Flow:**

1. **Before each test session:**
   ```bash
   adb shell pm clear com.example.go2office
   ```

2. **Test the onboarding flow**

3. **Test main features**

4. **Clear again for next test**

**Pro Tip:** Create a bash alias for quick reset:

```bash
# Add to ~/.zshrc or ~/.bashrc
alias reset-go2office="adb shell pm clear com.example.go2office && adb shell am start -n com.example.go2office/.MainActivity"

# Then just run:
reset-go2office
```

---

## 📊 Data That Gets Cleared

When you clear app data, the following are deleted:

✅ **Room Database:**
- office_settings table (your requirements)
- daily_entries table (all logged days)
- monthly_logs table (historical data)
- holidays table (configured holidays)
- office_locations table (auto-detection locations)
- office_presence table (detection sessions)

✅ **Shared Preferences:**
- Any saved settings
- UI state
- User preferences

✅ **Files:**
- Any cached data
- Temporary files

**What DOESN'T get cleared:**
- The APK itself (app stays installed)
- App permissions (may need to re-grant)

---

## 🚀 Quick Reset Command

**The fastest way to test as new user:**

```bash
# One-liner: Clear data and relaunch
adb shell pm clear com.example.go2office && adb shell am start -n com.example.go2office/.MainActivity
```

**Or create a script:**

```bash
#!/bin/bash
# save as reset-go2office.sh

echo "🧹 Clearing Go2Office data..."
adb shell pm clear com.example.go2office

echo "🚀 Relaunching app..."
adb shell am start -n com.example.go2office/.MainActivity

echo "✅ Go2Office reset complete! App should show onboarding."
```

Make executable and run:
```bash
chmod +x reset-go2office.sh
./reset-go2office.sh
```

---

## ✅ Summary

**Easiest Method:** 
```
Device Settings → Apps → Go2Office → Storage → Clear Data
```

**Fastest Method (with ADB):**
```bash
adb shell pm clear com.example.go2office
```

**Most Thorough Method:**
```bash
adb uninstall com.example.go2office
./gradlew installDebug
```

---

*Now you can test the onboarding flow and new user experience as many times as needed!* 🎉

