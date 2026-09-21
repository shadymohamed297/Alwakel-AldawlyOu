# Maintenance Center — Android app

Native Android client for نظام مركز صيانة متكامل (Kotlin + Jetpack Compose + Material 3),
Arabic RTL only, targeting the backend in `../backend`.

## Stack

- Kotlin, Jetpack Compose, Material 3
- Hilt (DI), Navigation Compose
- Retrofit + OkHttp + kotlinx.serialization
- DataStore Preferences (JWT storage)
- IBM Plex Sans Arabic (bundled under `app/src/main/res/font`, SIL OFL licensed — see
  `app/src/main/assets/licenses/IBMPlexSansArabic-OFL.txt`)

## Setup

1. Run the backend first (see `../backend/README.md`), or deploy it for free per `../DEPLOY.md`.
2. Open `android/` in Android Studio (Koala+) and let it sync.
3. `BuildConfig.BASE_URL` is only the *initial* value (`http://10.0.2.2:3000/`, the standard
   Android-emulator alias for the host machine's `localhost`). The real server address is stored
   on-device via DataStore and can be changed any time from the small "عنوان السيرفر" link under
   the login card — tap it, paste a URL, save. No rebuild needed to point the app at a different
   backend (e.g. after deploying to Render).
4. Run the `app` configuration. Log in with any seeded account (see backend README), password
   `Passw0rd!`.

## CI build (no local setup needed)

`.github/workflows/android-build.yml` builds the debug APK and runs lint on every push/PR that
touches `android/`, and can also be triggered manually from the Actions tab ("Run workflow").
This exists specifically because the sandbox this app was built in has no Android SDK and no
network access to Google's Maven/download servers (an explicit org network policy — confirmed,
not transient), so a real build could not be verified there. Push this repo to GitHub and the
first run will tell you definitively whether it compiles; the finished APK is attached as a
downloadable workflow artifact ("markaz-sayana-debug-apk") — install it straight on a device or
emulator without needing Android Studio at all.

## Screens

The 8 screens from `project/مركز الصيانة.dc.html`:

| # | Screen | Role |
|---|--------|------|
| 1 | Reception dashboard | Reception |
| 2 | New request intake (customer + device) | Reception |
| 3 | Assign technician & schedule | Reception |
| 4 | Technician's daily tasks | Technician |
| 5 | Work order execution (checklist, parts) | Technician |
| 6 | Invoice + customer signature/close-out | Technician |
| 7 | Manager performance dashboard | Manager |
| 8 | Inventory / spare parts | Manager (+ shared) |

Plus 5 screens the bottom nav referenced but that had no original design (built to match the
same brand/components rather than left as placeholders, per the shop's request):

| Screen | Role | Notes |
|---|---|---|
| الطلبات (all work orders) | Reception | Search + status filter, tap a row for a read-only detail dialog |
| العملاء (customers) | Reception | Search → tap a customer for their device list + full order history |
| الفنيون (technician roster) | Manager | Every technician with today's load, monthly completions, rating |
| التقارير (reports) | Manager | Totals + breakdown by technician/branch/device, period tabs, **CSV export** (shares the file via a real Android share sheet) |
| أدائي (my performance) | Technician | Personal stats + recent closed jobs, mirrors the manager dashboard's style |

## What was and wasn't verified

This sandbox has **no Android SDK and no access to `maven.google.com` / `dl.google.com`**
(outbound network policy blocks them — confirmed via direct `curl`, HTTP 403 through the proxy).
That means the Android Gradle Plugin itself can't be fetched, so a real `./gradlew assembleDebug`
could not be run here.

To compensate, every file was cross-checked programmatically instead of just by eye:
- every `import com.markazsayana.app.ui.theme.*` / `...util.*` / `...ui.components.*` symbol was
  diffed against what's actually declared in those packages — no missing symbols
- every DTO field accessed from a screen was diffed against its `data class` in `Dto.kt`
- every repository method call was checked against its declared signature
- every `SavedStateHandle` key read in a ViewModel was checked against the matching
  `navArgument(...)` name in `NavGraph.kt`
- every `.kt` file's `package` declaration was checked against its directory path
- brace/paren balance was checked file-by-file

Real bugs were caught and fixed this way, not just hypothetical risk: `remember { … }` called
from plain, non-`@Composable` `Modifier` extension functions (invalid Kotlin/Compose) in two
early files, later replaced with the standard `Modifier.clickable(...)` overload everywhere.

This is **not a substitute for an actual build**. Before shipping, run a real
`./gradlew assembleDebug` (or open in Android Studio) in an environment with the Android SDK,
and smoke-test the golden path in an emulator/device against the running backend.

## Signing: debug build, on purpose

This app is for the maintenance center's own staff, installed directly on their phones — it's
never going to Google Play. A debug build is already signed (with Android's auto-generated debug
key) and installs and runs identically to a release build for that use case; the only real
difference is a somewhat larger APK and no R8 shrinking. Setting up a real release keystore adds
secret-management complexity (where the key lives, who can rotate it) for no practical benefit
here, so it was deliberately skipped. If this ever needs proper release signing (e.g. to publish
in-place updates through some other channel), generate a keystore with `keytool`, wire it into
a `release` `signingConfig` in `app/build.gradle.kts`, and store the keystore + passwords as
GitHub Actions secrets rather than committing them.

## Known gaps / simplifications

- The customer signature pad on screen 6 is a real freehand `Canvas` drawing surface for visual
  fidelity, but the strokes aren't persisted (no image upload endpoint on the backend) — the
  actual "signed" record sent to the API is the typed name field beneath it.
- "مسح باركود" (barcode scan) and "إضافة صور قبل/بعد" (before/after photos) are visually present
  but inert — no camera/upload backend exists yet.
- Reopened work orders, inventory reorder emails, and WhatsApp delivery mentioned in the design
  copy are not implemented — those are backend-side integrations out of scope for this pass.
