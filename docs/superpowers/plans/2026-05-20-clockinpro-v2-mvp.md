# ClockInPro v2 MVP Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Rebuild ClockInPro into a local-first multi-target check-in app with onboarding, reminders, stats, and JSON backup.

**Architecture:** Keep the existing Android Compose/Hilt/Room foundation, introduce a new v2 local data model and repositories, then replace the app navigation and screens with onboarding, dashboard, detail, and settings flows. Destructive migration is acceptable and expected for the v1 to v2 transition.

**Tech Stack:** Kotlin, Jetpack Compose, Material 3, Room, Hilt, DataStore, AlarmManager, Gson, JUnit

---

### Task 1: Add test scaffolding for core v2 logic

**Files:**
- Create: `app/src/test/java/com/clockinpro/v2/util/DateKeyUtilsTest.kt`
- Create: `app/src/test/java/com/clockinpro/v2/domain/StatsCalculatorTest.kt`
- Create: `app/src/test/java/com/clockinpro/v2/data/backup/BackupCodecTest.kt`
- Modify: `app/build.gradle`

- [ ] Step 1: Add coroutine test dependency if needed.
- [ ] Step 2: Write failing tests for date key formatting.
- [ ] Step 3: Run the unit test command and verify failure.
- [ ] Step 4: Write failing tests for streak and total calculation.
- [ ] Step 5: Run the unit test command and verify failure.
- [ ] Step 6: Write failing tests for backup encode/decode round-trip.
- [ ] Step 7: Run the unit test command and verify failure.

### Task 2: Build the v2 domain and persistence layer

**Files:**
- Create: `app/src/main/java/com/clockinpro/v2/data/local/TargetEntity.kt`
- Create: `app/src/main/java/com/clockinpro/v2/data/local/CompletionEntity.kt`
- Create: `app/src/main/java/com/clockinpro/v2/data/local/TargetDao.kt`
- Create: `app/src/main/java/com/clockinpro/v2/data/local/CompletionDao.kt`
- Create: `app/src/main/java/com/clockinpro/v2/domain/model/*.kt`
- Create: `app/src/main/java/com/clockinpro/v2/data/repository/*.kt`
- Modify: `app/src/main/java/com/clockinpro/data/local/AppDatabase.kt`
- Modify: `app/src/main/java/com/clockinpro/di/DatabaseModule.kt`

- [ ] Step 1: Implement the new entities and DAOs.
- [ ] Step 2: Wire them into Room and Hilt with destructive migration.
- [ ] Step 3: Implement minimal repository methods for targets, completions, stats, and import/export support.
- [ ] Step 4: Run unit tests and fix compilation gaps until green.

### Task 3: Replace app state, navigation, and onboarding

**Files:**
- Create: `app/src/main/java/com/clockinpro/v2/ui/onboarding/*.kt`
- Modify: `app/src/main/java/com/clockinpro/data/local/PreferencesManager.kt`
- Modify: `app/src/main/java/com/clockinpro/ui/MainViewModel.kt`
- Modify: `app/src/main/java/com/clockinpro/ui/MainActivity.kt`
- Modify: `app/src/main/java/com/clockinpro/ui/navigation/AppNavigation.kt`
- Modify: `app/src/main/java/com/clockinpro/ui/navigation/Screen.kt`

- [ ] Step 1: Add onboarding completion preference and tests if logic is extracted.
- [ ] Step 2: Replace login-gated navigation with onboarding-or-home start routing.
- [ ] Step 3: Build the three-page onboarding experience and finish action.
- [ ] Step 4: Run tests and `assembleDebug` to verify the new app shell.

### Task 4: Implement the dashboard and target management flow

**Files:**
- Create: `app/src/main/java/com/clockinpro/v2/ui/home/*.kt`
- Create: `app/src/main/java/com/clockinpro/v2/ui/components/*.kt`
- Modify: `app/src/main/java/com/clockinpro/ui/home/HomeScreen.kt`

- [ ] Step 1: Build dashboard state models and target card UI.
- [ ] Step 2: Implement add/edit target dialog with icon, color, and reminder controls.
- [ ] Step 3: Implement instant completion, undo, and haptic feedback.
- [ ] Step 4: Run unit tests and `assembleDebug`.

### Task 5: Implement target detail, calendar stats, and settings backup

**Files:**
- Create: `app/src/main/java/com/clockinpro/v2/ui/detail/*.kt`
- Create: `app/src/main/java/com/clockinpro/v2/ui/settings/*.kt`
- Create: `app/src/main/java/com/clockinpro/v2/data/backup/*.kt`
- Modify: `app/src/main/java/com/clockinpro/ui/record/RecordScreen.kt`

- [ ] Step 1: Build stats calculator and calendar rendering support.
- [ ] Step 2: Implement target detail screen with month navigation and recent records.
- [ ] Step 3: Implement JSON export/import settings flows.
- [ ] Step 4: Run unit tests and `assembleDebug`.

### Task 6: Reminder plumbing and product cleanup

**Files:**
- Create: `app/src/main/java/com/clockinpro/v2/reminder/*.kt`
- Modify: `app/src/main/java/com/clockinpro/ClockInApp.kt`
- Modify: `app/src/main/java/com/clockinpro/worker/ReminderReceiver.kt`
- Modify: `app/src/main/java/com/clockinpro/worker/BootReceiver.kt`
- Modify: `app/src/main/AndroidManifest.xml`

- [ ] Step 1: Replace reminder payload handling with target-based reminders.
- [ ] Step 2: Reschedule reminders on boot and after import.
- [ ] Step 3: Remove location, camera, background location, file provider, and exact alarm declarations.
- [ ] Step 4: Run `assembleDebug` and targeted tests to verify final integration.
