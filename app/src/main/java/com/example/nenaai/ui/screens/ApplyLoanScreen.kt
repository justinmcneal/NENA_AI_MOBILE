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
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.drawToBitmap
import com.example.nenaai.ui.components.LoanConfirmationCard
import com.example.nenaai.viewmodel.ApplyLoanViewModel
import kotlinx.coroutines.delay

@Composable
fun ApplyLoanScreen(
    viewModel: ApplyLoanViewModel,
    onBack: () -> Unit,
    onNavigateHome: () -> Unit
) {
    var monthlyIncome by remember { mutableStateOf("") }
    var loanAmount by remember { mutableStateOf("") }
    var loanTerm by remember { mutableStateOf("") }

    val loanResponse by viewModel.loanResponse.collectAsState()
    val error by viewModel.error.collectAsState()
    val context = LocalContext.current

    // Trigger to capture receipt
    var captureTrigger by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Back button
        Button(
            onClick = onBack,
            modifier = Modifier.align(Alignment.Start)
        ) {
            Text("Back")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Loan form (only if no response yet)
        if (loanResponse == null) {
            OutlinedTextField(
                value = monthlyIncome,
                onValueChange = { monthlyIncome = it },
                label = { Text("Monthly Income") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = loanAmount,
                onValueChange = { loanAmount = it },
                label = { Text("Loan Amount Request") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = loanTerm,
                onValueChange = { loanTerm = it },
                label = { Text("Loan Term (months)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (monthlyIncome.isNotEmpty() && loanAmount.isNotEmpty() && loanTerm.isNotEmpty()) {
                        viewModel.applyLoan(
                            monthlyIncome.toDouble(),
                            loanAmount.toDouble(),
                            loanTerm.toInt()
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Submit Loan Application")
            }

            error?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Text("Error: $it", color = Color.Red)
            }
        } else {
            // Show Loan Confirmation Card
            LoanConfirmationCard(
                loan = loanResponse!!,
                onDownloadClick = { captureTrigger = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
        }
    }

    // Capture card and save to gallery
    if (captureTrigger && loanResponse != null) {
        val view = LocalView.current
        LaunchedEffect(captureTrigger) {
            // Small delay to ensure recomposition
            delay(200)
            val bitmap = view.drawToBitmap()
            saveBitmapToGallery(context, bitmap, "LoanReceipt_${loanResponse!!.loan_code}")
            Toast.makeText(context, "Receipt saved to gallery!", Toast.LENGTH_SHORT).show()

            // Navigate home after download
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
