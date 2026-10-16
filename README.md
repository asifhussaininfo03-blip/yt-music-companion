# YT Music Companion — Phase 1 (app shell)

Personal companion app. Phase 1 only: dark UI + accent picker + official
YouTube Music launcher + settings. No local player yet (Phase 3),
no equalizer yet (Phase 5).

Compliance: launcher-only. No YouTube audio extraction, no ad blocking,
no downloads, no WebView Google login, no password storage, no backend.

## Folder

`C:\xampp\htdocs\music-app` — kept as-is per your project.
Tip for later: Android projects usually live outside `htdocs`
(e.g. `C:\dev\yt-music-companion`) to avoid XAMPP interference.

## Install toolchain (once, beginner steps)

1. Install JDK 17 LTS (Temurin or Oracle). Check: `java -version`
2. Install Android Studio (Ladybug or newer).
3. In Studio: SDK Manager → install Platform 34 + Build-Tools 34.
4. Open THIS folder in Android Studio → let it sync Gradle.
5. Run: `.\gradlew.bat :app:assembleDebug` (first sync downloads deps).

## Test Phase 1

```powershell
.\gradlew.bat :app:testDebugUnitTest
.\gradlew.bat :app:assembleDebug
adb install -r app\build\outputs\apk\debug\app-debug.apk
```

Manual check:
- Dashboard shows “Open YouTube Music”.
- With YT Music installed → official app opens.
- Without → Play Store opens, then website fallback.
- Accent dots change theme, persist after restart (DataStore).
- Settings toggle saves dynamic-color flag.

## What is NOT verified yet

This environment has no JDK/SDK, so `./gradlew` was NOT run here.
Status: files created, static checks passed, build pending on your PC
with Android Studio.

Next phases: 3 = ExoPlayer local player, 4 = Room playlists,
5 = Equalizer/BassBoost with fallback.
