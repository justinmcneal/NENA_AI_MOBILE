package com.example.nenaai.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object OtpVerification : Screen("otp_verification")
    object ProfileCompletion : Screen("profile_completion")
    object Main : Screen("main")
    object UserInformation : Screen("user_information")
    object VerificationScreen : Screen("verification_screen")
}