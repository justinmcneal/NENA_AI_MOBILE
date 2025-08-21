package com.example.nenaai.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.nenaai.ui.components.CommonSnackbar
import com.example.nenaai.ui.theme.NENA_AI_MOBILETheme
import com.example.nenaai.viewmodel.AuthViewModel
import com.example.nenaai.viewmodel.AuthState
import com.example.nenaai.viewmodel.OneTimeEvent
import com.example.nenaai.navigation.Screen // Import Screen for navigation routes
import kotlinx.coroutines.launch

@Composable
fun PinVerificationScreen(
    navController: NavController,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    var pinInput by remember { mutableStateOf("") }
    val authState by authViewModel.authState.collectAsStateWithLifecycle()
    val phoneNumber by authViewModel.phoneNumber.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var isPinError by remember { mutableStateOf(false) } // ADDED: State to track PIN error

    LaunchedEffect(Unit) {
        authViewModel.oneTimeEvent.collect { event ->
            when (event) {
                is OneTimeEvent.Success -> {
                    isPinError = false // Reset error on success
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = event.authResponse.message,
                            withDismissAction = true
                        )
                    }
                    // Navigate to MainScreen on successful PIN verification
                    if (event.authResponse.user_status == "PIN_VERIFIED" || event.authResponse.access != null) { // Assuming backend sends PIN_VERIFIED or access token on success
                        navController.navigate(Screen.Main.route) {
                            popUpTo(Screen.PinVerification.route) { inclusive = true } // Clear backstack
                        }
                    }
                }
                is OneTimeEvent.Error -> {
                    isPinError = true // Set error on failure
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = event.message,
                            withDismissAction = true
                        )
                    }
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            Icons.Default.Lock,
            contentDescription = "PIN Verification",
            modifier = Modifier.size(96.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(48.dp))

        Text(
            text = "Enter Your PIN",
            style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Please enter your 4-6 digit PIN to continue.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = pinInput,
            onValueChange = { newValue ->
                if (newValue.length in 0..6 && newValue.all { it.isDigit() }) {
                    pinInput = newValue
                    isPinError = false // Reset error when user types
                }
            },
            label = { Text("PIN") },
            placeholder = { Text("e.g., 1234") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            modifier = Modifier.fillMaxWidth(),
            isError = isPinError, // CHANGED: Use isPinError state
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (pinInput.length in 4..6) {
                    authViewModel.loginWithPIN(phoneNumber, pinInput) // Use loginWithPIN for verification
                } else {
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = "PIN must be 4-6 digits long.",
                            withDismissAction = true
                        )
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            ),
            shape = RoundedCornerShape(12.dp),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
            enabled = authState !is AuthState.Loading
        ) {
            if (authState is AuthState.Loading) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(24.dp))
            } else {
                Text(
                    "Verify PIN",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
    CommonSnackbar(snackbarHostState = snackbarHostState)
}

@Preview(showBackground = true)
@Composable
fun PinVerificationScreenPreview() {
    NENA_AI_MOBILETheme {
        PinVerificationScreen(navController = rememberNavController())
    }
}