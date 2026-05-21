# Release Packaging And Bilingual Localization Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Add a signed `release APK` pipeline plus English and Simplified Chinese localization with an in-app language switch.

**Architecture:** Keep `app/src/main` as the shared source set, make `release` a real signed and shrunk packaging target, and move active `v2` UI copy into Android string resources. Persist language preference in DataStore and apply it through AndroidX locale APIs so the app updates immediately without custom resource plumbing.

**Tech Stack:** Android Gradle Plugin, Kotlin, Compose Material 3, DataStore Preferences, Hilt, GitHub Actions, Android string resources

---

### Task 1: Document Variant And Signing Model

**Files:**
- Create: `docs/release-setup.md`
- Create: `keystore.properties.example`
- Modify: `.gitignore`

- [ ] **Step 1: Write the shared variant and signing reference doc**

Document:
- what `app/src/main` affects
- what `debug` adds
- what `release` changes
- which files are secret
- which GitHub Secrets are required
- how to back up the keystore

- [ ] **Step 2: Add a checked-in signing template**

Create `keystore.properties.example` with these keys and placeholder values:

```properties
storeFile=release-keystore.jks
storePassword=CHANGE_ME
keyAlias=clockinpro
keyPassword=CHANGE_ME
```

- [ ] **Step 3: Ignore local signing secrets**

Ensure `.gitignore` includes:

```gitignore
keystore.properties
release-keystore.jks
release-keystore.keystore
```

### Task 2: Wire Release Signing Into Gradle

**Files:**
- Modify: `app/build.gradle`

- [ ] **Step 1: Add property loading for signing config**

Load `keystore.properties` from the repo root when present and fall back to Gradle properties for CI.

- [ ] **Step 2: Define a release signing config**

Attach:
- `storeFile`
- `storePassword`
- `keyAlias`
- `keyPassword`

only when signing values are present so the project still syncs cleanly without committed secrets.

- [ ] **Step 3: Make `release` a real packaging target**

Update `release` to:
- use the release signing config when available
- set `minifyEnabled true`
- set `shrinkResources true`

- [ ] **Step 4: Keep `debug` unchanged**

Do not add release-only signing logic to debug.

### Task 3: Publish Release APK From GitHub Actions

**Files:**
- Modify: `.github/workflows/build.yml`

- [ ] **Step 1: Add signing secret reconstruction**

Decode a Base64 keystore secret into a temporary file during the workflow and expose alias/password values as Gradle-readable properties.

- [ ] **Step 2: Build the release artifact**

Replace `assembleDebug` with `assembleRelease`.

- [ ] **Step 3: Upload the release APK**

Upload:

```text
app/build/outputs/apk/release/app-release.apk
```

- [ ] **Step 4: Update release publishing metadata**

Rename artifacts and release notes to reference `release` instead of `debug`.

### Task 4: Add App Language Persistence And Locale Switching

**Files:**
- Modify: `app/src/main/java/com/clockinpro/data/local/PreferencesManager.kt`
- Modify: `app/src/main/java/com/clockinpro/ClockInApp.kt`
- Modify: `app/src/main/java/com/clockinpro/ui/MainActivity.kt`
- Modify: `app/src/main/java/com/clockinpro/v2/ui/settings/SettingsViewModel.kt`

- [ ] **Step 1: Add a stored app-language preference**

Persist one of:
- `system`
- `en`
- `zh-CN`

- [ ] **Step 2: Expose language state to the settings screen**

Extend `SettingsUiState` with the selected language and busy/error states as needed.

- [ ] **Step 3: Apply app locales through AndroidX**

Initialize and update app locales using AndroidX app-locale APIs so the change takes effect immediately.

- [ ] **Step 4: Localize notification channel creation**

Use string resources for the notification channel name and description when the app creates it.

### Task 5: Replace Active UI Hardcoded Text With Resources

**Files:**
- Modify: `app/src/main/res/values/strings.xml`
- Create: `app/src/main/res/values-zh-rCN/strings.xml`
- Modify active `v2` UI files under:
  - `app/src/main/java/com/clockinpro/v2/ui/home/`
  - `app/src/main/java/com/clockinpro/v2/ui/onboarding/`
  - `app/src/main/java/com/clockinpro/v2/ui/components/`
  - `app/src/main/java/com/clockinpro/v2/ui/detail/`
  - `app/src/main/java/com/clockinpro/v2/ui/settings/`

- [ ] **Step 1: Replace the corrupted default strings file**

Make default `values/strings.xml` valid English for active app strings.

- [ ] **Step 2: Add Simplified Chinese translations**

Create `values-zh-rCN/strings.xml` with the same active string keys translated into Simplified Chinese.

- [ ] **Step 3: Convert active screens to `stringResource(...)`**

Cover:
- onboarding
- home
- target create/edit/delete
- target detail
- settings
- snackbar and dialog copy
- accessibility content descriptions

- [ ] **Step 4: Keep only active, justified strings**

Prefer focused resource coverage for the active `v2` flow over reviving unused legacy screens.

### Task 6: Add Language Picker To Settings

**Files:**
- Modify: `app/src/main/java/com/clockinpro/v2/ui/settings/SettingsScreen.kt`

- [ ] **Step 1: Add a language settings card**

Present:
- Follow system
- English
- 简体中文

- [ ] **Step 2: Wire selection to the view model**

Selecting a language should persist the preference and update the app locale immediately.

- [ ] **Step 3: Keep backup and reminder sections localized**

Move all visible Settings copy to string resources.

### Task 7: Verify Release And Localization Behavior

**Files:**
- Modify if needed: `app/proguard-rules.pro`

- [ ] **Step 1: Run text/config verification searches**

Check for:
- lingering active hardcoded strings
- tracked signing files
- release pipeline still pointing at debug artifacts

- [ ] **Step 2: Run build verification where environment allows**

Use:
- local release build if JDK and SDK are available
- otherwise document the local limitation and rely on GitHub Actions for full build confirmation

- [ ] **Step 3: Tighten keep rules only if release shrinking fails**

Add minimal rules required by the first verified release build instead of disabling shrinking.
