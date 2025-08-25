package com.example.nenaai.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.nenaai.data.model.NavItem
import com.example.nenaai.navigation.Screen
import com.example.nenaai.ui.theme.AppTypography
import com.example.nenaai.viewmodel.ChatViewModel
import com.example.nenaai.viewmodel.LoanStatusViewModel
import com.example.nenaai.viewmodel.ProfileViewModel

@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavController, token: String) {
    val nestedNavController = rememberNavController()
    val navBackStackEntry by nestedNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val loanViewModel: LoanStatusViewModel = hiltViewModel()
    val loanState by loanViewModel.loanDetails.collectAsStateWithLifecycle()
    val profileViewModel: ProfileViewModel = hiltViewModel()
    val profileState by profileViewModel.userProfile.collectAsStateWithLifecycle()

    // Initial fetch
    LaunchedEffect(Unit) {
        loanViewModel.fetchLoanData(token)
        profileViewModel.fetchUserProfile()
    }

    val navItems = listOf(
        NavItem("Home", Icons.Default.Home, Screen.BottomNav.Home.route),
        NavItem("Chat", Icons.Default.Face, Screen.BottomNav.Chat.route),
        NavItem("Profile", Icons.Default.Person, Screen.BottomNav.Profile.route)
    )

    Scaffold(
        modifier = Modifier.statusBarsPadding(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Nena AI",
                        style = AppTypography.headlineLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 4.dp
            ) {
                navItems.forEach { item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = {
                            Text(
                                item.label,
                                style = AppTypography.bodyMedium,
                                color = if (currentRoute == item.route)
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.onSurface
                            )
                        },
                        selected = currentRoute == item.route,
                        onClick = {
                            nestedNavController.navigate(item.route) {
                                popUpTo(nestedNavController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primary,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurface,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurface
                        )
                    )
                }
            }
        },
        floatingActionButton = {
            if (currentRoute != Screen.BottomNav.Chat.route) {
                FloatingActionButton(
                    onClick = {
                        nestedNavController.navigate(Screen.BottomNav.Chat.route) {
                            popUpTo(nestedNavController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    modifier = Modifier.clip(CircleShape),
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White,
                    shape = CircleShape,
                    elevation = FloatingActionButtonDefaults.elevation(8.dp)
                ) {
                    Icon(Icons.Default.Face, contentDescription = "Go to Chat")
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            NavHost(
                navController = nestedNavController,
                startDestination = navItems[0].route,
                modifier = Modifier.fillMaxSize()
            ) {
                composable(navItems[0].route) {
                    profileState?.let { it1 ->
                        HomeScreen(
                            onApplyLoanClick = { navController.navigate(Screen.ApplyLoan.route) },
                            loanState = loanState,
                            retryFetchLoan = { loanViewModel.fetchLoanData(token) },
                            verificationStatus = it1.verification_status,
                            onNavigateToVerification = { navController.navigate(Screen.Verification.route) },
                            retryFetchVerif = { profileViewModel.fetchUserProfile() }
                        )
                    }
                }
                composable(Screen.BottomNav.Chat.route) {
                    val chatViewModel: ChatViewModel = hiltViewModel()
                    val messages by chatViewModel.messages.collectAsStateWithLifecycle()
                    ChatScreen(
                        messages = messages,
                        onSendMessage = { chatViewModel.sendMessage(it) }
                    )
                }
                composable(Screen.BottomNav.Profile.route) {
                    ProfileScreen(
                        onNavigateToSetPin = { navController.navigate(Screen.SetPin.route) },
                        onNavigateToVerification = { navController.navigate(Screen.Verification.route) },
                        navController = navController
                    )
                }
            }
        }
    }
}