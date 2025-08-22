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
import androidx.compose.runtime.*
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost // Import NavHost
import androidx.navigation.compose.composable // Import composable
import androidx.navigation.compose.rememberNavController // Import rememberNavController
import androidx.navigation.compose.currentBackStackEntryAsState // Import currentBackStackEntryAsState
import com.example.nenaai.data.model.NavItem
import com.example.nenaai.navigation.Screen // Import Screen
import com.example.nenaai.viewmodel.ChatViewModel
import com.example.nenaai.viewmodel.LoanStatusViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavController, token: String) { // navController is the parent NavController
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val nestedNavController = rememberNavController() // Nested NavController for bottom tabs
    val navBackStackEntry by nestedNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val loanViewModel: LoanStatusViewModel = hiltViewModel()
    val loanState by loanViewModel.loanDetails.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        loanViewModel.fetchLoanData(token)
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
            // ✅ FAB only shows if you're NOT in Chat route
            floatingActionButton = {
                if (currentRoute != Screen.BottomNav.Chat.route) {
                    FloatingActionButton(onClick = { /* TODO: Add action */ }) {
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
                        HomeScreen(
                            onApplyLoanClick = { navController.navigate(Screen.ApplyLoan.route) },
                            loanState = loanState,
                            retryFetch = { loanViewModel.fetchLoanData(token) }
                        )
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
                            onNavigateToVerification = { navController.navigate(Screen.VerificationScreen.route) }
                        )
                    }
                }
            }
        }
    }
}