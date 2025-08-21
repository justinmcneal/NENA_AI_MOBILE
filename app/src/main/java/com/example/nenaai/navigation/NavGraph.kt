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
import com.example.nenaai.viewmodel.AuthViewModel
import com.example.nenaai.viewmodel.OneTimeEvent
import com.example.nenaai.viewmodel.ProfileViewModel // Import ProfileViewModel
import com.example.nenaai.viewmodel.ProfileOneTimeEvent // Import ProfileOneTimeEvent
import androidx.compose.runtime.LaunchedEffect

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = hiltViewModel()
    val profileViewModel: ProfileViewModel = hiltViewModel() // Get ProfileViewModel instance

    LaunchedEffect(Unit) { // Observe AuthViewModel's oneTimeEvent for navigation
        authViewModel.oneTimeEvent.collect { event ->
            Log.d("NavGraph", "Received Auth OneTimeEvent: $event")
            when (event) {
                is OneTimeEvent.Success -> {
                    Log.d("NavGraph", "Auth OneTimeEvent.Success with user_status: ${event.authResponse.user_status}")
                    when (event.authResponse.user_status) {
                        "OTP_SENT" -> {
                            Log.d("NavGraph", "Navigating to OtpVerificationScreen")
                            navController.navigate(Screen.OtpVerification.route)
                        }
                        "OTP_VERIFIED" -> {
                            Log.d("NavGraph", "Navigating to ProfileCompletionScreen")
                            navController.navigate(Screen.ProfileCompletion.route) {
                                popUpTo(Screen.Login.route) { inclusive = true }
                            }
                        }
                        "PROFILE_COMPLETE" -> {
                            Log.d("NavGraph", "Navigating to MainScreen")
                            navController.navigate(Screen.Main.route) {
                                popUpTo(Screen.Login.route) { inclusive = true }
                            }
                        }
                        else -> {
                            Log.d("NavGraph", "Unhandled user_status: ${event.authResponse.user_status}")
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
        startDestination = Screen.Login.route // Start with LoginScreen
    ) {
        composable(Screen.Login.route) {
            LoginScreen(authViewModel = authViewModel)
        }
        composable(Screen.OtpVerification.route) {
            OtpVerificationScreen(authViewModel = authViewModel)
        }
        composable(Screen.ProfileCompletion.route) {
            ProfileCompletionScreen(authViewModel = authViewModel)
        }
        composable(Screen.SetPin.route) {
            SetPinScreen(profileViewModel = profileViewModel)
        }
        composable(Screen.Main.route) {
            MainScreen(navController)
        }
        composable(Screen.VerificationScreen.route){
            VerificationScreen(navController)
        }
    }
}