# Experiment 6 - Google Maps Location Tracking

Android Studio project (Java + XML + Gradle) that shows live location on Google Maps, draws the travelled path
(Polyline) and displays latitude, longitude, accuracy and distance.

> Status: source code written for this practical. It has **not** been compiled or run by the author of this package
> (no Android SDK / Google Maps access was available when it was generated). Screenshots in `Output_Screenshots`
> are placeholders - replace them with your own.

## Folder contents
| Item | Description |
|------|-------------|
| `AndroidStudioProject/` | Complete Gradle project - open this folder in Android Studio |
| `Java_Files/` | Copy of the Java source (MainActivity.java) |
| `XML_Files/` | Copy of manifest, layout and values XML files |
| `Output_Screenshots/` | Placeholder PNGs - replace with real screenshots |
| `Practical_Writeup.docx` | Write-up (12 headings) |

## Requirements
Android Studio (Koala/Ladybug or newer), JDK 17 (bundled), Android SDK 34, internet connection,
phone with GPS or emulator (Google APIs / Google Play image).

## 1. Get a Google Maps API key
1. Go to https://console.cloud.google.com and create/select a project.
2. APIs & Services > Library > enable **Maps SDK for Android**.
3. APIs & Services > Credentials > **Create credentials > API key**.
4. (Recommended) Restrict the key to *Android apps*: package `com.example.maptracker` + your debug SHA-1
   (`gradlew signingReport` or Gradle panel > android > signingReport).
5. Link a billing account (required by Google; a free monthly credit applies).
6. Open `AndroidStudioProject/app/src/main/res/values/google_maps_api.xml` and replace `YOUR_API_KEY_HERE`.

Do not commit or share your real key.

## 2. Open and run
1. Unzip, then **File > Open** and choose the `AndroidStudioProject` folder.
2. Let Gradle sync finish (Android Studio creates `local.properties` with your SDK path automatically).
3. Choose a device and press **Run**.
4. Tap **Start Tracking**, allow location permission.
   - Emulator: *... > Location* in the emulator toolbar, set a point or play a GPX/route.
   - Phone: go outdoors and walk; the blue line shows the path.
5. Use **Stop Tracking**, **My Location** and **Clear Path** to test the other buttons.

## Command line (optional)
```
cd AndroidStudioProject
./gradlew assembleDebug        # Windows: gradlew.bat assembleDebug
```
APK output: `app/build/outputs/apk/debug/app-debug.apk`

## Troubleshooting
- Grey map: check API key, enabled API, billing, restrictions, Logcat tag `Google Android Maps SDK`.
- No updates on emulator: send a location from the emulator's Location panel first.
- Sync errors: File > Sync Project with Gradle Files; check internet; use JDK 17 (Settings > Build Tools > Gradle).

## Limitations
- Requires your own API key and internet; cannot be tested without them.
- Tracking runs while the app is open (no background/foreground service).
