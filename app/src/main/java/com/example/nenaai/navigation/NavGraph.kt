package com.example.nenaai.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nenaai.data.repository.AuthRepository
import com.example.nenaai.ui.screens.LoginScreen
import com.example.nenaai.ui.screens.MainScreen
import com.example.nenaai.ui.screens.Registration
import com.example.nenaai.viewmodel.AuthViewModel
import com.example.nenaai.viewmodel.AuthViewModelFactory

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    val repository = AuthRepository()

    // Use factory
    val authViewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory(repository)
    )
    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        composable(Screen.Login.route) {
            LoginScreen(onLoginSuccess = {
                navController.navigate(Screen.Main.route)
            }, authViewModel = authViewModel,navController)
        }
//        composable(Screen.OtpVerification.route) {
//            OtpVerificationScreen(onVerificationSuccess = {
//                navController.navigate(Screen.UserInformation.route) {
//                    popUpTo(Screen.UserInformation.route) { inclusive = true }
//                }
//            }, authViewModel = authViewModel)
//        }
        composable(Screen.Main.route) {
            MainScreen(navController)
        }


        composable(Screen.Registration.route){
            Registration(navController,authViewModel)
        }

    }
}
