package com.example.nenaai.ui.screens

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import java.util.*

@Composable
fun VerificationScreen(navController: NavController) {
    var currentStep by remember { mutableStateOf(1) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Step Progress Indicator
        LinearProgressIndicator(
            progress = currentStep / 3f,
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(CircleShape),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Title
        Text(
            text = "Verification",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = "Step $currentStep of 3",
            style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Content inside a Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(4.dp),
            shape = MaterialTheme.shapes.medium
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                when (currentStep) {
                    1 -> Step1Content()
                    2 -> Step2Content()
                    3 -> Step3Content()
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Navigation buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (currentStep > 1) {
                OutlinedButton(
                    onClick = { currentStep-- },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                ) {
                    Text("Previous", fontSize = 16.sp)
                }
                Spacer(modifier = Modifier.width(16.dp))
            }
            Button(
                onClick = {
                    if (currentStep < 3) currentStep++ else {
                        navController.navigate("main_screen"){
                            popUpTo("main_screen"){
                                inclusive = true
                            }
                        }
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp)
                  ,
                shape = MaterialTheme.shapes.medium
            ) {
                Text(
                    if (currentStep < 3) "Next" else "Submit",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Step1Content() {
    // Basic Information section
    Text(
        text = "BASIC INFORMATION",
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .padding(bottom = 8.dp)
    )

    // Description text
    Text(
        text = "To apply for a loan on our platform, simply fill out your basic information, including your name, date of birth, and contact details. This information helps us verify your identity and streamline the application process. Rest assured, all your data is securely stored and protected.",
        fontSize = 14.sp,
        color = Color.Gray,
        textAlign = TextAlign.Start,
        modifier = Modifier
            .padding(bottom = 16.dp)
    )

    // Date of Birth
    val context = LocalContext.current
    var dateOfBirth by remember { mutableStateOf("") }
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    OutlinedTextField(
        value = dateOfBirth,
        onValueChange = {},
        label = { Text("Date of Birth") },
        modifier = Modifier.fillMaxWidth(),
        readOnly = true,
        trailingIcon = {
            IconButton(onClick = {
                DatePickerDialog(context, { _, y, m, d ->
                    dateOfBirth = "$y-${m + 1}-$d"
                }, year, month, day).show()
            }) {
                Icon(Icons.Default.ArrowDropDown, contentDescription = "Select Date")
            }
        }
    )
    Spacer(modifier = Modifier.height(16.dp))

    // Gender
    DropdownField(
        label = "Gender",
        options = listOf("Male", "Female")
    )
    Spacer(modifier = Modifier.height(16.dp))

    // Civil Status
    DropdownField(
        label = "Civil Status",
        options = listOf("Single", "Married", "Widowed", "Legally Separated")
    )
    Spacer(modifier = Modifier.height(16.dp))

    // Education Level
    DropdownField(
        label = "Education Level",
        options = listOf(
            "No Formal Education", "Elementary Graduate", "High School Graduate",
            "Vocational", "College Graduate", "Post Graduate"
        )
    )
    Spacer(modifier = Modifier.height(16.dp))

    // Region
    DropdownField(
            label = "Region",
    options = listOf(
        "NCR", "CAR", "Region I – Ilocos", "Region II – Cagayan Valley",
        "Region III – Central Luzon", "Region IV-A – CALABARZON", "MIMAROPA",
        "Region V – Bicol", "Region VI – Western Visayas", "Region VII – Central Visayas"
    )
    )
    Spacer(modifier = Modifier.height(16.dp))

    // Province (placeholder)

    DropdownField(
        label = "Province",
        options = listOf("Abra", "Albay", "Batangas", "Bulacan", "Cebu", "Davao del Sur")
    )
    Spacer(modifier = Modifier.height(16.dp))

    DropdownField(
        label = "City or Town",
        options = listOf("Quezon City", "Manila", "Cebu City", "Davao City", "Baguio")
    )
    Spacer(modifier = Modifier.height(16.dp))

    // Barangay (placeholder)
    DropdownField(label = "Barangay", options = listOf("asd","asdas"))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Step2Content() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Text(
            text = "Employment Information",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        Text(
            text = "Tell us about your employment details. This helps us assess your application.",
            fontSize = 14.sp,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 16.dp)
        )


        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = "",
            onValueChange = {},
            label = { Text("Employer / Business Name") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = "",
            onValueChange = {},
            label = { Text("Address") },
            modifier = Modifier.fillMaxWidth()
        )
        DropdownField(label = "Industry", options = listOf("Agriculture", "Manufacturing", "Services", "Retail", "Technology", "Other"))
    }
}

@Composable
fun Step3Content() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Text(
            text = "IDENTIFICATION",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        UploadField(label = "Valid Id Front")
        Spacer(modifier = Modifier.height(16.dp))
        UploadField(label = "Valid Id Back")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownField(label: String, options: List<String>) {
    var expanded by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf("") }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedOption,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        selectedOption = option
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun UploadField(label: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.LightGray.copy(alpha = 0.2f))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = label, fontSize = 16.sp)
            Button(
                onClick = { /* TODO: Handle upload */ },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Blue)
            ) {
                Text("Upload", color = Color.White)
            }
        }
    }
}