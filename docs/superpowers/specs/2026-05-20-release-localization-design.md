# Release Packaging And Bilingual Localization Design

## Goal

Turn ClockInPro into a proper release-ready Android app by introducing a real `release` pipeline, documenting how `main` / `debug` / `release` map onto the project, and adding first-class Simplified Chinese plus English localization with an in-app language switch.

## Current State

- The app currently ships from `app/src/main` and the active navigation flow points at the `v2` screens.
- GitHub Actions builds `assembleDebug`, uploads `app-debug.apk`, and does not produce a signed release artifact.
- `release` exists as a build type, but it is not signed, does not enable shrinking, and is not meaningfully differentiated from `debug`.
- UI copy is split between hardcoded English strings in Compose code and a legacy `strings.xml` whose Chinese content is visibly mis-encoded.

## Scope

This work includes:

- documenting the relationship between `app/src/main`, the implicit `debug` variant, and the explicit `release` build type
- generating and wiring a new release keystore workflow
- moving signing secrets out of tracked Gradle files
- enabling code shrinking and resource shrinking for `release`
- changing GitHub Actions to build a signed `release APK`
- replacing hardcoded UI copy in the active app flow with Android string resources
- adding `values/strings.xml` for English and `values-zh-rCN/strings.xml` for Simplified Chinese
- adding an in-app language selector in Settings and persisting the choice

This work does not include:

- Play Store publishing
- additional languages beyond English and Simplified Chinese
- redesigning unrelated old `ui/*` screens that are no longer in the active navigation flow

## Build Variant Model

### `main`

`app/src/main` remains the shared source set. It contains the application manifest, shared resources, data layer, and the active `v2` UI flow. Any change here affects both `debug` and `release`.

### `debug`

The project uses the default Android `debug` variant. It inherits everything from `main` and adds debug-only dependencies such as Compose tooling. It is intended for local development and CI smoke builds.

### `release`

The project uses the Android `release` build type defined in `app/build.gradle`. It should inherit `main`, disable debugging, use the release signing config, enable shrinking, and be the only artifact published by GitHub Actions. The app behavior should remain functionally aligned with `debug`, but packaging and security characteristics should differ.

## Signing Design

### Local Development

- Generate one new release keystore for the project.
- Store the keystore file outside tracked source control or keep it locally under the workspace while ignoring it via `.gitignore`.
- Read signing values from an untracked `keystore.properties` file in the project root.
- Support a fallback to Gradle properties so CI can inject the same values without committing secrets.

### CI

- Store the keystore as Base64 in GitHub Secrets.
- Store alias and passwords in separate GitHub Secrets.
- During workflow execution, reconstruct the keystore file, generate `keystore.properties` or pass equivalent Gradle properties, then run `assembleRelease`.

### Security Notes

- Do not hardcode passwords in `app/build.gradle`.
- Do not commit `keystore.properties`, `.jks`, or `.keystore` files.
- Remind the user to back up the generated keystore and all passwords in multiple safe locations because future app updates depend on them.

## Release Packaging Design

- Enable `minifyEnabled true` for `release`.
- Enable `shrinkResources true` for `release`.
- Keep existing ProGuard rules and extend them only if the first release build reveals missing keep rules.
- Leave `debug` behavior unchanged.
- Publish `app-release.apk` from GitHub Actions instead of `app-debug.apk`.

## Localization Design

### Resource Strategy

- Use Android string resources as the single source of truth for active `v2` screens and release-facing system text.
- Make English the default `values/strings.xml`.
- Add Simplified Chinese translations in `values-zh-rCN/strings.xml`.
- Replace hardcoded visible text, snackbar messages, dialog labels, content descriptions, and notification channel strings in the active app flow.

### Locale Persistence

- Add a stored app language preference in `PreferencesManager`.
- Support three internal states:
  - `system`
  - `en`
  - `zh-CN`
- Use AndroidX app locale APIs so the selected language applies across the app without building a custom resource loader.

### UI Surface

- Add a language section to the `v2` Settings screen.
- Present three choices:
  - Follow system
  - English
  - 简体中文
- Update the app locale immediately after selection and persist it through DataStore-backed preferences.

### Date And Time Formatting

- Continue using `Locale.getDefault()` in date-format helpers, but ensure the app locale change actually updates the default resources context used by the app so month and weekday strings follow the selected language.

## Files And Responsibilities

- `app/build.gradle`
  - release signing wiring
  - shrink configuration
  - locale support dependency if required
- `.gitignore`
  - ignore `keystore.properties`
- `keystore.properties.example`
  - document required keys without secrets
- `.github/workflows/build.yml`
  - decode signing secrets and build `assembleRelease`
- `docs/`
  - record `main` / `debug` / `release` behavior and secret setup steps
- `app/src/main/res/values/strings.xml`
  - default English strings
- `app/src/main/res/values-zh-rCN/strings.xml`
  - Simplified Chinese translations
- `app/src/main/java/com/clockinpro/data/local/PreferencesManager.kt`
  - persisted app-language setting
- `app/src/main/java/com/clockinpro/ClockInApp.kt`
  - locale initialization and localized notification channel text
- `app/src/main/java/com/clockinpro/ui/MainActivity.kt`
  - app locale bootstrap if needed by the chosen API
- `app/src/main/java/com/clockinpro/v2/ui/settings/SettingsViewModel.kt`
  - expose language state and selection action
- `app/src/main/java/com/clockinpro/v2/ui/settings/SettingsScreen.kt`
  - render language picker and localized copy
- active `v2` UI composables
  - replace hardcoded UI text with `stringResource(...)`

## Error Handling And Risks

- Shrinking may surface missing keep rules for Hilt, Room, Gson, or Compose-generated code. If release build verification fails, add the minimal keep rules needed instead of disabling shrinking.
- Locale switching can leave stray hardcoded strings if we only patch Settings. The implementation must cover the active `v2` flow, not just one screen.
- Notification channel names are sticky after creation on some Android versions. We should use localized strings for new installs and document that existing installed channels may retain their prior label until app data is cleared or the channel is recreated.

## Testing Strategy

- Build verification:
  - local `assembleRelease` when a JDK and Android SDK are available
  - GitHub Actions `assembleRelease`
- Functional checks:
  - Settings language selector changes visible UI copy
  - onboarding, home, target editor/detail, and settings screens show localized text
  - backup/export snackbar messages localize correctly
- Packaging checks:
  - `debug` artifact remains available for local development if needed
  - CI publishes signed `app-release.apk`

## Success Criteria

- The repository clearly documents what goes into `main`, `debug`, and `release`.
- The app can build a signed `release APK` through GitHub Actions once secrets are configured.
- Release builds enable shrinking and resource shrinking.
- The active app flow supports English and Simplified Chinese, including an in-app language switch that persists.
