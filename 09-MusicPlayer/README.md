# Experiment 9 - Background Music Player using Services

Android Studio project (Java + XML + Gradle). A **foreground Service** owns a `MediaPlayer` and keeps playing when
the app is minimised or closed. The Activity binds to the service for seek/progress/pause/stop, and a notification
provides Pause/Play/Stop buttons.

> Status: source code written for this practical. It has **not** been compiled or run by the author of this package
> (no Android SDK / audio device was available). Screenshots in `Output_Screenshots` are placeholders.

## Folder contents
| Item | Description |
|------|-------------|
| `AndroidStudioProject/` | Complete Gradle project - open this folder in Android Studio |
| `Java_Files/` | MainActivity.java, MusicService.java |
| `XML_Files/` | Manifest, layout, drawable, values |
| `Output_Screenshots/` | Placeholder PNGs |
| `Practical_Writeup.docx` | Write-up (12 headings) |

## Audio file
`app/src/main/res/raw/sample_music.wav` is a short synthesised melody (Ode to Joy, a public-domain tune) generated for
this project. To use your own song, replace it with a file of the same base name (`sample_music.mp3`, `.ogg` or `.wav`;
delete the old one so only one `sample_music.*` exists).

## Run
1. Unzip, **File > Open** > select `AndroidStudioProject`; wait for Gradle sync.
2. Run on a device/emulator (API 24+). On Android 13+ tap **Allow** for notifications.
3. Press **Play**; press Home or open another app - music continues.
4. Pull down the notification and use **Pause / Play / Stop**; tap the notification to return to the app.

## Troubleshooting
- No notification: enable notifications for the app (Android 13+).
- Crash on Android 14: make sure manifest has `foregroundServiceType="mediaPlayback"` and the matching permission (already included).
- No sound on emulator: raise emulator volume / check host audio.
- Sync errors: check internet and JDK 17.
