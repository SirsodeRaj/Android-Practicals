# Experiment 7 - Camera Image Capture and Internal Storage

Android Studio project (Java + XML + Gradle). Captures a photo with the device camera app and saves it to the app's
**internal storage** (`getFilesDir()/images`). Saved photos are shown in a grid; tap to preview, long-press to delete.

> Status: source code written for this practical. It has **not** been compiled or run by the author of this package
> (no Android SDK was available). Screenshots in `Output_Screenshots` are placeholders - replace them with your own.

## Folder contents
| Item | Description |
|------|-------------|
| `AndroidStudioProject/` | Complete Gradle project - open this folder in Android Studio |
| `Java_Files/` | MainActivity.java, ImageAdapter.java, ImageUtils.java |
| `XML_Files/` | Manifest, layouts, values, file_paths.xml |
| `Output_Screenshots/` | Placeholder PNGs - replace with real screenshots |
| `Practical_Writeup.docx` | Write-up (12 headings) |

## Requirements
Android Studio (recent stable), JDK 17 (bundled), Android SDK 34, a phone with camera or an emulator whose camera is
set to *Virtual Scene* or *Webcam0* (Device Manager > edit AVD > Show Advanced Settings > Camera).

## Run
1. Unzip, then **File > Open** and select `AndroidStudioProject`.
2. Wait for Gradle sync (Android Studio creates `local.properties` automatically).
3. Select a device and press **Run**.
4. Tap **Capture Photo**, take a picture and accept it. The image appears in the grid and its path is displayed.
5. Verify storage: **View > Tool Windows > Device Explorer > data/data/com.example.camerastorage/files/images**.

## Notes
- No CAMERA permission is needed because the system camera app is launched through an intent.
- Images are private to the app and are removed on uninstall.

## Troubleshooting
- "No camera app found": configure the emulator camera or use a physical device.
- `Failed to find configured root`: check `res/xml/file_paths.xml` and the provider authority.
- Gradle errors: check internet, JDK 17 and File > Sync Project with Gradle Files.
