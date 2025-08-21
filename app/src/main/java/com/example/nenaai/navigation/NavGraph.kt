package com.example.nenaai.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.nenaai.ui.screens.LoginScreen
import com.example.nenaai.ui.screens.MainScreen
import com.example.nenaai.ui.screens.OtpVerificationScreen
import com.example.nenaai.ui.screens.ProfileCompletionScreen
import com.example.nenaai.ui.screens.UserInformation
import com.example.nenaai.ui.screens.VerificationScreen
import com.example.nenaai.viewmodel.AuthViewModel
import com.example.nenaai.viewmodel.AuthState
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = hiltViewModel()
    val authState by authViewModel.authState.collectAsStateWithLifecycle()

    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Success -> {
                when ((authState as AuthState.Success).authResponse.user_status) {
                    "OTP_VERIFIED" -> {
                        navController.navigate(Screen.ProfileCompletion.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
                    "PROFILE_COMPLETE" -> {
                        navController.navigate(Screen.Main.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
                    else -> { /* Handle other statuses or do nothing */ }
                }
                authViewModel.resetAuthState() // Reset state after navigation
            }
            else -> { /* Handle other states or do nothing */ }
        }
    }

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route // Start with LoginScreen
    ) {
        composable(Screen.Login.route) {
            LoginScreen(onOtpSent = {
                // Navigation is now handled by LaunchedEffect observing authState
            }, authViewModel = authViewModel)
        }
        composable(Screen.OtpVerification.route) {
            OtpVerificationScreen(onVerificationSuccess = {
                // Navigation is now handled by LaunchedEffect observing authState
            }, authViewModel = authViewModel)
        }
        composable(Screen.ProfileCompletion.route) {
            ProfileCompletionScreen(onProfileComplete = {
                // Navigation is now handled by LaunchedEffect observing authState
            }, authViewModel = authViewModel)
        }
        composable(Screen.Main.route) {
            MainScreen(navController)
        }
        composable(Screen.UserInformation.route){
            UserInformation(navController)
        }
        composable(Screen.VerificationScreen.route){
            VerificationScreen(navController)
        }
    }
}