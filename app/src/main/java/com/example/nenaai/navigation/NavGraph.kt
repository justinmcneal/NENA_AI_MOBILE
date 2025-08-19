package com.example.nenaai.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.nenaai.ui.screens.OnboardingScreen
import com.example.nenaai.ui.screens.SplashScreen

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(onTimeout = {
                navController.navigate(Screen.Onboarding.route) {
                    // Pop splash screen off the back stack
                    popUpTo(Screen.Splash.route) { inclusive = true }
                }
            })
        }
        composable(Screen.Onboarding.route) {
            OnboardingScreen(onOnboardingFinished = {
                navController.navigate(Screen.Main.route) {
                    // Pop onboarding screen off the back stack
                    popUpTo(Screen.Onboarding.route) { inclusive = true }
                }
            })
        }
        composable(Screen.Main.route) {
            // Main chat screen will be built later
        }
    }
}
