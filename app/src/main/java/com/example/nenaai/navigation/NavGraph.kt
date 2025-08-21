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
import androidx.compose.runtime.LaunchedEffect

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = hiltViewModel()

    LaunchedEffect(Unit) { // Observe oneTimeEvent for navigation
        authViewModel.oneTimeEvent.collect { event ->
            Log.d("NavGraph", "Received OneTimeEvent: $event")
            when (event) {
                is OneTimeEvent.Success -> {
                    Log.d("NavGraph", "OneTimeEvent.Success with user_status: ${event.authResponse.user_status}")
                    when (event.authResponse.user_status) {
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
                    Log.d("NavGraph", "OneTimeEvent.Error: ${event.message}")
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
            LoginScreen(onOtpSent = {
                navController.navigate(Screen.OtpVerification.route) // Navigate to OTP verification
            }, authViewModel = authViewModel)
        }
        composable(Screen.OtpVerification.route) {
            OtpVerificationScreen(onVerificationSuccess = {
                // After OTP verification, navigation is handled by the LaunchedEffect in NavGraph
                // based on user_status (PROFILE_COMPLETE or OTP_VERIFIED leading to ProfileCompletionScreen)
            }, authViewModel = authViewModel)
        }
        composable(Screen.ProfileCompletion.route) {
            ProfileCompletionScreen(onProfileComplete = {
                // After profile completion, navigate to MainScreen
                navController.navigate(Screen.Main.route) {
                    popUpTo(Screen.Login.route) { inclusive = true } // Clear backstack up to Login
                }
            }, authViewModel = authViewModel)
        }
        composable(Screen.SetPin.route) {
            SetPinScreen(onPinSetSuccess = {
                navController.popBackStack() // Go back to previous screen (ProfileScreen)
            })
        }
        composable(Screen.Main.route) {
            MainScreen(navController)
        }
        composable(Screen.VerificationScreen.route){
            VerificationScreen(navController)
        }
    }
}