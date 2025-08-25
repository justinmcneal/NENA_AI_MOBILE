package com.example.nenaai.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
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

@RequiresApi(Build.VERSION_CODES.S)
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

    NENA_AI_MOBILETheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 32.dp)
                    .padding(top = 64.dp),
                verticalArrangement = Arrangement.Center, // Proper vertical centering
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Nena AI",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    textAlign = TextAlign.Center,
                )
                Spacer(modifier = Modifier.height(64.dp))

                Text(
                    text = "Welcome to Nena AI",
                    style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Enter your mobile number to get started. We'll send a verification code.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(48.dp))

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
                        Text("+63 ", style = MaterialTheme.typography.bodyMedium)
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.large,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                )

                Spacer(modifier = Modifier.height(32.dp))

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
                    shape = MaterialTheme.shapes.large,
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp),
                    enabled = authState !is AuthState.Loading,
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    if (authState is AuthState.Loading) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    } else {
                        Text(
                            "Send OTP",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }
            CommonSnackbar(snackbarHostState = snackbarHostState)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.S)
@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    NENA_AI_MOBILETheme {
        LoginScreen()
    }
}