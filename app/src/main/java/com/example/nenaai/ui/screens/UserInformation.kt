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
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.nenaai.data.model.UserDetails


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserInformation(navController: NavController) {
    // State to hold user details
    var userDetails by remember { mutableStateOf(UserDetails()) }

    // State for validation errors
    var showError by remember { mutableStateOf(false) }

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
            }
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(24.dp), // Increased overall padding
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Icon/Illustration for User Info
                Icon(
                    Icons.Default.Person, // Using a person icon for user information
                    contentDescription = "User Information",
                    modifier = Modifier.size(96.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(48.dp))

                Text(
                    text = "Tell Us About Yourself",
                    style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Please provide your personal details to complete your profile.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(32.dp))

                OutlinedTextField(
                    value = userDetails.firstName,
                    onValueChange = { newValue ->
                        userDetails = userDetails.copy(firstName = newValue)
                        // Clear error related to this field when user types
                        if (showError && newValue.isNotBlank()) showError = false
                    },
                    label = { Text("First Name") },
                    isError = showError && userDetails.firstName.isBlank(),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp) // Rounded corners
                )
                Spacer(modifier = Modifier.height(16.dp)) // Increased spacing between fields
                OutlinedTextField(
                    value = userDetails.lastName,
                    onValueChange = { newValue ->
                        userDetails = userDetails.copy(lastName = newValue)
                        // Clear error related to this field when user types
                        if (showError && newValue.isNotBlank()) showError = false
                    },
                    label = { Text("Last Name") },
                    isError = showError && userDetails.lastName.isBlank(),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp) // Rounded corners
                )
                Spacer(modifier = Modifier.height(16.dp)) // Increased spacing
                OutlinedTextField(
                    value = userDetails.middleName,
                    onValueChange = { newValue ->
                        userDetails = userDetails.copy(middleName = newValue)
                        // No error check needed here if middle name is optional
                    },
                    label = { Text("Middle Name (Optional) ") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp) // Rounded corners
                )
                Spacer(modifier = Modifier.height(32.dp)) // Increased spacer before button

                Button(
                    onClick = {
                        // Validate required fields before navigating
                        val isValid = userDetails.firstName.isNotBlank() && userDetails.lastName.isNotBlank()

                        if (isValid) {
                            showError = false // Clear any previous error
                            navController.navigate("main_screen") {
                                popUpTo("main_screen") {
                                    inclusive = true
                                }
                            }
                        } else {
                            showError = true // Show error if validation fails
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp), // Fixed height for prominence
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary // Use primary color
                    ),
                    shape = RoundedCornerShape(12.dp), // Rounded corners
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp) // Subtle shadow
                ) {
                    Text(
                        text = "Confirm",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }

                if (showError) {
                    Text(
                        text = "Please fill in all required fields (First Name, Last Name).",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        textAlign = TextAlign.Start // Align error text to start
                    )
                }
            }
        }
    )
}
