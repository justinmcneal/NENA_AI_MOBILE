Project Architecture Documentation (MVVM - Jetpack Compose + Hilt + Retrofit)
Root Package (Application Source Code)

MainActivity.kt (View / Entry Point)

Role:

Single Activity in your Compose application.

Main entry point for the UI.

Sets up Compose content and hosts the navigation graph.

MVVM Contribution:

Acts as the primary View container.

Displays the UI and observes changes from the NavGraph (which observes ViewModels).

Delegates complex UI logic to composable screens.

NenaAIApplication.kt (Application / Hilt Setup)

Role:

Custom Application class annotated with @HiltAndroidApp.

Used by Hilt to generate dependency injection components.

MVVM Contribution:

Sets up the dependency graph for ViewModels, Repositories, and other components.

Enables separation of concerns and testability.

data/ (Model Layer)

This directory encapsulates all data-related logic, including local/remote sources, models, and repositories.

local/

TokenManager.kt

Role: Manages authentication tokens with SharedPreferences.

MVVM Contribution: Local data source for authentication tokens used by AuthRepository.

model/

NavItem.kt

Role: Data class for navigation items (label, icon, route).

MVVM Contribution: UI-specific model used in the View layer.

UserDetails.kt

Role: Holds user profile details.

MVVM Contribution: Data model representing user information.

model/dto/

AuthDtos.kt

Role: Data Transfer Objects (DTOs) for backend communication.

Includes request bodies (UserRegistrationRequest, OTPVerificationRequest) and responses (AuthResponse, BackendErrorResponse).

MVVM Contribution: Defines structures exchanged with the remote API.

network/

ApiService.kt

Role: Retrofit interface for API endpoints (e.g., POST register/).

MVVM Contribution: Remote data source contract.

AuthInterceptor.kt

Role: Adds Authorization headers to API requests (except public endpoints).

MVVM Contribution: Network layer authentication mechanism.

BackendException.kt

Role: Custom exception for backend errors.

MVVM Contribution: Structured error propagation.

repository/

AuthRepository.kt

Role: Abstracts data sources (ApiService, local storage).

Provides a clean API for ViewModels (e.g., registerUser, verifyOTP).

Handles API response parsing and error propagation.

MVVM Contribution: Central piece of the Model layer and single source of truth for authentication.

di/ (Dependency Injection)

NetworkModule.kt

Role: Hilt module that provides networking dependencies (OkHttpClient, Retrofit, ApiService).

MVVM Contribution: Enables loose coupling, testability, and modularity.

navigation/ (View / Navigation Logic)

NavGraph.kt

Role: Defines navigation structure with Jetpack Compose Navigation.

Observes authentication events and manages navigation actions.

MVVM Contribution: View layer component orchestrating navigation, often triggered by ViewModels.

Screen.kt

Role: Sealed class defining navigation routes.

MVVM Contribution: Type-safe navigation destinations for the View layer.

ui/ (View Layer)

Contains UI-related code with Jetpack Compose Composables.

components/

CommonSnackbar.kt

Role: Reusable snackbar UI component.

MVVM Contribution: Displays feedback messages triggered by ViewModels.

Placeholder.kt

Role: Simple reusable placeholder composable.

MVVM Contribution: Part of the UI toolkit.

screens/

Screens: HomeScreen.kt, LoginScreen.kt, MainScreen.kt, OtpVerificationScreen.kt,
ProfileCompletionScreen.kt, ProfileScreen.kt, SetPinScreen.kt, VerificationScreen.kt.

Role: Define the UI for individual screens.

MVVM Contribution: Collect StateFlows from ViewModels, display data, and send events back.

theme/

Files: Color.kt, Shape.kt, Theme.kt, Type.kt

Role: Define Material Design 3 visual theming.

MVVM Contribution: View layer aesthetics.

viewmodel/ (ViewModel Layer)

Intermediary between View and Model.

AuthViewModel.kt

Role: Manages authentication flows (registration, OTP verification, profile completion, login).

Exposes:

StateFlow for UI state.

SharedFlow for one-time events (navigation, messages).

MVVM Contribution: Processes user input, interacts with AuthRepository, updates state.

ProfileViewModel.kt

Role: Manages profile-specific logic (e.g., PIN setup).

MVVM Contribution: Specialized ViewModel for profile concerns.

MVVM Contributions Summary

Model Layer (data/):

Models: model/, model/dto/.

Data Sources: network/ and local/.

Repository: AuthRepository.kt.

View Layer (ui/, navigation/, MainActivity.kt):

Composables (ui/screens, ui/components).

Navigation (NavGraph.kt, Screen.kt).

Responsibility: Observe ViewModel state, render UI, dispatch events.

ViewModel Layer (viewmodel/):

Manage UI state, handle events, interact with Repository.

Data Flow Example: Login

View (LoginScreen): User enters phone number and taps Send OTP.

LoginScreen → AuthViewModel: Calls authViewModel.registerUser(phoneNumber).

AuthViewModel:

Updates _authState → Loading.

Calls repository.registerUser(phoneNumber).

AuthRepository: Calls apiService.registerUser(request).

ApiService: Sends actual network request.

AuthRepository: Parses response.

AuthViewModel:

On success: updates state, emits OneTimeEvent.Success.

On error: emits OneTimeEvent.Error.

NavGraph/LoginScreen: Observes event.

Success → Navigate to OtpVerificationScreen.

Error → Show error via CommonSnackbar.

LoginScreen: Observes state → hides loading indicator.