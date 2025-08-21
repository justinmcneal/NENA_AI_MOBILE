package com.example.nenaai.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object OtpVerification : Screen("otp_verification")
    object ProfileCompletion : Screen("profile_completion")
    object SetPin : Screen("set_pin")
    object Main : Screen("main")
    object VerificationScreen : Screen("verification_screen")
    object PinVerification : Screen("pin_verification_screen") // ADDED THIS LINE

    // Nested routes for Bottom Navigation
    object BottomNav {
        object Home : Screen("home_tab")
        object Profile : Screen("profile_tab")
        // Add other bottom nav tabs here
    }
}