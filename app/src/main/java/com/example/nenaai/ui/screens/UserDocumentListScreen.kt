package com.example.nenaai.ui.screens

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.nenaai.viewmodel.VerificationViewModel

@Composable
fun UserDocumentListScreen(
    viewModel: VerificationViewModel = hiltViewModel()
) {
    // Observe the uploaded documents from ViewModel
    val documents by viewModel.documentBitmaps.collectAsState()

    if (documents.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = androidx.compose.ui.Alignment.Center
        ) {
            Text(
                text = "No documents uploaded yet",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(documents.entries.toList()) { entry ->
                DocumentItem(label = entry.key, bitmap = entry.value)
            }
        }
    }
}

@Composable
fun DocumentItem(label: String, bitmap: Bitmap) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = label,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "Document $label",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )
        }
    }
}
