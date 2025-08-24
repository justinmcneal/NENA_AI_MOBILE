package com.example.nenaai.ui.screens

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.drawToBitmap
import com.example.nenaai.ui.components.LoanConfirmationCard
import com.example.nenaai.viewmodel.ApplyLoanViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApplyLoanScreen(
    viewModel: ApplyLoanViewModel,
    onBack: () -> Unit,
    onNavigateHome: () -> Unit
) {
    var monthlyIncome by remember { mutableStateOf("") }
    var loanAmount by remember { mutableStateOf("") }
    var loanTerm by remember { mutableStateOf("") }

    var incomeError by remember { mutableStateOf<String?>(null) }
    var amountError by remember { mutableStateOf<String?>(null) }
    var termError by remember { mutableStateOf<String?>(null) }

    val loanResponse by viewModel.loanResponse.collectAsState()
    val context = LocalContext.current

    // Dropdown state
    var expanded by remember { mutableStateOf(false) }
    val loanTermOptions = listOf("6", "12", "18", "24", "36")

    // Trigger for receipt capture
    var captureTrigger by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.statusBarsPadding(),
        topBar = {
            TopAppBar(
                title = { Text("Apply for Loan", color = MaterialTheme.colorScheme.primary) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (loanResponse == null) {
                    // Monthly Income
                    OutlinedTextField(
                        value = monthlyIncome,
                        onValueChange = {
                            monthlyIncome = it
                            incomeError = if (it.toIntOrNull() != null && it.toInt() >= 5000) null
                            else "Income must be at least 5,000"
                        },
                        label = { Text("Monthly Income") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = incomeError != null
                    )
                    incomeError?.let {
                        Text(text = it, color = Color.Red, style = MaterialTheme.typography.bodySmall)
                    }

                    Spacer(Modifier.height(8.dp))

                    // Loan Amount
                    OutlinedTextField(
                        value = loanAmount,
                        onValueChange = {
                            loanAmount = it
                            val amount = it.toIntOrNull()
                            amountError = if (amount != null && amount in 5000..300000) null
                            else "Loan amount must be between 5,000 and 300,000"
                        },
                        label = { Text("Loan Amount Request") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = amountError != null
                    )
                    amountError?.let {
                        Text(text = it, color = Color.Red, style = MaterialTheme.typography.bodySmall)
                    }

                    Spacer(Modifier.height(8.dp))

                    // Loan Term Dropdown
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {
                        OutlinedTextField(
                            value = loanTerm,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Loan Term (months)") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth(),
                            isError = termError != null
                        )

                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            loanTermOptions.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option) },
                                    onClick = {
                                        loanTerm = option
                                        termError = null
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                    termError?.let {
                        Text(text = it, color = Color.Red, style = MaterialTheme.typography.bodySmall)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Button enabled only if valid
                    val isFormValid = incomeError == null &&
                            amountError == null &&
                            termError == null &&
                            monthlyIncome.isNotBlank() &&
                            loanAmount.isNotBlank() &&
                            loanTerm.isNotBlank()

                    Button(
                        onClick = {
                            val income = monthlyIncome.toDoubleOrNull() ?: 0.0
                            val amount = loanAmount.toDoubleOrNull() ?: 0.0

                            if (income >= 5000.0 && amount in 5000.0..300000.0 && loanTerm in loanTermOptions) {
                                viewModel.applyLoan(
                                    monthlyIncome = income,
                                    loanedAmount = amount,
                                    loanTerm = loanTerm.toInt()
                                )
                            } else {
                                if (income < 5000.0) incomeError = "Income must be at least 5,000"
                                if (amount !in 5000.0..300000.0) amountError = "Loan amount must be between 5,000 and 300,000"
                                if (loanTerm.isBlank()) termError = "Please select a loan term"
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = isFormValid
                    ) {
                        Text("Submit Loan Request")
                    }
                } else {
                    LoanConfirmationCard(
                        loan = loanResponse!!,
                        onDownloadClick = { captureTrigger = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    )
                }
            }
        }
    )

    // Capture card and save to gallery
    if (captureTrigger && loanResponse != null) {
        val view = LocalView.current
        LaunchedEffect(captureTrigger) {
            delay(200)
            val bitmap = view.drawToBitmap()
            saveBitmapToGallery(context, bitmap, "LoanReceipt_${loanResponse!!.loan_code}")
            Toast.makeText(context, "Receipt saved to gallery!", Toast.LENGTH_SHORT).show()

            onNavigateHome()
            captureTrigger = false
        }
    }
}


fun saveBitmapToGallery(context: Context, bitmap: Bitmap, fileName: String) {
    val fos: java.io.OutputStream?
    try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val resolver = context.contentResolver
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, "$fileName.png")
                put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/NenaAI")
            }
            val imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            fos = imageUri?.let { resolver.openOutputStream(it) }
        } else {
            val imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES + "/NenaAI")
            if (!imagesDir.exists()) imagesDir.mkdirs()
            val image = java.io.File(imagesDir, "$fileName.png")
            fos = java.io.FileOutputStream(image)

            // Notify media scanner
            val intent = android.content.Intent(android.content.Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
            intent.data = android.net.Uri.fromFile(image)
            context.sendBroadcast(intent)
        }

        fos?.use {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
            it.flush()
        }

        Toast.makeText(context, "Receipt saved to gallery!", Toast.LENGTH_SHORT).show()
    } catch (e: Exception) {
        e.printStackTrace()
        Toast.makeText(context, "Failed to save receipt: ${e.message}", Toast.LENGTH_LONG).show()
    }
}
