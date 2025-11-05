# ImageTOTextAndroidApp

ImageTOTextAndroidApp is an Android app (Jetpack Compose) that captures images, allows simple cropping/editing, and extracts text from images (OCR). The project uses modern Android libraries such as CameraX, Jetpack Compose, Hilt, Coil, Room, and ML Kit / Text Recognition.

> This README was generated from the project files. It summarizes detected features and provides setup and troubleshooting guidance.

## Key features

- Camera capture using CameraX (Preview + ImageCapture + Lifecycle integration)
- Crop UI integration (uCrop)
- OCR / Text Recognition (ML Kit or similar text recognition library)
- Image loading with Coil (Compose integration)
- Local persistence with Room (stores previous extracted text)
- Dependency injection with Hilt
- Jetpack Compose UI and Navigation
- AdMob integration (play-services-ads)
- Project modularization: includes a local `:sdk` module

## Technologies & Libraries (detected)

- Kotlin + Compose (Compose BOM)
- CameraX (core, camera2, lifecycle, view)
- Hilt (DI) + Hilt Navigation for Compose
- Coil (coil-compose)
- uCrop (image cropping)
- Room (room-runtime / room-ktx)
- ML Text Recognition (text recognition dependency detected)
- AdMob (play-services-ads)

(See `app/build.gradle.kts` for exact dependency declarations.)

## Project structure (recommended overview)

- `app/src/main/java/.../ui/screen/` — Composables for screens (Camera, Crop, ImagePreview, ExtractedText, etc.)
- `app/src/main/java/.../viewmodel/` — ViewModels (MVVM)
- `app/src/main/java/.../ui/navigation/` — Navigation host and graph
- `app/src/main/java/.../repository/` — Data layer (Room, local storage, etc.)
- `sdk/` — Local module used by the app

## Requirements

- Android Studio Flamingo or later (recommended)
- JDK 17 (project configured with JVM toolchain 17)
- Android SDK API 36 (compileSdk = 36)
- Minimum Android API: 26 (minSdk = 26)

## Setup & Build

1. Clone the repository:

```bash
git clone <your-repo-url>
cd ImageTOTextAndroidApp
```

2. Open the project in Android Studio and let Gradle sync.
3. If the build prompts for missing SDK components, install them via SDK Manager.
4. Run on a device or emulator with a camera (recommended: a physical device for best CameraX behavior).

## Permissions

The app interacts with camera and device images. Ensure you request and declare the correct permissions:

In `AndroidManifest.xml`:

```xml
<uses-permission android:name="android.permission.CAMERA" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" android:maxSdkVersion="32" />
<uses-permission android:name="android.permission.READ_MEDIA_IMAGES" />
```

Request these at runtime (Android 6.0+). For Android 13+ use `READ_MEDIA_IMAGES`; for earlier versions use `READ_EXTERNAL_STORAGE`.

## Running the app

- Build and run from Android Studio (Select an Android device/emulator and Run).
- Grant camera and storage/media permissions when the app requests them.

## Typical usage flow

1. Launch app — splash screen appears.
2. Open camera screen to capture an image.
3. Crop the captured image using the crop helper (uCrop integration).
4. App runs OCR (text recognition) on the cropped image and shows extracted text.
5. Optionally save or view previous extracted text (Room DB).

## Troubleshooting & Notes

- You may see vendor-specific logs such as:

```
Failed to find provider info for com.oplus.statistics.provider
```

  These originate from device/vendor analytics and are not caused by your app. They can generally be ignored.

- If camera capture fails inside a Composable: ensure you construct camera controller with a valid Context inside Compose (use `val context = LocalContext.current` and pass that to `LifecycleCameraController(context)`).

- If you use Coil and see problems loading files/URIs, verify the URI format and required permissions. Prefer `content://` URIs returned by MediaStore or use `FileProvider` URIs when sharing files between components.

- For Kotlin compiler / JVM target configuration the project uses JDK 17 and sets the Kotlin jvm target to 17 in Gradle Kotlin DSL. Keep your Android Studio and Kotlin plugin up to date.

## Contributing

- Follow the existing package and naming conventions (screens under `ui/screen`, ViewModels under `viewmodel`).
- Keep composables small and stateless when possible. Put side-effects and long-running operations into ViewModels.
- Add unit tests for ViewModels and integration tests for critical flows.

## Useful commands

Build debug APK:

```bash
./gradlew :app:assembleDebug
```

Run unit tests:

```bash
./gradlew test
```

Run instrumentation tests:

```bash
./gradlew connectedAndroidTest
```

## License

Add a license file if you plan to make the repository public (MIT/Apache-2.0 recommended).

---

If you want, I can expand this README with screenshots, a workflow GIF, or specific code snippets (how to capture images in ViewModel, runtime permission sample, or how to configure AdMob). Tell me which sections you'd like expanded.
