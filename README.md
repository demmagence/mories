# Mories

Mories is a native Android application designed for browsing and streaming movies and television series. It integrates with the TMDB (The Movie Database) API for media metadata and utilizes a custom WebView implementation for media streaming.

## Architecture

The application strictly adheres to Clean Architecture principles and the Model-View-ViewModel (MVVM) pattern, ensuring a separation of concerns, testability, and scalability.

## Tech Stack

- **Language:** Kotlin
- **UI Toolkit:** Jetpack Compose
- **Dependency Injection:** Hilt
- **Networking:** Retrofit, OkHttp
- **Serialization:** Kotlinx Serialization
- **Local Persistence:** Room Database
- **Pagination:** Paging 3
- **Image Loading:** Coil
- **Asynchronous Programming:** Kotlin Coroutines & Flow

## Prerequisites

- Android Studio (Koala or latest stable version)
- JDK 17
- Android SDK Minimum API Level 24
- Target SDK API Level 35

## Getting Started

1. Clone the repository:
   ```bash
   git clone https://github.com/demmagence/mories.git
   ```
2. Create a `local.properties` file in the project root directory.
3. Obtain API keys from TMDB and configure them in `local.properties`:
   ```properties
   TMDB_API_KEY=your_api_key_here
   TMDB_READ_ACCESS_TOKEN=your_read_access_token_here
   ```
4. Build and run the project via Android Studio or the command line:
   ```bash
   ./gradlew assembleDebug
   ```

## Release Build Configuration

To build a signed release APK, configure your keystore details in `local.properties`:

```properties
KEYSTORE_PASSWORD=your_keystore_password
KEY_ALIAS=your_key_alias
KEY_PASSWORD=your_key_password
```

Ensure your `.jks` file is placed in the `app/` directory and named `release.jks`. Run the following command to generate the release build:

```bash
./gradlew assembleRelease
```

## Project Structure

- `data`: Data sources, repository implementations, and DTOs.
- `domain`: Core business logic, models, and repository interfaces.
- `ui`: Jetpack Compose screens, ViewModels, and UI state management.
- `di`: Hilt dependency injection modules.
- `util`: Extension functions and utility classes.
