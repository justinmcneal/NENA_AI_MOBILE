package com.example.nenaai.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
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
import com.example.nenaai.viewmodel.ChatViewModel
import com.example.nenaai.viewmodel.LoanStatusViewModel
import kotlinx.coroutines.launch

@SuppressLint("SuspiciousIndentation")
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

    // Initial fetch
    LaunchedEffect(Unit) {
        loanViewModel.fetchLoanData(token)
    }

    val navItems = listOf(
        NavItem("Home", Icons.Default.Home, Screen.BottomNav.Home.route),
        NavItem("Chat", Icons.Default.Face, Screen.BottomNav.Chat.route),
        NavItem("Profile", Icons.Default.Person, Screen.BottomNav.Profile.route)
    )

//
        Scaffold(
            modifier = Modifier.statusBarsPadding(),
            topBar = {
                TopAppBar(
                    title = { Text("Nena Ai", color = MaterialTheme.colorScheme.primary)},
                )
            },
            bottomBar = {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface)
                    {
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
                            },
                            colors = androidx.compose.material3.NavigationBarItemDefaults.colors(
                                selectedIconColor = Color.White,
                                selectedTextColor = MaterialTheme.colorScheme.primary, // 🔵 Blue for selected text
                                indicatorColor = MaterialTheme.colorScheme.primary,    // 🔵 Blue indicator
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
                            contentColor = Color.White

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
                            onNavigateToVerification = { navController.navigate(Screen.Verification.route) },
                            navController = navController // Pass navController here
                        )
                    }
                }
            }
        }

}
