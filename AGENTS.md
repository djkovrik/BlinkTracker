# Blinkz Agent Guide

This file is the source of truth for agents working in this repository. Keep `CLAUDE.md` and `GEMINI.md` as lightweight imports of this file, not copies.

## Project Overview

Blinkz is an Android app for tracking blinks through the front camera to help with dry eye prevention. Tracking works while the full app is open and while the activity is in Picture in Picture mode.

The app uses Kotlin, Jetpack Compose, MVIKotlin, Decompose, Room, DataStore, CameraX, ML Kit Face Detection, Vico, Firebase Crashlytics/Performance, and Timber. Dependency versions are centralized in `gradle/libs.versions.toml`.

Use JDK 21. The Android app module targets `compileSdk = 36`, `minSdk = 26`, and `targetSdk = 36`. Library modules currently use `compileSdk = 34`.

## Required Validation

For any code change, run:

```powershell
.\gradlew.bat assembleDebug
.\gradlew.bat detekt
```

On macOS/Linux, use `./gradlew assembleDebug` and `./gradlew detekt`.

`assembleDebug` must pass before the final `detekt` run. The final validation step is always `detekt`; fix all new findings before finishing. CI runs the custom `runOnGitHub` Gradle task, which executes `detekt` and `assembleDebug`.

Screenshot tests for Compose previews live in `sources/ui` and use Paparazzi with generated tests from ComposablePreviewScanner. When a change affects UI design, layout, theme, typography, colors, icons, strings visible in previews, chart rendering, or preview data, update the screenshot baseline:

```powershell
.\gradlew.bat :sources:ui:recordPaparazziDebug
.\gradlew.bat :sources:ui:verifyPaparazziDebug
```

On macOS/Linux, use `./gradlew :sources:ui:recordPaparazziDebug` and `./gradlew :sources:ui:verifyPaparazziDebug`. Commit the updated PNG files under `sources/ui/src/test/snapshots/images` when the visual changes are intentional. If a UI-related change should not affect screenshots, run `verifyPaparazziDebug` and investigate any diff instead of recording over it.

There are currently no dedicated unit or instrumentation test source sets in the repo. Add focused tests when changing logic with meaningful branching or persistence behavior, but still run the required Gradle tasks above.

## Repository Layout

- `app`: Android application entry point, manifest, Firebase initialization, `MainActivity`, app-level tools.
- `sources:root`: Decompose root component and screen stack navigation.
- `sources:settings`: DataStore-backed app settings.
- `sources:database`: Room database, DAO, repository, database models.
- `sources:ui`: Compose UI, theme, CameraX/ML Kit camera pipeline, Vico chart UI.
- `sources:components:home`: Aggregates camera, tracker, and statistic child components.
- `sources:components:camera`: Camera permission/lens state component and store.
- `sources:components:tracker`: Blink tracking state machine and Picture in Picture bridge.
- `sources:components:preferences`: Settings screen state machine and overlay permission flow.
- `sources:components:statistic`: Statistics state machine, Room persistence bridge, aggregation for chart periods.
- `detekt`: Detekt config, baseline, and generated report location.
- `distribution`: changelog and Play Store "what's new" text.

## Architecture

The app follows a Decompose + MVIKotlin structure:

- Public component interfaces live near the component root, for example `BlinkTracker`, `BlinkCamera`, `BlinkStatistic`, `BlinkPreferences`.
- Concrete Decompose components live under `integration`.
- MVIKotlin stores live under `store` and should remain `internal`.
- Store state is mapped to public UI models through `Mappers.kt`.
- One-off effects are emitted as store labels and converted to component outputs.
- Components use `instanceKeeper.getStore { ... }` so stores survive component recreation.
- Components that collect labels create a `CoroutineScope(Dispatchers.Main)` and cancel it with `lifecycle.doOnDestroy(scope::cancel)`.

Prefer extending this pattern instead of introducing ViewModels or direct state mutation in Compose.

## Navigation

`BlinkRootComponent` owns the Decompose stack:

- Initial screen: `Configuration.Home`.
- Secondary screen: `Configuration.Preferences`.
- `openPreferencesScreen()` pushes preferences.
- `closePreferencesScreen()` pops preferences.
- Root forwards camera permissions, face data, PiP state, and overlay permission callbacks to the active child components.

`BlinkHomeComponent` owns three stable child contexts:

- `camera` -> `BlinkCameraComponent`
- `tracker` -> `BlinkTrackerComponent`
- `statistic` -> `BlinkStatisticComponent`

Tracker outputs are routed through `BlinkHomeComponent`: sound/vibration labels call `NotificationsManager`, errors go to `ErrorHandler`, and `BlinkedPerMinute` is forwarded to statistics.

## App Runtime

`MainActivity` is the composition root. It:

- Creates `FaceDetectorProcessor` with ML Kit face detector options.
- Creates `AppSettings`, `StatisticsRepositoryReal`, `AppErrorHandler`, and `AppNotificationsManager`.
- Creates `BlinkRootComponent` with `DefaultStoreFactory`.
- Collects `imageProcessor.faceData` and calls `root.onFaceDataChanged(...)`.
- Collects minimized opacity from settings and applies it to the activity window while in PiP.
- Checks camera permission in `onStart`.
- Registers `AppUnlockReceiver` for screen/user-present events.
- Implements `PictureInPictureLauncher`.
- Implements `OverlayPermissionChecker`.

