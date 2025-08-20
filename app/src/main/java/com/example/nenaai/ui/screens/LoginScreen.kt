package com.example.nenaai.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource // Used for local drawable, adjust if using another image source
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nenaai.ui.theme.NENA_AI_MOBILETheme
import com.example.nenaai.viewmodel.AuthViewModel
import com.example.nenaai.R // Assuming R.drawable.your_logo is available or replace with a placeholder

@Composable
fun LoginScreen(onOtpSent: () -> Unit, authViewModel: AuthViewModel = viewModel()) {
    val phoneNumber by authViewModel.phoneNumber.collectAsState()
    val error by authViewModel.error.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // App Logo/Illustration (replace with your actual logo)
        // For demonstration, using a placeholder image or a simple Icon
        // If you have a drawable: Image(painter = painterResource(id = R.drawable.your_logo), ...)
        // For a simple placeholder:
        Icon(
            Icons.Default.Phone, // Using a phone icon as a placeholder
            contentDescription = "App Logo",
            modifier = Modifier.size(96.dp),
            tint = MaterialTheme.colorScheme.primary // Match app theme
        )
        Spacer(modifier = Modifier.height(48.dp))

        // Enhanced Title
        Text(
            text = "Welcome to Nena AI",
            style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Informative subtitle
        Text(
            text = "Enter your mobile number to get started. We'll send a verification code.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(32.dp))

        // OutlinedTextField with improved label and leading icon
        OutlinedTextField(
            value = phoneNumber,
            onValueChange = { newValue ->
                if (newValue.length <= 10) {
                    authViewModel.setPhoneNumber(newValue)
                    authViewModel.clearError()
                }
            },
            label = { Text("Mobile Number") },
            placeholder = { Text("e.g., 9123456789") }, // Moved example to placeholder
            leadingIcon = {
                Icon(Icons.Default.Phone, contentDescription = "Phone Icon")
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            modifier = Modifier.fillMaxWidth(),
            isError = error != null, // Use error directly for isError
            shape = RoundedCornerShape(12.dp) // Rounded corners for text field
        )

        // Error message handling
        error?.let { errorMessage ->
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                textAlign = TextAlign.Start // Align error text to start
            )
        }
        Spacer(modifier = Modifier.height(24.dp))

        // Styled Button
        Button(
            onClick = {
                authViewModel.sendOtp(phoneNumber)
                // Assuming authViewModel.sendOtp updates the error state
                // We should only navigate if there's no error *after* attempting to send OTP
                if (authViewModel.error.value == null) {
                    onOtpSent()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp), // Fixed height for a more prominent button
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary // Use primary color for button
            ),
            shape = RoundedCornerShape(12.dp), // Rounded corners for button
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp) // Subtle shadow
        ) {
            Text(
                "Send OTP",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimary // Text color on primary button
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    NENA_AI_MOBILETheme {
        LoginScreen(onOtpSent = {})
    }
}
