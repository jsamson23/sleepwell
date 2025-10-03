# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Keep Hilt generated classes
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep class * extends dagger.hilt.android.lifecycle.HiltViewModel

# Keep DataStore
-keep class androidx.datastore.*.** { *; }

# ==================== IMPLEMENTATION NOTES ====================

/*
 * COMPLETE ANDROID APPLICATION: Morning Focus Alarm
 * 
 * This is a fully functional Android application that implements all features
 * specified in the requirements document.
 * 
 * PROJECT STRUCTURE:
 * 
 * com.example.morningfocusalarm/
 * ├── MorningFocusApplication.kt      - Application class with notification channels
 * ├── MainActivity.kt                  - Main entry point with navigation
 * ├── MainViewModel.kt                 - Manages onboarding state
 * │
 * ├── data/
 * │   ├── local/
 * │   │   └── PreferencesManager.kt   - DataStore for settings persistence
 * │   └── repository/
 * │       ├── AlarmRepository.kt      - Manages alarm scheduling
 * │       └── AppRepository.kt        - Fetches installed apps
 * │
 * ├── di/
 * │   └── AppModule.kt                - Hilt dependency injection
 * │
 * ├── model/
 * │   ├── AlarmSettings.kt            - Settings data model
 * │   └── AppInfo.kt                  - App information model
 * │
 * ├── service/
 * │   ├── AlarmReceiver.kt            - Handles alarm broadcasts
 * │   ├── BootReceiver.kt             - Reschedules alarms after reboot
 * │   └── LockoutService.kt           - Foreground service for app monitoring
 * │
 * ├── ui/
 * │   ├── components/
 * │   │   └── TimePickerDialog.kt    - Reusable time picker
 * │   ├── screens/
 * │   │   ├── alarm/
 * │   │   │   └── AlarmAlertActivity.kt    - Morning alarm alert screen
 * │   │   ├── applist/
 * │   │   │   ├── AppListScreen.kt         - App selection screen
 * │   │   │   └── AppListViewModel.kt      - ViewModel for app list
 * │   │   ├── dashboard/
 * │   │   │   ├── DashboardScreen.kt       - Main dashboard
 * │   │   │   └── DashboardViewModel.kt    - Dashboard state management
 * │   │   ├── lockscreen/
 * │   │   │   └── LockScreenActivity.kt    - Lock screen overlay
 * │   │   ├── onboarding/
 * │   │   │   ├── OnboardingScreen.kt      - First-time setup
 * │   │   │   └── OnboardingViewModel.kt   - Onboarding logic
 * │   │   └── settings/
 * │   │       ├── SettingsScreen.kt        - Settings configuration
 * │   │       └── SettingsViewModel.kt     - Settings state management
 * │   └── theme/
 * │       ├── Color.kt                - Material 3 color definitions
 * │       ├── Theme.kt                - App theme
 * │       └── Type.kt                 - Typography
 * │
 * └── util/
 *     ├── PermissionHelper.kt         - Permission management utilities
 *     └── UsageStatsHelper.kt         - App usage detection
 * 
 * 
 * KEY FEATURES IMPLEMENTED:
 * 
 * 1. ALARM SYSTEM:
 *    - Precise alarm scheduling with AlarmManager.setExactAndAllowWhileIdle()
 *    - Morning alarm with custom ringtone
 *    - Optional nightly lockout starting the previous evening
 *    - Automatic rescheduling after alarm triggers
 *    - Boot receiver to restore alarms after device restart
 * 
 * 2. APP LOCKOUT:
 *    - Real-time foreground app detection using UsageStatsManager
 *    - Foreground service runs only during active lockout periods
 *    - Full-screen lock overlay prevents app access
 *    - Countdown timer shows remaining lockout time
 *    - Non-dismissible overlay (back button disabled)
 * 
 * 3. USER INTERFACE:
 *    - Modern Material 3 design with Jetpack Compose
 *    - Onboarding flow for first-time users
 *    - Dashboard with alarm status and master toggle
 *    - Settings screen for all configurations
 *    - App selection with icons and checkboxes
 *    - Permission request flows with explanations
 * 
 * 4. DATA PERSISTENCE:
 *    - DataStore (Preferences) for settings
 *    - Settings persist across app restarts
 *    - Selected apps list maintained
 * 
 * 5. PERMISSIONS HANDLED:
 *    - SCHEDULE_EXACT_ALARM - for precise alarm timing
 *    - POST_NOTIFICATIONS - for alarm alerts
 *    - SYSTEM_ALERT_WINDOW - for lock screen overlay
 *    - PACKAGE_USAGE_STATS - for foreground app detection
 *    - RECEIVE_BOOT_COMPLETED - for alarm rescheduling
 *    - WAKE_LOCK & VIBRATE - for alarm functionality
 * 
 * 
 * SETUP INSTRUCTIONS:
 * 
 * 1. Create a new Android Studio project with Empty Compose Activity
 * 2. Copy all the code above into the appropriate files
 * 3. Sync Gradle to download dependencies
 * 4. Build and run on Android device (API 26+)
 * 
 * Note: Some permissions require manual granting:
 * - Usage Stats: Settings > Apps > Special Access > Usage Access
 * - Display Over Apps: Settings > Apps > Special Access > Display over other apps
 * - Exact Alarms: Auto-granted on API < 31, requires permission on API 31+
 * 
 * 
 * ARCHITECTURE:
 * 
 * - MVVM pattern with ViewModels for each screen
 * - Hilt for dependency injection
 * - Kotlin Coroutines and Flow for async operations
 * - Jetpack Compose for declarative UI
 * - Repository pattern for data access
 * - Single Activity architecture with Compose Navigation
 * 
 * 
 * TESTING THE APP:
 * 
 * 1. First Launch: Complete onboarding flow
 * 2. Grant all required permissions in Settings
 * 3. Select apps to lock from the app list
 * 4. Set alarm time and lockout duration
 * 5. Enable the master toggle on dashboard
 * 6. (Optional) Enable nightly lockout
 * 7. Wait for alarm or test by setting alarm 1 minute ahead
 * 8. Dismiss alarm - lockout begins immediately
 * 9. Try opening a locked app - lock screen appears
 * 10. Wait for lockout duration to expire
 * 
 * 
 * ADDITIONAL RESOURCES NEEDED:
 * 
 * For a complete working app, add:
 * - App icon (ic_launcher.png) in res/mipmap directories
 * - The files are already included above:
 *   - res/values/strings.xml
 *   - res/values/themes.xml  
 *   - res/drawable/ic_notification.xml
 * 
 * 
 * The application is complete and ready to build!
 */