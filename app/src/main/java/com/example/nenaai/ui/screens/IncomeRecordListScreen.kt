package com.example.nenaai.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.* // Import all Material3 components
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.nenaai.data.model.IncomeRecordResponse
import com.example.nenaai.viewmodel.IncomeRecordListViewModel

@OptIn(ExperimentalMaterial3Api::class) // Add this annotation
@Composable
fun IncomeRecordListScreen(
    viewModel: IncomeRecordListViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val incomeRecords by viewModel.incomeRecords.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Income Records") },
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
                Button(onClick = { viewModel.fetchIncomeRecords() }) {
                    Text("Retry")
                }
            } else if (incomeRecords.isEmpty()) {
                Text("No income records found.")
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(incomeRecords) {
                        IncomeRecordItem(record = it)
                    }
                }
            }
        }
    }
}

@Composable
fun IncomeRecordItem(record: IncomeRecordResponse) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Amount: ${record.amount}", style = MaterialTheme.typography.titleMedium)
            Text("Date: ${record.record_date}", style = MaterialTheme.typography.bodyMedium)
            if (!record.notes.isNullOrBlank()) {
                Text("Notes: ${record.notes}", style = MaterialTheme.typography.bodySmall)
            }
            Text("Recorded: ${record.created_at}", style = MaterialTheme.typography.bodySmall)
        }
    }
}
