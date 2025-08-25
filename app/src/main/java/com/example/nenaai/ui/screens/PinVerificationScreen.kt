package com.example.nenaai.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.nenaai.ui.components.CommonSnackbar
import com.example.nenaai.ui.theme.AppTypography
import com.example.nenaai.ui.theme.AppShapes
import com.example.nenaai.ui.theme.NENA_AI_MOBILETheme
import com.example.nenaai.viewmodel.AuthViewModel
import com.example.nenaai.viewmodel.AuthState
import com.example.nenaai.viewmodel.OneTimeEvent
import com.example.nenaai.navigation.Screen
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
    var isPinError by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        authViewModel.oneTimeEvent.collect { event ->
            when (event) {
                is OneTimeEvent.Success -> {
                    isPinError = false
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = event.authResponse.message,
                            withDismissAction = true
                        )
                    }
                    if (event.authResponse.user_status == "PIN_VERIFIED" || event.authResponse.access != null) {
                        event.authResponse.access?.let { token ->
                            authViewModel.saveToken(token)
                        }

                        navController.navigate(Screen.Main.route) {
                            popUpTo(Screen.PinVerification.route) { inclusive = true }
                        }
                    }
                }
                is OneTimeEvent.Error -> {
                    isPinError = true
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
            style = AppTypography.headlineLarge,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Please enter your 4-6 digit PIN to continue.",
            style = AppTypography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = pinInput,
            onValueChange = { newValue ->
                if (newValue.length in 0..6 && newValue.all { it.isDigit() }) {
                    pinInput = newValue
                    isPinError = false
                }
            },
            label = { Text("PIN", style = AppTypography.bodyMedium) },
            placeholder = { Text("e.g., 1234", style = AppTypography.bodyMedium) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            modifier = Modifier.fillMaxWidth(),
            isError = isPinError,
            shape = AppShapes.medium
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (pinInput.length in 4..6) {
                    authViewModel.loginWithPIN(phoneNumber, pinInput)
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
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ),
            shape = AppShapes.medium,
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
            enabled = authState !is AuthState.Loading
        ) {
            if (authState is AuthState.Loading) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Text(
                    "Verify PIN",
                    style = AppTypography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
    CommonSnackbar(snackbarHostState = snackbarHostState)
}

@RequiresApi(Build.VERSION_CODES.S)
@Preview(showBackground = true)
@Composable
fun PinVerificationScreenPreview() {
    NENA_AI_MOBILETheme {
        PinVerificationScreen(navController = rememberNavController())
    }
}