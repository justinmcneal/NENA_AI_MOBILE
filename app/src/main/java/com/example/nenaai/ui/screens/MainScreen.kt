package com.example.nenaai.ui.screens

import android.R
import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.material3.DismissibleNavigationDrawer
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
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

@SuppressLint("SuspiciousIndentation")
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

//    ModalNavigationDrawer(
//        drawerState = drawerState,
//        drawerContent = {
//            Column(
//                modifier = Modifier
//                    .fillMaxHeight()
//
//            ) {
//                // 🔵 Drawer Header with blue background
//                Box(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .height(120.dp)
//                        .background(MaterialTheme.colorScheme.primary), // blue background
//                    contentAlignment = Alignment.Center
//                ) {
//                    Text(
//                        text = "Nena Ai",
//                        style = MaterialTheme.typography.headlineMedium,
//                        color = MaterialTheme.colorScheme.onPrimary // makes text white
//                    )
//                }
//
//                Spacer(modifier = Modifier.height(16.dp))
//
//                // Drawer Navigation Items
//                navItems.forEach { item ->
//                    androidx.compose.material3.NavigationDrawerItem(
//                        icon = { Icon(item.icon, contentDescription = item.label) },
//                        label = { Text(item.label) },
//                        selected = currentRoute == item.route,
//                        onClick = {
//                            scope.launch { drawerState.close() }
//                            nestedNavController.navigate(item.route) {
//                                popUpTo(nestedNavController.graph.startDestinationId) {
//                                    saveState = true
//                                }
//                                launchSingleTop = true
//                                restoreState = true
//                            }
//                        },
//                        modifier = Modifier.padding(9.dp)
//                    )
//                }
//
//            }
//        }
//    ) {
        Scaffold(
            modifier = Modifier.statusBarsPadding(),

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
                NavigationBar (
                    containerColor = MaterialTheme.colorScheme.surface,
                ){
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
            // ✅ FAB only shows if you're NOT in Chat route
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
                        containerColor =  MaterialTheme.colorScheme.primary,
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
                    modifier = Modifier.fillMaxSize(),

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
                            onNavigateToVerification = { navController.navigate(Screen.Verification.route) }
                        )
                    }
                }
            }
        }
    }
