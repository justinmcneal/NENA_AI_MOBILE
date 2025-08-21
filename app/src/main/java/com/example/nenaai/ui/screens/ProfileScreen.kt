package com.example.nenaai.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
    profileViewModel: ProfileViewModel = hiltViewModel()
) {
    val userProfile by profileViewModel.userProfile.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // Placeholder user data for now. In a real app, this would be fetched from API.
    val currentUser = userProfile ?: User(
        phone_number = "+639123456789",
        first_name = "John",
        middle_name = "D.",
        last_name = "Doe",
        verification_status = "UNVERIFIED"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        // Header with name and Unverified badge
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF3F51B5)) // Purple background
                .padding(16.dp)
        ) {
            Column {
                Text(
                    text = "${currentUser.first_name} ${currentUser.middle_name?.let { "$it " } ?: ""}${currentUser.last_name}",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                if (currentUser.verification_status != "PROFILE_COMPLETE") {
                    Text(
                        text = currentUser.verification_status,
                        color = Color.White,
                        fontSize = 14.sp,
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.Red)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                            .clickable {
                                onNavigateToVerification()
                            }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Personal Information section
        Text(
            text = "Personal Information",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Name
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.LightGray.copy(alpha = 0.1f))
                .padding(12.dp)
        ) {
            Text(
                text = "Name",
                fontSize = 12.sp,
                color = Color.Gray
            )
            Text(
                text = "${currentUser.first_name} ${currentUser.middle_name?.let { "$it " } ?: ""}${currentUser.last_name}",
                fontSize = 16.sp
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Email (Placeholder for now)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.LightGray.copy(alpha = 0.1f))
                .padding(12.dp)
        ) {
            Text(
                text = "rhyforemd@gmail.com", // Placeholder
                fontSize = 16.sp
            )
            Text(
                text = "Contact admin to update",
                fontSize = 12.sp,
                color = Color.Gray
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Phone
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.LightGray.copy(alpha = 0.1f))
                .padding(12.dp)
        ) {
            Text(
                text = "Phone",
                fontSize = 12.sp,
                color = Color.Gray
            )
            Text(
                text = currentUser.phone_number,
                fontSize = 16.sp
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Change Password
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.LightGray.copy(alpha = 0.1f))
                .clip(RoundedCornerShape(12.dp))
                .clickable {
                    onNavigateToSetPin()
                }
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Change Password",
                fontSize = 16.sp
            )
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Arrow",
                tint = Color.Gray
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Business Information",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

        // Assuming empty for now, as per image
    }
    CommonSnackbar(snackbarHostState = snackbarHostState)
}