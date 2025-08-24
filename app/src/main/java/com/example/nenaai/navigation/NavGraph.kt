package com.example.nenaai.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.nenaai.ui.screens.LoginScreen
import com.example.nenaai.ui.screens.MainScreen
import com.example.nenaai.ui.screens.OtpVerificationScreen
import com.example.nenaai.ui.screens.ProfileCompletionScreen
import com.example.nenaai.ui.screens.SetPinScreen
import com.example.nenaai.ui.screens.VerificationScreen
import com.example.nenaai.ui.screens.PinVerificationScreen // Import the new screen
import com.example.nenaai.viewmodel.AuthViewModel
import com.example.nenaai.viewmodel.OneTimeEvent
import com.example.nenaai.viewmodel.ProfileViewModel
import com.example.nenaai.viewmodel.ProfileOneTimeEvent
import com.example.nenaai.data.local.TokenManager // Import TokenManager
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nenaai.ui.screens.AddIncomeRecordScreen
import com.example.nenaai.ui.screens.ApplyLoanScreen
import com.example.nenaai.ui.screens.IncomeRecordListScreen
import com.example.nenaai.viewmodel.ApplyLoanViewModel
import com.example.nenaai.viewmodel.NavigationEvent
import javax.inject.Inject // Import Inject

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = hiltViewModel()
    val profileViewModel: ProfileViewModel = hiltViewModel()
    val tokenManager: TokenManager = hiltViewModel<AuthViewModel>().tokenManager // Inject TokenManager

    // Determine start destination based on saved state
    val startDestination = getStartDestination(tokenManager)

    // This LaunchedEffect handles navigation events from the AuthViewModel.
    LaunchedEffect(Unit) {
        authViewModel.navigationEvent.collect { event ->
            Log.d("NavGraph", "Received NavigationEvent: $event")
            when (event) {
                is NavigationEvent.ToOtpVerification -> {
                    navController.navigate(Screen.OtpVerification.route)
                }
                is NavigationEvent.ToPinLogin -> {
                    navController.navigate(Screen.PinVerification.route)
                }
            }
        }
    }

    // This LaunchedEffect handles the original flow after OTP verification.
    LaunchedEffect(Unit) { // Observe AuthViewModel's oneTimeEvent for navigation
        authViewModel.oneTimeEvent.collect { event ->
            Log.d("NavGraph", "Received Auth OneTimeEvent: $event")
            when (event) {
                is OneTimeEvent.Success -> {
                    Log.d("NavGraph", "Auth OneTimeEvent.Success with message: ${event.authResponse.message}")
                    when (event.authResponse.user_status) { // Keep existing logic for other statuses
                        "OTP_VERIFIED" -> {
                            Log.d("NavGraph", "Navigating to ProfileCompletionScreen")
                            navController.navigate(Screen.ProfileCompletion.route) {
                                popUpTo(Screen.Login.route) { inclusive = true }
                            }
                        }
                        "PROFILE_COMPLETE" -> {
                            Log.d("NavGraph", "Navigating to SetPinScreen") // Changed log message
                            navController.navigate(Screen.SetPin.route) { // Changed navigation route
                                popUpTo(Screen.Login.route) { inclusive = true }
                            }
                        }
                        "PIN_VERIFIED" -> { // Or check for access token
                            Log.d("NavGraph", "Navigating to MainScreen after PIN login")
                            navController.navigate(Screen.Main.route) {
                                popUpTo(Screen.Login.route) { inclusive = true }
                            }
                        }
                        else -> {
                            Log.d("NavGraph", "Unhandled user_status or message: ${event.authResponse.user_status} / ${event.authResponse.message}")
                        }
                    }
                }
                is OneTimeEvent.Error -> {
                    Log.d("NavGraph", "Auth OneTimeEvent.Error: ${event.message}")
                    // Error handled by individual screens via Snackbar
                }
            }
        }
    }

    LaunchedEffect(Unit) { // Observe ProfileViewModel's oneTimeEvent for navigation
        profileViewModel.oneTimeEvent.collect { event ->
            Log.d("NavGraph", "Received Profile OneTimeEvent: $event")
            when (event) {
                is ProfileOneTimeEvent.Success -> {
                    Log.d("NavGraph", "Profile OneTimeEvent.Success: ${event.message}")
                    // Assuming setting PIN is the last step before full access to main features
                    // or returning to the profile screen.
                    // For now, let's navigate to MainScreen after successful PIN setup.
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Login.route) { inclusive = true } // Clear backstack up to Login
                    }
                }
                is ProfileOneTimeEvent.Error -> {
                    Log.d("NavGraph", "Profile OneTimeEvent.Error: ${event.message}")
                    // Error handled by individual screens via Snackbar
                }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = startDestination // Use dynamic start destination
    ) {
        composable(Screen.Login.route) {
            LoginScreen(authViewModel = authViewModel)
        }
        composable(Screen.OtpVerification.route) {
            OtpVerificationScreen(navController = navController, authViewModel = authViewModel)
        }
        composable(Screen.ProfileCompletion.route) {
            ProfileCompletionScreen(authViewModel = authViewModel)
        }
        composable(Screen.SetPin.route) {
            SetPinScreen(profileViewModel = profileViewModel)
        }
        composable(Screen.PinVerification.route) { // ADDED new composable
            PinVerificationScreen(navController = navController, authViewModel = authViewModel)
        }
        composable(Screen.Main.route) {
            tokenManager.getToken()?.let { it1 -> MainScreen(navController, it1) }
        }
        composable(Screen.Verification.route){
            VerificationScreen(navController = navController)
        }

        composable(Screen.ApplyLoan.route) {
            val viewModel: ApplyLoanViewModel = hiltViewModel() // or viewModel() if not using Hilt
            ApplyLoanScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onNavigateHome = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.AddIncomeRecord.route) {
            AddIncomeRecordScreen(onRecordAdded = { navController.popBackStack() })
        }
        composable(Screen.IncomeRecordList.route) {
            IncomeRecordListScreen(onBack = { navController.popBackStack() })
        }
        composable(Screen.UserAnalytics.route) {
            UserAnalyticsScreen(onBack = { navController.popBackStack() })
        }

    }
}

@Composable
private fun getStartDestination(tokenManager: TokenManager): String {
    return when {
        tokenManager.getToken() != null && tokenManager.getAuthFlowState(TokenManager.KEY_PIN_SET) -> {
            Screen.PinVerification.route // Changed to PinVerificationScreen
        }
        tokenManager.getAuthFlowState(TokenManager.KEY_PROFILE_COMPLETE) -> {
            Screen.SetPin.route // Profile is complete, but PIN is not set
        }
        tokenManager.getAuthFlowState(TokenManager.KEY_OTP_VERIFIED) -> {
            Screen.ProfileCompletion.route // OTP is verified, but profile is not complete
        }
        tokenManager.getAuthFlowState(TokenManager.KEY_OTP_SENT) -> {
            Screen.OtpVerification.route // OTP is sent, but not verified
        }
        else -> {
            Screen.Login.route // Default to login screen
        }
    }
}