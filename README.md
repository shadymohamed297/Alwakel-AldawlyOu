# CODING AGENTS: READ THIS FIRST

This is a **handoff bundle** from Claude Design (claude.ai/design).

A user mocked up designs in HTML/CSS/JS using an AI design tool, then exported this bundle so a coding agent can implement the designs for real.

## What you should do — IMPORTANT

**Read the chat transcripts first.** There are 1 chat transcript(s) in `chats/`. The transcripts show the full back-and-forth between the user and the design assistant — they tell you **what the user actually wants** and **where they landed** after iterating. Don't skip them. The final HTML files are the output, but the chat is where the intent lives.

**Read `project/مركز الصيانة.dc.html` in full.** The user had this file open when they triggered the handoff, so it's almost certainly the primary design they want built. Read it top to bottom — don't skim. Then **follow its imports**: open every file it pulls in (shared components, CSS, scripts) so you understand how the pieces fit together before you start implementing.

**If anything is ambiguous, ask the user to confirm before you start implementing.** It's much cheaper to clarify scope up front than to build the wrong thing.

## About the design files

The design medium is **HTML/CSS/JS** — these are prototypes, not production code. Your job is to **recreate them pixel-perfectly** in whatever technology makes sense for the target codebase (React, Vue, native, whatever fits). Match the visual output; don't copy the prototype's internal structure unless it happens to fit.

**Don't render these files in a browser or take screenshots unless the user asks you to.** Everything you need — dimensions, colors, layout rules — is spelled out in the source. Read the HTML and CSS directly; a screenshot won't tell you anything they don't.

## Bundle contents

- `README.md` — this file
- `chats/` — conversation transcripts (read these!)
- `project/` — the `نظام مركز الصيانة المتكامل` project files (HTML prototypes, assets, components)

## Implementation

The 8 screens from `project/مركز الصيانة.dc.html`, plus 5 more the design's own bottom nav
referenced but never designed (الطلبات، العملاء، الفنيون، التقارير، أدائي — built to match the
same brand rather than left as dead ends), are implemented as a real full-stack app, ready for
a maintenance center to actually run day to day — not published to Google Play, just installed
directly on staff phones:

- `backend/` — Node.js + Express + PostgreSQL REST API (auth, work orders, customers,
  technicians, inventory, dashboards, reports/CSV export). See `backend/README.md` for setup
  and the full API reference, and its "Production notes" section for the crash-safety and
  startup-guard fixes made during development.
- `android/` — Native Android app (Kotlin + Jetpack Compose + Material 3 + Hilt + Retrofit),
  Arabic RTL only, using the real IBM Plex Sans Arabic font and the brand colors/tokens
  extracted from the design file. The backend URL is configurable on-device (no rebuild needed
  after deploying). See `android/README.md` for setup and what's still a known gap.
- `DEPLOY.md` — free hosting walkthrough (Neon + Render, no credit card, no server to manage) so
  the backend runs somewhere real phones can reach, not just `localhost`.
- `.github/workflows/` — CI for both halves: `backend-ci.yml` spins up a real Postgres and
  smoke-tests every endpoint; `android-build.yml` builds (and lints) the debug APK and attaches
  it as a downloadable artifact — this exists because the sandbox this was built in has no
  Android SDK and no network access to Google's Maven/download servers (confirmed org policy,
  not transient), so a real `./gradlew` build could never be run there.

Every backend endpoint was verified end-to-end with curl during development, including
deliberately killing Postgres mid-request to confirm the server survives instead of crashing.
The Android code was cross-checked symbol-by-symbol (every import, DTO field, repository call,
and nav argument diffed against its definition) since a real Gradle build wasn't possible in
that sandbox — see `android/README.md` for exactly what that did and didn't catch, and why a
real build in Android Studio or CI is still the thing that actually proves it compiles.
