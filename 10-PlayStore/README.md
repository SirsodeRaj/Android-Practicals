# Experiment 10 - Signed APK and Test Play Store Deployment

Android Studio project (Java + XML + Gradle): a small **Bill Splitter** app configured for release signing, plus a
step-by-step guide to build a signed APK/AAB and publish it to a Google Play **internal testing** track.

> Status: source code and instructions written for this practical. The project has **not** been compiled, signed,
> uploaded or published by the author of this package (no Android SDK, keystore or Play Console account was available).
> **No private credentials are included.** Screenshots in `Output_Screenshots` are placeholders.

## Folder contents
| Item | Description |
|------|-------------|
| `AndroidStudioProject/` | Gradle project - open in Android Studio |
| `Java_Files/`, `XML_Files/` | Copies of Java and XML sources |
| `Output_Screenshots/` | Placeholder PNGs |
| `Practical_Writeup.docx` | Write-up (12 headings) |

Inside the project: `keystore.properties.example`, `generate_keystore.sh` / `.bat`, `app/build.gradle` (signing + R8).

## Step 1 - Make the app yours
In `app/build.gradle` change `applicationId` to a unique name (e.g. `com.yourname.billsplitter`; `com.example.*` is rejected by
Play) and also (optionally) the Java package. Increase `versionCode` for every new upload.

## Step 2 - Create a keystore (once)
Option A - Android Studio: **Build > Generate Signed App Bundle / APK > Create new...**
Option B - command line (from the project folder): `sh generate_keystore.sh` (Windows: `generate_keystore.bat`).
Keep the `.jks` file and passwords private and backed up. Never upload them anywhere.

## Step 3 - Build the signed release
**Wizard:** Build > Generate Signed App Bundle / APK > choose APK or Android App Bundle > select keystore > *release* > Finish.

**Gradle:** copy `keystore.properties.example` to `keystore.properties`, fill in your values, then
```
./gradlew assembleRelease     # APK -> app/build/outputs/apk/release/app-release.apk
./gradlew bundleRelease       # AAB -> app/build/outputs/bundle/release/app-release.aab
```
(Windows: `gradlew.bat`.) Without `keystore.properties` Gradle produces an *unsigned* APK.

Verify / install (optional):
```
apksigner verify --verbose app-release.apk      # from <sdk>/build-tools/<version>/
adb install app-release.apk
```

## Step 4 - Publish to a test track (Google Play Console)
1. Create / verify a developer account at https://play.google.com/console (one-time registration fee; identity verification).
2. **Create app**: name, default language, App or Game, Free or Paid, accept declarations.
3. Complete *Set up your app*: privacy policy URL, app access, ads, content rating, target audience, data safety.
4. **Test and release > Testing > Internal testing > Create new release.**
   Accept *Play App Signing*, upload `app-release.aab`, add release notes, *Save > Review release > Start rollout*.
   (New apps must be uploaded as an **AAB**; the signed APK is for direct installation/sideloading.)
5. **Testers** tab: create an email list with the testers' Google accounts and copy the **opt-in link**.
6. Testers open the link, accept, and install from the Play Store. Check the version shown in the app.

Policies for new developer accounts (e.g. closed-testing duration/tester count before Production) change over time -
read the current Play Console Help before planning a production release.

## Limitations
- Signing and publishing require **your** keystore and Play Console account; this could not be done or tested here.
- Do not commit `keystore.properties`, `*.jks` or `*.keystore` (already in `.gitignore`).
