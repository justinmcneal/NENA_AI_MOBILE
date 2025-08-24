package com.example.nenaai.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.example.nenaai.viewmodel.ChatViewModel
import com.example.nenaai.viewmodel.LoanStatusViewModel
import com.example.nenaai.viewmodel.ProfileViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavController, token: String) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
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

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(16.dp)
            ) {
                Text("Drawer Menu", style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(16.dp))
                Text("Menu Item 1")
                Spacer(modifier = Modifier.height(8.dp))
                Text("Menu Item 2")
                Spacer(modifier = Modifier.height(8.dp))
                Text("Menu Item 3")
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Nena Ai") },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    }
                )
            },
            bottomBar = {
                NavigationBar {
                    navItems.forEach { item ->
                        NavigationBarItem(
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) },
                            selected = currentRoute == item.route,
                            onClick = {
                                nestedNavController.navigate(item.route) {
                                    popUpTo(nestedNavController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            },
            floatingActionButton = {
                if (currentRoute != Screen.BottomNav.Chat.route) {
                    FloatingActionButton(onClick = { /* TODO */ }) {
                        Icon(Icons.Default.Add, contentDescription = "Add")
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
                                onNavigateToVerification = {navController.navigate(Screen.Verification.route)},
                                retryFetchVerif = {profileViewModel.fetchUserProfile()}
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
                            navController = navController // Pass navController here
                        )
                    }
                }
            }
        }
    }
}