Do not move Android framework APIs into pure component modules unless you also introduce a small interface boundary like `PictureInPictureLauncher`, `OverlayPermissionChecker`, `NotificationsManager`, or `ErrorHandler`.

## Face Detection And Blink Tracking

Face detection is split across two layers:

- CameraX/ML Kit processing lives in `sources/ui/.../camera`.
- Blink counting state lives in `sources/components/tracker`.

`CameraPreviewComposable` creates a `PreviewView`, builds a `CameraSelector` from `CameraLens`, and binds CameraX use cases through `Context.bindCameraUseCases(...)`.

`bindCameraUseCases(...)` binds:

- `Preview` for the visible camera preview.
- `ImageAnalysis` at `640x480`.
- Analyzer on the main executor, delegating to `VisionImageProcessor.process(imageProxy)`.

`FaceDetectorProcessor`:

- Uses ML Kit `FaceDetection.getClient(...)`.
- Requires `FaceDetectorOptions.CLASSIFICATION_MODE_ALL`; eye-open probabilities depend on classification.
- Uses `PERFORMANCE_MODE_FAST`.
- Emits `VisionFaceData(leftEye, rightEye, faceAvailable)` through a `MutableStateFlow`.
- Uses only the first detected face.
- Sets `faceAvailable = true` only when both eye probabilities are non-null.
- Rounds eye probabilities to 4 decimals.
- Emits empty data when no face is found or processing fails.
- Closes each `ImageProxy` in the ML Kit completion listener.
- Calls `detector.close()` and shuts down `ScopedExecutor` from `stop()`.

Low-light handling in `FaceDetectorProcessor` checks image luma at most once per second. If average luma is at or below `105.0`, it converts the input to an upright bitmap and applies brightness/contrast before ML Kit processing. Preserve this path carefully because missed `ImageProxy.close()` calls will stall CameraX analysis.

Blink counting is in `BlinkTrackerStoreProvider`:

- A blink is registered only when tracking is active.
- Both `leftEye` and `rightEye` must be non-null and lower than `0.25f`.
- A 500 ms cooldown prevents one eye closure from being counted repeatedly.
- A timer ticks every 1000 ms while active.
- Every 60 seconds, the store publishes the last-minute blink count, optionally triggers sound/vibration if the count is below the configured threshold, then resets the minute counter.
- `blinksTotal` is session-local store state, not persisted.
- Persisted statistics receive one value per completed minute through the `BlinkedPerMinute` output.

When changing blink detection, update both the tracker store and this guide. Be explicit about thresholds, time windows, and whether persistence semantics changed.

## Picture In Picture And Minimized Mode

The manifest enables PiP on `MainActivity` and handles relevant configuration changes.

PiP flow:

- Pressing minimize in UI calls `BlinkTracker.onMinimizeRequested()`.
- The tracker store emits `OnLaunchPip` to the `PictureInPictureLauncher`.
- `MainActivity.launchPictureInPicture()` enters PiP with aspect ratio `3:4`.
- `onPictureInPictureModeChanged(...)` forwards minimized state to the tracker and applies opacity.
- `onUserLeaveHint()` also enters PiP unless the overlay settings screen is currently visible.

UI minimized mode is driven by `BlinkTracker.Model.isMinimized`. In minimized mode, `MainScreenMinimized` shows timer and current per-minute count, and uses a different background when no face data is available.

Settings:

- `launch_minimized_enabled` can automatically enter PiP when tracking starts.
- `minimized_opacity` controls window alpha in PiP.

Be careful not to confuse Android PiP state with the tracker model flag; the UI flag is updated from `onPictureInPictureModeChanged(...)`.

## Permissions And Auto-start

Camera permission is checked in `MainActivity.onStart()`:

- Granted -> `root.onPermissionGranted()` and front-camera availability check.
- Denied with rationale -> `root.onPermissionRationale()`.
- Otherwise -> request `Manifest.permission.CAMERA`.

Camera availability currently accepts only `PackageManager.FEATURE_CAMERA_FRONT`. If no front camera is found, `CameraLens.NOT_AVAILABLE` is sent and the UI shows the no-camera state.

Auto-start uses `SYSTEM_ALERT_WINDOW`:

- Preference changes are handled by `BlinkPreferencesStoreProvider`.
- Enabling auto-start without overlay permission shows the rationale.
- Agreeing opens `ACTION_MANAGE_OVERLAY_PERMISSION`.
- On resume from settings, the preference is kept only if permission was granted.
- `AppUnlockReceiver` launches `MainActivity` after `BOOT_COMPLETED` or `USER_PRESENT` if auto-start is enabled and overlay permission is granted.

The manifest also declares `SCREEN_ON`; the current receiver implementation does not launch on that action. If you change that behavior, update this guide and check background activity start restrictions on modern Android versions.

## Settings

`Settings` is an interface backed by `AppSettings` and DataStore Preferences.

