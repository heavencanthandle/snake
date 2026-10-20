# Snake

@AGENTS.md

The parent folder's `../CLAUDE.md` has the cross-app rules. Snake talks to no other app and does
not feed Glance.

## Build, test, phone

- `.\gradlew.bat :app:testDebugUnitTest :app:assembleDebug`. Tests are in
  `app/src/test/kotlin/.../game/`.
- Phone: debug build (this PC's debug key). Raise `versionCode` (in `app/build.gradle.kts`), then
  `adb -d install -r app\build\outputs\apk\debug\app-debug.apk`.
- No release keystore. Don't create one without asking.

## Conventions

- Sources are in `app/src/main/kotlin` (not `java`), packages `game` (pure logic) and `ui`.
- No permissions, by design.
- Board size (`COLUMNS`, `ROWS`) and the tick (`TICK_MS`) are in `GameViewModel`.
