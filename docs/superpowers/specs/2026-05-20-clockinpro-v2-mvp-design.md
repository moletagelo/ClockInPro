# ClockInPro v2 MVP Design

**Date:** 2026-05-20
**Scope:** Replace the v1 attendance-oriented flow with a local-first personal habit check-in MVP.

## Goals

- Remove all account, login, and remote-first assumptions.
- Make the app usable offline with local Room persistence only.
- Support multiple custom targets with instant daily completion.
- Add a lightweight onboarding flow, per-target reminders, and local JSON backup/restore.

## Product Shape

### Information Architecture

- `Onboarding`: three minimal pages shown only on first launch.
- `Home`: default landing screen with today’s date, settings entry, target grid, and add-target FAB.
- `Target Detail`: per-target calendar, streak, total completions, and edit/delete actions.
- `Settings`: JSON export/import and reminder notification info.

### Core Interaction

- Tapping a target’s check action marks it complete for today immediately in UI.
- A success state animates on the card and triggers haptic feedback.
- An undo snackbar appears after completion.
- Tapping the card body opens the target detail screen.
- Long-lived destructive flows require confirmation only for deleting a target.

## Technical Design

### Data Model

Use a new Room schema with destructive migration from v1.

- `TargetEntity`
  - `id`
  - `name`
  - `iconKey`
  - `colorKey`
  - `reminderEnabled`
  - `reminderHour`
  - `reminderMinute`
  - `createdAt`
  - `sortOrder`
- `CompletionEntity`
  - `id`
  - `targetId`
  - `dateKey` (`yyyy-MM-dd`)
  - `completedAt`

Legacy `User`, auth state, location, and photo capture are removed from the active v2 flow.

### State and Storage

- `PreferencesManager` stores `hasCompletedOnboarding` and existing appearance preferences.
- Room `Flow`s drive the dashboard and detail screens.
- JSON export/import serializes all targets and completions, clears current local data on import, and reschedules reminders afterward.

### Reminders

- Reminders become a target attribute instead of a standalone screen.
- Schedule local daily alarms with `AlarmManager.setInexactRepeating`.
- Keep `POST_NOTIFICATIONS`, `RECEIVE_BOOT_COMPLETED`, and vibration support.
- Remove location, camera, storage-provider, and exact-alarm requirements from the active product path.

## Screen-Level Design

### Onboarding

- Page 1: local-first privacy and no-account positioning.
- Page 2: multi-target customization and reminders.
- Page 3: one-tap completion, streaks, and local backup.
- Final CTA marks onboarding complete and navigates to home.

### Home

- Top app bar shows formatted date and a settings icon.
- Empty state encourages creating the first target.
- Non-empty state uses a responsive target grid.
- Each target card shows icon, name, reminder time when enabled, and today completion state.
- Overflow actions open edit and delete affordances.

### Target Detail

- Top section shows target identity and actions.
- Stats row shows current streak and total completions.
- Month calendar highlights completed days.
- Recent completion list gives lightweight history context.

### Settings

- Export to JSON through Android document creation.
- Import from JSON through document picker.
- Show a short privacy explanation that data stays on-device unless exported manually.

## Error Handling

- Local writes should fail gracefully with snackbar messaging.
- Invalid or unreadable import files should not wipe current data.
- Import is all-or-nothing inside a database transaction.
- Reminder scheduling failures should not block target creation or editing.

## Testing Strategy

- Add unit tests first for date-key generation, streak/stat calculations, and backup serialization.
- Verify the new database-backed repository behavior through focused integration-safe logic where possible.
- Run the Gradle unit test suite and the app assemble task before completion.
