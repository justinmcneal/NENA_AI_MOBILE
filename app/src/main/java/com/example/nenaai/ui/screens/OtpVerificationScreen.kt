package com.example.nenaai.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nenaai.ui.theme.NENA_AI_MOBILETheme
import com.example.nenaai.viewmodel.AuthViewModel
// import com.example.nenaai.R // Uncomment if you have an actual drawable asset

@Composable
fun OtpVerificationScreen(onVerificationSuccess: () -> Unit, authViewModel: AuthViewModel = viewModel()) {
    var otpInput by remember { mutableStateOf("") }
    val phoneNumber by authViewModel.phoneNumber.collectAsState()
    val error by authViewModel.error.collectAsState()
    val isAuthenticated by authViewModel.isAuthenticated.collectAsState()

    // Observe isAuthenticated state to trigger navigation
    if (isAuthenticated) {
        onVerificationSuccess()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // App Logo/Illustration (replace with your actual logo or choose another icon)
        Icon(
            Icons.Default.Call, // Using a message icon for OTP screen
            contentDescription = "OTP Verification",
            modifier = Modifier.size(96.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(48.dp))

        // Enhanced Title
        Text(
            text = "Verify Your Number",
            style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Informative subtitle with highlighted phone number
        Text(
            text = "Enter the 6-digit code sent to \n+63 $phoneNumber", // Line break for better readability
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(32.dp))

        // OutlinedTextField for OTP with improved label and leading icon
        OutlinedTextField(
            value = otpInput,
            onValueChange = { newValue ->
                if (newValue.length <= 6 && newValue.all { it.isDigit() }) { // Ensure only digits
                    otpInput = newValue
                    authViewModel.clearError()
                }
            },
            label = { Text("6-digit OTP") },
            placeholder = { Text("e.g., 123456") },
            leadingIcon = {
                Icon(Icons.Default.Lock, contentDescription = "OTP Icon")
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword), // NumberPassword hides input
            modifier = Modifier.fillMaxWidth(),
            isError = error != null,
            shape = RoundedCornerShape(12.dp)
        )

        // Error message handling
        error?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                textAlign = TextAlign.Start
            )
        }
        Spacer(modifier = Modifier.height(24.dp))

        // Styled Verify Button
        Button(
            onClick = { authViewModel.verifyOtp(otpInput) },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            ),
            shape = RoundedCornerShape(12.dp),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
        ) {
            Text(
                "Verify",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Styled Resend OTP Button (Outlined for secondary action)
        Button(
            onClick = { authViewModel.sendOtp(phoneNumber) }, // Resend OTP
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = Color.Transparent, // Transparent background
                contentColor = MaterialTheme.colorScheme.primary // Primary color for text/icon
            ),
            shape = RoundedCornerShape(12.dp),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp) // No shadow for outlined
        ) {
            Text(
                "Resend OTP",
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OtpVerificationScreenPreview() {
    NENA_AI_MOBILETheme {
        OtpVerificationScreen(onVerificationSuccess = {})
    }
}
