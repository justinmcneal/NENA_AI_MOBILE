package com.example.nenaai.ui.screens

import android.app.DatePickerDialog
import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Create
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.nenaai.data.local.TokenManager
import com.example.nenaai.data.model.UserVerificationRequest
import com.example.nenaai.navigation.Screen
import com.example.nenaai.viewmodel.VerificationViewModel
import java.util.*

@Composable
fun VerificationScreen(
    navController: NavController,
    viewModel: VerificationViewModel = hiltViewModel()
) {
    var currentStep by remember { mutableIntStateOf(1) }
    val documentBitmaps by viewModel.documentBitmaps.collectAsStateWithLifecycle()

    // 🟢 Form States
    var dateOfBirth by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var civilStatus by remember { mutableStateOf("") }
    var educationLevel by remember { mutableStateOf("") }
    var region by remember { mutableStateOf("") }
    var province by remember { mutableStateOf("") }
    var cityTown by remember { mutableStateOf("") }
    var barangay by remember { mutableStateOf("") }
    var businessName by remember { mutableStateOf("") }
    var businessAddress by remember { mutableStateOf("") }
    var businessIndustry by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LinearProgressIndicator(
            progress = { currentStep / 3f },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(CircleShape),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
        )

        Spacer(modifier = Modifier.height(24.dp))

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

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(4.dp),
            shape = MaterialTheme.shapes.medium
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                when (currentStep) {
                    1 -> Step1Content(
                        dateOfBirth = dateOfBirth,
                        onDateOfBirthChange = { dateOfBirth = it },
                        gender = gender,
                        onGenderChange = { gender = it },
                        civilStatus = civilStatus,
                        onCivilStatusChange = { civilStatus = it },
                        educationLevel = educationLevel,
                        onEducationLevelChange = { educationLevel = it },
                        region = region,
                        onRegionChange = { region = it },
                        province = province,
                        onProvinceChange = { province = it },
                        cityTown = cityTown,
                        onCityTownChange = { cityTown = it },
                        barangay = barangay,
                        onBarangayChange = { barangay = it }
                    )
                    2 -> Step2Content(
                        businessName = businessName,
                        onBusinessNameChange = { businessName = it },
                        businessAddress = businessAddress,
                        onBusinessAddressChange = { businessAddress = it },
                        businessIndustry = businessIndustry,
                        onBusinessIndustryChange = { businessIndustry = it }
                    )
                    3 -> Step3Content(
                        bitmaps = documentBitmaps,
                        onDocumentCaptured = viewModel::onDocumentCaptured
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (currentStep > 1) {
                OutlinedButton(
                    onClick = { currentStep-- },
                    modifier = Modifier.weight(1f).height(50.dp)
                ) {
                    Text("Previous", fontSize = 16.sp)
                }
                Spacer(modifier = Modifier.width(16.dp))
            }
            Button(
                onClick = {
                    if (currentStep < 3) {
                        currentStep++
                    } else {
                        // 🟢 Build request and call ViewModel
                        val request = UserVerificationRequest(
                            date_of_birth = dateOfBirth,
                            gender = gender,
                            civil_status = civilStatus,
                            education_level = educationLevel,
                            region = region,
                            province = province,
                            city_town = cityTown,
                            barangay = barangay,
                            business_name = businessName,
                            business_address = businessAddress,
                            business_industry = businessIndustry
                        )
                        viewModel.submitVerification()
                        viewModel.submitVerificationDetails(request)

                        navController.navigate(Screen.Main.route) {
                            popUpTo(Screen.Verification.route) { inclusive = true }
                        }
                    }
                },
                modifier = Modifier.weight(1f).height(50.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(if (currentStep < 3) "Next" else "Submit", fontSize = 16.sp, fontWeight = FontWeight.Medium)
            }
        }
    }
}

@Composable
fun Step1Content(
    dateOfBirth: String,
    onDateOfBirthChange: (String) -> Unit,
    gender: String,
    onGenderChange: (String) -> Unit,
    civilStatus: String,
    onCivilStatusChange: (String) -> Unit,
    educationLevel: String,
    onEducationLevelChange: (String) -> Unit,
    region: String,
    onRegionChange: (String) -> Unit,
    province: String,
    onProvinceChange: (String) -> Unit,
    cityTown: String,
    onCityTownChange: (String) -> Unit,
    barangay: String,
    onBarangayChange: (String) -> Unit
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    OutlinedTextField(
        value = dateOfBirth,
        onValueChange = {},
        label = { Text("Date of Birth") },
        modifier = Modifier.fillMaxWidth(),
        readOnly = true,
        trailingIcon = {
            IconButton(onClick = {
                DatePickerDialog(context, { _, y, m, d ->
                    onDateOfBirthChange("$y-${m + 1}-$d")
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
            }) {
                Icon(Icons.Default.ArrowDropDown, contentDescription = "Select Date")
            }
        }
    )
    Spacer(modifier = Modifier.height(16.dp))

    DropdownField("Gender", listOf("Male", "Female"), gender, onGenderChange)
    Spacer(modifier = Modifier.height(16.dp))

    DropdownField("Civil Status", listOf("Single", "Married", "Widowed", "Legally Separated"), civilStatus, onCivilStatusChange)
    Spacer(modifier = Modifier.height(16.dp))

    DropdownField(
        "Education Level",
        listOf("No Formal Education", "Elementary Graduate", "High School Graduate", "Vocational", "College Graduate", "Post Graduate"),
        educationLevel,
        onEducationLevelChange
    )
    Spacer(modifier = Modifier.height(16.dp))

    DropdownField(
        "Region",
        listOf("NCR", "CAR", "Region I – Ilocos", "Region II – Cagayan Valley", "Region III – Central Luzon"),
        region,
        onRegionChange
    )
    Spacer(modifier = Modifier.height(16.dp))

    DropdownField("Province", listOf("Abra", "Albay", "Batangas", "Bulacan", "Cebu", "Davao del Sur"), province, onProvinceChange)
    Spacer(modifier = Modifier.height(16.dp))

    DropdownField("City or Town", listOf("Quezon City", "Manila", "Cebu City", "Davao City", "Baguio"), cityTown, onCityTownChange)
    Spacer(modifier = Modifier.height(16.dp))

    DropdownField("Barangay", listOf("Barangay 1", "Barangay 2"), barangay, onBarangayChange)
}

@Composable
fun Step2Content(
    businessName: String,
    onBusinessNameChange: (String) -> Unit,
    businessAddress: String,
    onBusinessAddressChange: (String) -> Unit,
    businessIndustry: String,
    onBusinessIndustryChange: (String) -> Unit
) {
    Column {
        Text("Employment Information", fontSize = 20.sp, fontWeight = FontWeight.Bold)

        OutlinedTextField(
            value = businessName,
            onValueChange = onBusinessNameChange,
            label = { Text("Employer / Business Name") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = businessAddress,
            onValueChange = onBusinessAddressChange,
            label = { Text("Address") },
            modifier = Modifier.fillMaxWidth()
        )
        DropdownField("Industry", listOf("Agriculture", "Manufacturing", "Services", "Retail", "Technology", "Other"), businessIndustry, onBusinessIndustryChange)
    }
}


@Composable
fun Step3Content(
    bitmaps: Map<String, Bitmap>,
    onDocumentCaptured: (String, Bitmap?) -> Unit
) {
    val idFrontLabel = "Valid ID (Front)"
    val idBackLabel = "Valid ID (Back)"

    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Text(
            text = "IDENTIFICATION",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = "Please upload a clear photo of the front and back of a valid ID.",
            fontSize = 14.sp,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        UploadField(
            label = idFrontLabel,
            imageBitmap = bitmaps[idFrontLabel],
            onDocumentCaptured = { onDocumentCaptured(idFrontLabel, it) }
        )
        Spacer(modifier = Modifier.height(16.dp))
        UploadField(
            label = idBackLabel,
            imageBitmap = bitmaps[idBackLabel],
            onDocumentCaptured = { onDocumentCaptured(idBackLabel, it) }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownField(
    label: String,
    options: List<String>,
    selectedValue: String,
    onValueChange: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        OutlinedTextField(
            value = selectedValue,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth()
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onValueChange(option)
                        expanded = false
                    }
                )
            }
        }
    }
}


@Composable
fun UploadField(
    label: String,
    imageBitmap: Bitmap?,
    onDocumentCaptured: (Bitmap?) -> Unit
) {
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        onDocumentCaptured(bitmap)
    }

    Column {
        Text(text = label, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .border(
                    1.dp,
                    MaterialTheme.colorScheme.outline,
                    RoundedCornerShape(12.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            if (imageBitmap != null) {
                Image(
                    bitmap = imageBitmap.asImageBitmap(),
                    contentDescription = "$label preview",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                IconButton(onClick = { cameraLauncher.launch() }) {
                    Icon(
                        imageVector = Icons.Default.Create,
                        contentDescription = "Take Photo",
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}