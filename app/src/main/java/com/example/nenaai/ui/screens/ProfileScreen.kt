package com.example.nenaai.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.nenaai.data.model.User
import com.example.nenaai.ui.components.CommonSnackbar
import com.example.nenaai.viewmodel.ProfileViewModel

@Composable
fun ProfileScreen(
    onNavigateToSetPin: () -> Unit,
    onNavigateToVerification: () -> Unit,
    onNavigateToFAQs: () -> Unit = {},      // New
    onNavigateToSupport: () -> Unit = {},   // New
    profileViewModel: ProfileViewModel = hiltViewModel()
) {
    val userProfile by profileViewModel.userProfile.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        profileViewModel.fetchUserProfile()
    }

    val currentUser: User = userProfile?.copy(
        phone_number = userProfile!!.phone_number ?: "Unknown",
        first_name = userProfile!!.first_name ?: "",
        middle_name = userProfile!!.middle_name,
        last_name = userProfile!!.last_name ?: "",
        verification_status = userProfile!!.verification_status ?: "UNKNOWN",
        income = userProfile!!.income,
        loan_status = userProfile!!.loan_status ?: "NONE"
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
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        // ========================
        // Personal Information
        // ========================
        Text(
            text = "Personal Information",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Name
        InfoItem(label = "Name", value = "${currentUser.first_name} ${currentUser.middle_name?.let { "$it " } ?: ""}${currentUser.last_name}")

        Spacer(modifier = Modifier.height(8.dp))

        // Phone
        InfoItem(label = "Phone", value = currentUser.phone_number)

        Spacer(modifier = Modifier.height(16.dp))

        // ========================
        // Account Settings
        // ========================
        Text(
            text = "Account Settings",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Change Password
        ActionItem(text = "Change Password") { onNavigateToSetPin() }

        Spacer(modifier = Modifier.height(8.dp))

        // Verification
        ActionItem(text = "Verification") { onNavigateToVerification() }

        Spacer(modifier = Modifier.height(24.dp))

        // ========================
        // Help & Support
        // ========================
        Text(
            text = "Help & Support",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(8.dp))

        // FAQs
        ActionItem(text = "FAQs") { onNavigateToFAQs() }

        Spacer(modifier = Modifier.height(8.dp))

        // Contact Support
        ActionItem(text = "Contact Support") { onNavigateToSupport() }
    }

    CommonSnackbar(snackbarHostState = snackbarHostState)
}

@Composable
fun InfoItem(label: String, value: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.LightGray.copy(alpha = 0.1f))
            .padding(12.dp)
    ) {
        Text(text = label, fontSize = 12.sp, color = Color.Gray)
        Text(text = value, fontSize = 16.sp)
    }
}

@Composable
fun ActionItem(text: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.LightGray.copy(alpha = 0.1f))
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = text, fontSize = 16.sp)
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = "Arrow",
            tint = Color.Gray
        )
    }
}
