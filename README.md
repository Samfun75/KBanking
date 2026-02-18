# Kifiya Banking App

A modern, cross-platform mobile banking application built with Kotlin Multiplatform and Compose Multiplatform. This app demonstrates clean architecture principles, MVVM UI pattern, and best practices for mobile development.

## Features

- **User Authentication**: Secure login and registration with JWT token management
- **Account Overview**: View all bank accounts with real-time balance information
- **Fund Transfers**: Transfer money between accounts with instant validation
- **Transaction History**: View paginated transaction history for each account
- **Session Management**: Automatic token refresh and secure logout
- **Offline Caching**: In-memory caching for improved performance

## Tech Stack

- **Kotlin Multiplatform** - Share code between Android and iOS
- **Compose Multiplatform** - Declarative UI framework
- **Koin** - Dependency injection
- **KSafe** - Secure token storage
- **Navigation Compose** - Type-safe navigation
- **Kotlinx Coroutines** - Asynchronous programming
- **Kotlinx Serialization** - JSON parsing

## Architecture

The app follows **Clean Architecture** with three main layers:

```
├── domain/          # Business logic & use cases
│   ├── model/       # Domain models
│   ├── repository/  # Repository interfaces
│   ├── usecase/     # Business use cases
│   └── util/        # Validators & utilities
├── data/            # Data access layer
│   ├── remote/      # API service & DTOs
│   ├── repository/  # Repository implementations
│   ├── local/       # Token storage
│   └── mapper/      # DTO to domain mappers
└── presentation/    # UI layer (MVVM)
    ├── screens/     # Composable screens
    ├── viewmodel/   # ViewModels with state
    ├── components/  # Reusable UI components
    ├── navigation/  # Navigation graph
    └── theme/       # App theme & colors
```

## Getting Started

### Prerequisites

- Android Studio Panda 1 (2025.3.1) or newer
- Xcode 14+ (for iOS development)
- JDK 21+ (Use IDE Runtime or equivalent Java 21 for Gradle JDK)
- Gradle 9.0+

### Running on Android

1. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/KifiyaBanking.git
   ```

2. Open the project in Android Studio

3. Sync Gradle files

4. Select the `androidApp` run configuration

5. Run on emulator or physical device

### Running on iOS

1. Open terminal in the project root

2. Build the shared framework:
   ```bash
   ./gradlew :composeApp:assembleXCFramework
   ```

3. Open `iosApp/iosApp.xcodeproj` in Xcode

4. Select a simulator or device

5. Build and run

## Test Credentials

The app uses a mocked backend for development. Use these credentials to log in:

- **Username**: `testuser`
- **Password**: `Test1234`

Or create a new account through the registration screen.

## API Integration

Currently, the app uses `MockBankingApiService` for development. To switch to the real API:

1. Open `AppModule.kt`
2. Replace `MockBankingApiService()` with `KtorBankingApiService(httpClient)`
3. Configure the HTTP client in the platform modules

## Project Structure

```
KifiyaBanking/
├── composeApp/              # Shared KMP module
│   └── src/
│       ├── commonMain/      # Shared Kotlin code
│       ├── androidMain/     # Android-specific code
│       └── iosMain/         # iOS-specific code
├── androidApp/              # Android application
└── iosApp/                  # iOS application
```

## Key Features Implementation

### Authentication
- JWT access and refresh tokens
- Secure storage using KSafe
- Automatic token refresh on 401 responses
- Client-side validation for forms

### Caching
- In-memory account caching with 1-minute validity
- Cache invalidation on transfers
- Force refresh capability

### Pagination
- Infinite scroll for accounts and transactions
- Load more detection using LazyColumn state
- Loading indicators for pagination

### Error Handling
- Result<T> pattern for consistent error handling
- User-friendly error messages
- Network error detection
- Session expiry handling

## Building APK

To build a release APK:

```bash
./gradlew :androidApp:assembleRelease
```

The APK will be located at:
`androidApp/build/outputs/apk/release/androidApp-release.apk`

