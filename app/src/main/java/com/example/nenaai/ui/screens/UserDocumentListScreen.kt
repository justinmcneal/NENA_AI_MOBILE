package com.example.nenaai.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.* // Import all Material3 components
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.nenaai.data.model.UserDocumentResponse
import com.example.nenaai.viewmodel.UserDocumentListViewModel

@OptIn(ExperimentalMaterial3Api::class) // Add this annotation
@Composable
fun UserDocumentListScreen(
    viewModel: UserDocumentListViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val userDocuments by viewModel.userDocuments.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Documents") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (loading) {
                CircularProgressIndicator()
            } else if (error != null) {
                Text("Error: ${error}", color = MaterialTheme.colorScheme.error)
                Button(onClick = { viewModel.fetchUserDocuments() }) {
                    Text("Retry")
                }
            } else if (userDocuments.isEmpty()) {
                Text("No documents uploaded yet.")
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(userDocuments) {
                        UserDocumentItem(document = it)
                    }
                }
            }
        }
    }
}

@Composable
fun UserDocumentItem(document: UserDocumentResponse) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Document Type: ${document.document_type}", style = MaterialTheme.typography.titleMedium)
            Text("Status: ${document.analysis_status}", style = MaterialTheme.typography.bodyMedium, color = when(document.analysis_status) {
                "VERIFIED" -> Color.Green
                "REJECTED" -> Color.Red
                "UNREADABLE" -> Color.Red
                else -> Color.Gray
            })
            Text("Uploaded: ${document.uploaded_at}", style = MaterialTheme.typography.bodySmall)
            // You might want to add an image preview or a link to view the document here
        }
    }
}
