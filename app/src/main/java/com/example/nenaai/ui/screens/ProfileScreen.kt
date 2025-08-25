package com.example.nenaai.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.nenaai.data.model.User
import com.example.nenaai.ui.components.CommonSnackbar
import com.example.nenaai.ui.theme.AppTypography
import com.example.nenaai.ui.theme.AppShapes
import com.example.nenaai.viewmodel.ProfileViewModel
import androidx.navigation.NavController
import com.example.nenaai.navigation.Screen
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(
    onNavigateToSetPin: () -> Unit,
    onNavigateToVerification: () -> Unit,
    onNavigateToFAQs: () -> Unit = {},
    onNavigateToSupport: () -> Unit = {},
    profileViewModel: ProfileViewModel = hiltViewModel(),
    navController: NavController
) {
    val userProfile by profileViewModel.userProfile.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        profileViewModel.fetchUserProfile()
    }

    val currentUser: User = userProfile?.copy(
        phone_number = userProfile!!.phone_number,
        first_name = userProfile!!.first_name,
        middle_name = userProfile!!.middle_name,
        last_name = userProfile!!.last_name,
        verification_status = userProfile!!.verification_status,
        income = userProfile!!.income,
        loan_status = userProfile!!.loan_status
    ) ?: User(
        id = 0,
        phone_number = "Loading...",
        first_name = "",
        middle_name = null,
        last_name = "",
        verification_status = "LOADING",
        income = 0.0,
        loan_status = "NONE"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.Start
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        // ========================
        // Personal Information
        // ========================
        Text(
            text = "Personal Information",
            style = AppTypography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(8.dp))

        InfoItem(label = "Name", value = "${currentUser.first_name} ${currentUser.middle_name?.let { "$it " } ?: ""}${currentUser.last_name}")

        Spacer(modifier = Modifier.height(8.dp))

        InfoItem(label = "Phone", value = currentUser.phone_number)

        Spacer(modifier = Modifier.height(16.dp))

        // ========================
        // Account Settings
        // ========================
        Text(
            text = "Account Settings",
            style = AppTypography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(8.dp))

        ActionItem(text = "Change Pin") { onNavigateToSetPin() }

        Spacer(modifier = Modifier.height(8.dp))

        ActionItem(text = "Add Income Record") { navController.navigate(Screen.AddIncomeRecord.route) }

        Spacer(modifier = Modifier.height(8.dp))

        ActionItem(text = "View Income Records") { navController.navigate(Screen.IncomeRecordList.route) }

        Spacer(modifier = Modifier.height(8.dp))

        ActionItem(text = "View User Analytics") { navController.navigate(Screen.UserAnalytics.route) }

        Spacer(modifier = Modifier.height(8.dp))

        ActionItem(text = "View My Documents") { navController.navigate(Screen.UserDocumentList.route) }

        Spacer(modifier = Modifier.height(24.dp))

        // ========================
        // Help & Support
        // ========================
        Text(
            text = "Help & Support",
            style = AppTypography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(8.dp))

        ActionItem(text = "FAQs") { onNavigateToFAQs() }

        Spacer(modifier = Modifier.height(8.dp))

        ActionItem(text = "Contact Support") { onNavigateToSupport() }
        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                navController.navigate(Screen.Login.route) {
                    popUpTo(0) { inclusive = true }
                }
                scope.launch {
                    snackbarHostState.showSnackbar(
                        message = "Logged Out Successfully",
                        withDismissAction = true
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .clip(AppShapes.large),
            shape = AppShapes.large,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Text(text = "Logout", style = AppTypography.titleMedium)
        }
    }

    CommonSnackbar(snackbarHostState = snackbarHostState)
}

@Composable
fun InfoItem(label: String, value: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant, AppShapes.small)
            .padding(12.dp)
    ) {
        Text(text = label, style = AppTypography.bodyMedium, color = MaterialTheme.colorScheme.secondary)
        Text(text = value, style = AppTypography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
    }
}

@Composable
fun ActionItem(text: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant, AppShapes.medium)
            .clip(AppShapes.medium)
            .clickable { onClick() }
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = text, style = AppTypography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = "Arrow",
            tint = MaterialTheme.colorScheme.secondary
        )
    }
}