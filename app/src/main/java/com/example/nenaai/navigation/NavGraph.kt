package com.example.nenaai.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nenaai.ui.screens.LoginScreen
import com.example.nenaai.ui.screens.MainScreen
import com.example.nenaai.ui.screens.OtpVerificationScreen
import com.example.nenaai.ui.screens.UserInformation
import com.example.nenaai.ui.screens.VerificationScreen
import com.example.nenaai.viewmodel.AuthViewModel

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination =Screen.Main.route
    ) {
        composable(Screen.Login.route) {
            LoginScreen(onOtpSent = {
                navController.navigate(Screen.OtpVerification.route)
            }, authViewModel = authViewModel)
        }
        composable(Screen.OtpVerification.route) {
            OtpVerificationScreen(onVerificationSuccess = {
                navController.navigate(Screen.Main.route) {
                    popUpTo(Screen.Login.route) { inclusive = true }
                }
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
