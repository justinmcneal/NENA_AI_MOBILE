# Nena AI Mobile Application

## Overview
This document outlines the architecture of the Nena AI mobile application, a native Android app built with modern, best-practice technologies.

## Architecture
This project follows the **MVVM (Model-View-ViewModel)** architecture, recommended by Google for building robust and maintainable Android applications. The architecture is further supported by Dependency Injection (Hilt), a single-activity pattern with Jetpack Compose, and a centralized, event-driven navigation system.

### Core Components

#### 1. View (UI Layer)
The View layer is responsible for displaying the application's UI and forwarding user interactions to the ViewModel. It is built entirely with **Jetpack Compose**.

-   **`MainActivity.kt`**: The single entry point for the UI. It hosts the `NavGraph` composable, which controls all screen displays.
-   **`ui/screens/`**: Contains all the individual screen composables (e.g., `LoginScreen.kt`, `OtpVerificationScreen.kt`). These screens are designed to be as stateless as possible, observing state from the ViewModel and delegating all logic to it.
-   **`ui/components/`**: Holds small, reusable UI elements (e.g., `CommonSnackbar.kt`) that can be used across multiple screens to maintain a consistent look and feel.
-   **`ui/theme/`**: Defines the application's visual style using Material Design 3, including colors (`Color.kt`), typography (`Type.kt`), and shapes (`Shape.kt`).

#### 2. ViewModel
The ViewModel acts as a bridge between the View and the Model. It holds and processes all the data required by the UI, handles business logic, and exposes state and events for the UI to observe, ensuring the UI stays updated and responsive.

-   **`viewmodel/AuthViewModel.kt`**: Manages the logic for the entire authentication flow.
    -   **Exposes**:
        -   `authState`: A `StateFlow` representing the current UI state (e.g., Idle, Loading).
        -   `oneTimeEvent`: A `SharedFlow` for events that should only be handled once, like showing a Snackbar message.
        -   `navigationEvent`: A `SharedFlow` for sending navigation commands to the UI layer, cleanly decoupling the ViewModel from the Navigation Controller.
    -   **Responsibilities**: Handles user registration by checking if the user already exists (`is_login_flow`), triggers OTP verification, and manages profile completion and PIN login flows.
-   **`viewmodel/ProfileViewModel.kt`**: Manages the logic related to the user's profile after the initial login/registration, such as setting or changing the PIN.

#### 3. Model (Data Layer)
The Model layer is responsible for managing the application's data. It fetches data from remote sources (and would be responsible for local caching) and provides it to the ViewModels.

-   **`data/repository/AuthRepository.kt`**: The single source of truth for authentication data. It abstracts the data sources, providing a clean API for the `AuthViewModel` to consume. It handles all interactions with the `ApiService`.
-   **`data/network/ApiService.kt`**: A Retrofit interface that defines all the HTTP API endpoints for communicating with the backend server.
-   **`data/network/AuthInterceptor.kt`**: An OkHttp interceptor that automatically attaches the user's authentication token to the headers of relevant API requests.
-   **`data/model/`**: Contains all the Kotlin data classes (DTOs) that model the JSON requests and responses for the API (e.g., `AuthDtos.kt`). This includes the `is_login_flow` flag in `AuthResponse` to differentiate between login and registration flows.
-   **`data/local/TokenManager.kt`**: A class responsible for securely saving and retrieving the user's authentication token and other session-related flags to and from `SharedPreferences`.

### Other Key Components

#### Dependency Injection (DI)
Dependency Injection is managed by **Hilt**, which simplifies DI by providing containers for every Android class in your project and managing their lifecycles.

-   **`di/NetworkModule.kt`**: A Hilt module that provides instances of network-related classes like `OkHttpClient`, `Retrofit`, and `ApiService` throughout the application, ensuring they are singletons.
-   **`NenaAIApplication.kt`**: The Application class, annotated with `@HiltAndroidApp` to trigger Hilt's code generation and initialize the dependency graph.

#### Navigation
Navigation is handled using Jetpack's **Navigation Compose** library within a single-activity architecture.

-   **`navigation/NavGraph.kt`**: Defines the entire navigation map of the app. It contains the `NavHost` and links all `Screen` routes to their composable destinations. It is the central observer of `NavigationEvent`s from the `AuthViewModel` and is responsible for executing all navigation actions.
-   **`navigation/Screen.kt`**: A sealed class that defines all possible navigation routes in a type-safe way. This prevents runtime errors that can occur from using simple strings for routes.

### Data Flow Example: Login/Registration

This example shows how the components work together when a user enters their phone number.

1.  **View (`LoginScreen`)**: The user enters their phone number and taps the "Send OTP" button. The click listener calls `authViewModel.registerUser(phoneNumber)`.
2.  **ViewModel (`AuthViewModel`)**:
    -   The `registerUser` function is invoked. It sets the UI state to `Loading`.
    -   It calls `repository.registerUser(phoneNumber)`.
3.  **Model (`AuthRepository` & `ApiService`)**:
    -   The repository creates a request object and calls the `apiService.registerUser()` suspend function.
    -   Retrofit executes the HTTP POST request to the `/register/` endpoint.
    -   The backend checks if the phone number exists and returns a response containing the `is_login_flow` flag.
4.  **ViewModel (`AuthViewModel`)**:
    -   The repository returns the `AuthResponse` to the ViewModel.
    -   The ViewModel inspects the `is_login_flow` flag.
    -   If `is_login_flow` is `true`, it emits `NavigationEvent.ToPinLogin`.
    -   If `is_login_flow` is `false`, it emits `NavigationEvent.ToOtpVerification`.
    -   It also emits a `OneTimeEvent.Success` to show a message (e.g., "OTP Sent").
5.  **View (`NavGraph`)**:
    -   A `LaunchedEffect` observing `navigationEvent` receives the event.
    -   It executes `navController.navigate(...)` to either the `PinVerificationScreen` or the `OtpVerificationScreen` based on the event, completing the flow.
