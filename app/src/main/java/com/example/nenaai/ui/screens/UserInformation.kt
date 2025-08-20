package com.example.nenaai.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.nenaai.data.model.UserDetails
import java.text.DecimalFormat


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserInformation(navController: NavController) {
    // Current step in the form (0-indexed)
    var currentStep by remember { mutableStateOf(0) }

    // Total number of steps in the form
    val totalSteps = 3 // Personal Info, Contact Info, Address Info (excluding review for progress calculation)

    // State to hold user details
    var userDetails by remember { mutableStateOf(UserDetails()) }

    // State for validation errors
    var showError by remember { mutableStateOf(false) }

    // Determine the progress percentage
    // Progress calculation based on current step relative to total conceptual steps (including review as part of the flow)
    val progressFraction = (currentStep.toFloat() / totalSteps.toFloat()).coerceIn(0f, 1f)
    val percentageFormatter = remember { DecimalFormat("0%") }
    val progressText = percentageFormatter.format(progressFraction)

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text("User Details Input") },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
                // Row for progress indicator and percentage text
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    LinearProgressIndicator(
                        progress = progressFraction,
                        modifier = Modifier.weight(1f),
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(modifier = Modifier.padding(horizontal = 8.dp))
                    Text(text = progressText, style = MaterialTheme.typography.bodyMedium)
                }
            }
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Content changes based on the current step
                when (currentStep) {
                    0 -> PersonalInfoStep(userDetails = userDetails, onDetailsChange = { userDetails = it }, showError = showError)
                    1 -> ContactInfoStep(userDetails = userDetails, onDetailsChange = { userDetails = it }, showError = showError)
                    2 -> AddressInfoStep(userDetails = userDetails, onDetailsChange = { userDetails = it }, showError = showError)
                    3 -> ReviewAndFinishStep(userDetails = userDetails) // Final review step
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Navigation buttons
                Button(
                    onClick = {
                        showError = false // Reset error state on button click

                        // Validate current step before moving to the next
                        val isValid = when (currentStep) {
                            0 -> userDetails.firstName.isNotBlank() && userDetails.lastName.isNotBlank()
                            1 -> userDetails.email.isNotBlank() && userDetails.phone.isNotBlank()
                            2 -> userDetails.addressLine1.isNotBlank() && userDetails.city.isNotBlank() && userDetails.zipCode.isNotBlank()
                            else -> true // No validation needed for the review step
                        }

                        if (isValid) {
                            if (currentStep < totalSteps) { // Check against totalSteps for advancement
                                currentStep++ // Move to next step
                            } else {
                                // All steps completed, navigate to MainScreen and clear back stack
                                println("User Details Completed: $userDetails")
                                // Navigate to MainScreen and pop all previous destinations
                                navController.navigate("main_screen") {
                                    popUpTo("main_screen") { // Pop up to and including main_screen, making it the new root
                                        inclusive = true
                                    }
                                }
                            }
                        } else {
                            showError = true // Show error if validation fails
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (currentStep < totalSteps) "Next" else "Finish")
                }

                if (showError) {
                    Text(
                        text = "Please fill in all required fields.",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }
    )
}


@Composable
fun PersonalInfoStep(userDetails: UserDetails, onDetailsChange: (UserDetails) -> Unit, showError: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Personal Information", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = userDetails.firstName,
            onValueChange = { newValue -> onDetailsChange(userDetails.copy(firstName = newValue)) },
            label = { Text("First Name") },
            isError = showError && userDetails.firstName.isBlank(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = userDetails.lastName,
            onValueChange = { newValue -> onDetailsChange(userDetails.copy(lastName = newValue)) },
            label = { Text("Last Name") },
            isError = showError && userDetails.lastName.isBlank(),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun ContactInfoStep(userDetails: UserDetails, onDetailsChange: (UserDetails) -> Unit, showError: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Contact Information", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = userDetails.email,
            onValueChange = { newValue -> onDetailsChange(userDetails.copy(email = newValue)) },
            label = { Text("Email") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            isError = showError && userDetails.email.isBlank(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = userDetails.phone,
            onValueChange = { newValue -> onDetailsChange(userDetails.copy(phone = newValue)) },
            label = { Text("Phone Number") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            isError = showError && userDetails.phone.isBlank(),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun AddressInfoStep(userDetails: UserDetails, onDetailsChange: (UserDetails) -> Unit, showError: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Address Information", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = userDetails.addressLine1,
            onValueChange = { newValue -> onDetailsChange(userDetails.copy(addressLine1 = newValue)) },
            label = { Text("Address Line 1") },
            isError = showError && userDetails.addressLine1.isBlank(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = userDetails.city,
            onValueChange = { newValue -> onDetailsChange(userDetails.copy(city = newValue)) },
            label = { Text("City") },
            isError = showError && userDetails.city.isBlank(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = userDetails.zipCode,
            onValueChange = { newValue -> onDetailsChange(userDetails.copy(zipCode = newValue)) },
            label = { Text("Zip Code") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            isError = showError && userDetails.zipCode.isBlank(),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun ReviewAndFinishStep(userDetails: UserDetails) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Review Your Details", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        Text("First Name: ${userDetails.firstName}")
        Text("Last Name: ${userDetails.lastName}")
        Text("Email: ${userDetails.email}")
        Text("Phone: ${userDetails.phone}")
        Text("Address: ${userDetails.addressLine1}, ${userDetails.city}, ${userDetails.zipCode}")
        Spacer(modifier = Modifier.height(16.dp))
        Text("Click 'Finish' to complete.", style = MaterialTheme.typography.bodyLarge)
    }
}
