package com.example.nenaai.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Phone
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
import com.example.nenaai.ui.components.CommonSnackbar
import com.example.nenaai.ui.theme.NENA_AI_MOBILETheme
import com.example.nenaai.viewmodel.AuthState
import com.example.nenaai.viewmodel.AuthViewModel
import com.example.nenaai.viewmodel.OneTimeEvent
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    authViewModel: AuthViewModel = hiltViewModel(),
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    val authState by authViewModel.authState.collectAsStateWithLifecycle()
    var textInput by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        authViewModel.oneTimeEvent.collect { event ->
            when (event) {
                is OneTimeEvent.Success -> {
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = event.authResponse.message,
                            withDismissAction = true
                        )
                    }
                }
                is OneTimeEvent.Error -> {
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
            Icons.Default.Phone,
            contentDescription = "App Logo",
            modifier = Modifier.size(96.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(48.dp))

        Text(
            text = "Welcome to Nena AI",
            style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Enter your mobile number to get started. We'll send a verification code.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = textInput,
            onValueChange = { newValue ->
                if (newValue.all { it.isDigit() } && newValue.length <= 10) {
                    textInput = newValue
                    authViewModel.setPhoneNumber(newValue)
                }
            },
            label = { Text("Mobile Number") },
            placeholder = { Text("e.g., 9123456789") },
            leadingIcon = {
                Text("+63 ", style = MaterialTheme.typography.bodyLarge)
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (textInput.length == 10) {
                    val fullPhoneNumber = "+63$textInput"
                    authViewModel.registerUser(fullPhoneNumber)
                } else {
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = "Please enter a valid 10-digit mobile number.",
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
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Text(
                    "Send OTP",
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
fun LoginScreenPreview() {
    NENA_AI_MOBILETheme {
        LoginScreen()
    }
}
