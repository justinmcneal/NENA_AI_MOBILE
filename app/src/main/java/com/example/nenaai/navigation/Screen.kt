package com.example.nenaai.navigation

sealed class Screen(val route: String) {
    object Auth : Screen("auth_screen")
    object Login : Screen("login_screen")
    object OtpVerification : Screen("otp_verification_screen")
    object Main : Screen("main_screen") // Main app content after auth
}
