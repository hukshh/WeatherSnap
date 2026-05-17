# WeatherSnap 🌤️📸

WeatherSnap is a production-quality Android application that allows users to fetch real-time weather data for any city and create detailed reports with captured photos.

## 🚀 Setup & Run Steps

1.  **Clone the repository**: Open the project in Android Studio.
2.  **Gradle Sync**: Ensure all dependencies are downloaded.
3.  **Build**: Build the project using the standard Gradle build process.
4.  **Run**: Deploy the app to an Android device or emulator (API 24+).
5.  **Permissions**: Grant Camera permission when prompted to use the reporting feature.

## 🛠️ Tech Stack

-   **Language**: Kotlin
-   **UI**: Jetpack Compose + Material 3
-   **Architecture**: MVVM (ViewModel + StateFlow + Coroutines)
-   **DI**: Hilt
-   **Navigation**: Navigation Compose with animated transitions
-   **Network**: Retrofit + Gson + OkHttp
-   **Local DB**: Room (All DB operations on IO dispatcher)
-   **Camera**: CameraX
-   **Image Handling**: Manual Bitmap compression (no third-party libs)
-   **Image Loading**: Coil (for reports list)
-   **UI Polish**: Dynamic gradients, Glassmorphism cards, Modern search UI

## 🧠 Developer Judgment Challenge

### Problem
Maintaining in-progress report state (weather snapshot, captured image, and notes) across configuration changes (rotation) or process death without creating duplicate entries in the database.

### Implemented Solution
In-progress report state (weather snapshot, image path, and notes) is stored in `SavedStateHandle` inside `CreateReportViewModel`. This survives process death and rotation without any extra persistence layer. Saving to Room only happens on explicit user action (Save button), preventing duplicates. Temp image files are cleaned up in `onCleared()` if the report was not saved, and the uncompressed original is deleted after a successful save.

## ⚖️ Known Tradeoffs

-   **Image Compression**: Manual compression is done on a background thread using `Bitmap.compress`. While effective for simple use cases, more complex scenarios might benefit from a dedicated library for advanced image processing.
-   **Permission Handling**: A basic permission request flow is implemented. In a full production app, more robust rationale handling and edge cases for permission denial would be added.
-   **API Caching**: City suggestions are cached in memory (HashMap) within the ViewModel. For a more persistent experience, this could be moved to a local DB cache.