Preference keys:

- `per_minute_threshold`
- `notify_sound_enabled`
- `notify_vibration_enabled`
- `launch_minimized_enabled`
- `auto_start_enabled`
- `minimized_opacity`

Defaults:

- Threshold: `12f` blinks per minute.
- Sound: `false`.
- Vibration: `true`.
- Launch minimized: `false`.
- Minimized opacity: `0.7f`.
- Auto-start: `false`.

`AppSettings.Store` keeps a singleton DataStore instance. Use the `Settings` interface in components and tests.

## Statistics Persistence And Aggregation

`StatisticsRepositoryReal` uses Room database `blink_tracker.db`, version `1`, with `fallbackToDestructiveMigration()`.

Room table:

- Entity: `BlinksRecordDbModel`
- Table: `stats`
- Columns: `id`, `blinks`, `date`
- `date` is `kotlinx.datetime.LocalDateTime` converted to/from `String`.

DAO:

- `insert(...)` uses `OnConflictStrategy.REPLACE`.
- `get()` returns `Flow<List<BlinksRecordDbModel>>` without an explicit SQL order.

Statistics flow:

- `BlinkTrackerStoreProvider` emits one `BlinkedPerMinute(value)` label every completed minute.
- `BlinkHomeComponent` forwards it to `BlinkStatisticComponent.onNewBlinksValue(value)`.
- `BlinkStatisticStoreProvider` calls `StatisticsManager.saveBlinks(value)`.
- `StatisticsManagerImpl` inserts the value with the current local date-time.
- `StatisticsManagerImpl` combines repository data with the selected `DisplayedPeriod`.

Displayed periods:

- `MINUTE`: last 60 records, one record per completed minute.
- `QUARTER_HOUR`: last 30 groups split by gaps greater than 15 minutes.
- `HOUR`: last 24 groups split by gaps greater than 60 minutes.
- `DAY`: last 30 calendar-day groups.
- `MONTH`: last 12 calendar-month groups.

The chart uses Vico `ChartEntry` values through `CustomChartEntry`, with labels generated in `StatisticsManagerImpl`.

If persistence semantics change, check database migration, DAO ordering, aggregation, chart labels, and UI period chips together.

## Compose UI

Compose UI lives in `sources/ui`.

Patterns to preserve:

- Public screen entry points collect component `Flow<Model>` with `collectAsState(initial = component.initial)`.
- Pure screen composables receive models and callbacks.
- Preview stubs live in `ui/preview/PreviewStubs.kt`.
- Root navigation uses Decompose `Children` with stack animation.
- Camera preview uses `AndroidView` around CameraX `PreviewView`.
- Theme tokens live in `ui/theme`.
- Shared UI pieces live in `ui/component`.
- Strings live in `sources/ui/src/main/res/values` and `values-ru`.

Avoid putting business logic into composables. Route user actions back into component methods and stores.

## Code Style

Follow existing Kotlin style and module boundaries.

- Prefer constructor-injected interfaces at component boundaries.
- Keep stores, actions, messages, and reducers close together.
- Keep `State` immutable and derive public UI models through mappers.
- Use `Flow` for observable state and labels/outputs for one-off effects.
- Keep Android framework classes out of component modules unless behind a small interface.
- Prefer explicit constants for thresholds, timing, dimensions, and repeated values.
- Do not add `TODO`, `FIXME`, or `STOPSHIP` comments; detekt forbids them.
- Avoid broad suppressions. If a suppression is necessary, keep it as local as possible and match existing style.
- Keep lines at or below detekt's 140 character limit.
- Use resources for user-facing text.
- Preserve `android.nonTransitiveRClass=true`; import the correct module `R` where needed.

## Detekt Notes

The project builds on default detekt config with `maxIssues = 0`, a baseline, and custom rules in `detekt/base-config.yml`.

Rules that commonly matter here:

- `ForbiddenComment` rejects `TODO`, `FIXME`, `STOPSHIP`.
- `MagicNumber` is active outside allowed contexts.
- `LongMethod`, `ComplexMethod`, and `CyclomaticComplexMethod` are active.
- `TooGenericExceptionCaught` and `PrintStackTrace` are active.
- Coroutine `InjectDispatcher` is active.
- `ModifierOrder` is active for Compose.
- `MaxLineLength` is 140.
- `NewLineAtEndOfFile` is active.

Do not update `detekt/baseline.xml` to hide new problems unless explicitly asked.

## Build And Release

Debug APK output is configured with `archivesBaseName = applicationId`.

Release signing uses `app/release.jks` if present and reads passwords from `local.properties`. Do not commit signing material or local credentials.

Firebase config exists in `app/google-services.json`; be careful with changes to application id, Firebase plugins, or release packaging.

Release build enables resource shrinking and minification. If changing ML Kit, CameraX, Room, or serialization behavior, consider release/proguard impact.

## Agent Files

`AGENTS.md` is the canonical file. `CLAUDE.md` and `GEMINI.md` should contain:

```text
@AGENTS.md
```

Do not use symlinks for these files; symlink creation and checkout behavior differ across Windows, macOS, and Linux.
