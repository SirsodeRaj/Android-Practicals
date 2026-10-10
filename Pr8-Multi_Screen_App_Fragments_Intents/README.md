# Experiment 8 - Multi-Screen App using Fragments and Intents

Android Studio project (Java + XML + Gradle).

* **Fragments:** Home, Profile Form, About - switched with a BottomNavigationView in `MainActivity`.
* **Explicit Intent:** Form -> `DetailActivity` with extras (name, email, course); `DetailActivity` returns a result.
* **Implicit Intents:** open website, share text, dialer, compose email.
* **Fragment communication:** `SharedViewModel` + LiveData (Form -> Home).

> Status: source code written for this practical. It has **not** been compiled or run by the author of this package
> (no Android SDK was available). Screenshots in `Output_Screenshots` are placeholders - replace them with your own.

## Folder contents
| Item | Description |
|------|-------------|
| `AndroidStudioProject/` | Complete Gradle project - open this folder in Android Studio |
| `Java_Files/` | All Java sources |
| `XML_Files/` | Manifest, layouts, menu, drawables, values |
| `Output_Screenshots/` | Placeholder PNGs |
| `Practical_Writeup.docx` | Write-up (12 headings) |

## Requirements
Android Studio (recent stable), JDK 17 (bundled), Android SDK 34, device or emulator (API 24+).

## Run
1. Unzip, **File > Open** > select `AndroidStudioProject`.
2. Wait for Gradle sync.
3. Press **Run**.
4. Use the tabs: Home / Profile Form / About.
5. In *Profile Form* enter name, a valid email and course > **Submit** > **Confirm and Return**. Open *Home* to see the stored profile.

## Troubleshooting
- Inflate errors for BottomNavigationView: keep the Material theme and dependency.
- Browser / email / share actions need corresponding apps on the device (emulators may lack an email app).
- Sync problems: check internet and JDK 17; File > Sync Project with Gradle Files.
