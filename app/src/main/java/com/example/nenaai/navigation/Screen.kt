package com.example.nenaai.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object OtpVerification : Screen("otp_verification")
    object ProfileCompletion : Screen("profile_completion")
    object SetPin : Screen("set_pin")
    object Main : Screen("main")
    object PinVerification : Screen("pin_verification_screen")

    object ApplyLoan : Screen("apply_loan")
    object Verification : Screen("verification")
    object AddIncomeRecord : Screen("add_income_record")
    object IncomeRecordList : Screen("income_record_list")
    object UserAnalytics : Screen("user_analytics")
    object UserDocumentList : Screen("user_document_list")


    // Nested routes for Bottom Navigation
    object BottomNav {
        object Home : Screen("home_tab")
        object Chat : Screen("chat_tab") // ADDED THIS LINE
        object Profile : Screen("profile_tab")
        // Add other bottom nav tabs here
    }
}