package com.example.nenaai.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.nenaai.data.model.IncomeRecordResponse
import com.example.nenaai.viewmodel.IncomeRecordListViewModel
import com.example.nenaai.ui.theme.AppTypography
import com.example.nenaai.ui.theme.AppShapes

@OptIn(ExperimentalMaterial3Api::class)
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
                title = {
                    Text(
                        "Income Records",
                        style = AppTypography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            if (loading) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            } else if (error != null) {
                Text(
                    "Error: $error",
                    color = MaterialTheme.colorScheme.error,
                    style = AppTypography.bodyMedium
                )
                Button(
                    onClick = { viewModel.fetchIncomeRecords() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    shape = AppShapes.medium,
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text("Retry", style = AppTypography.titleMedium)
                }
            } else if (incomeRecords.isEmpty()) {
                Text(
                    "No income records found.",
                    style = AppTypography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
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
        shape = AppShapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Amount: ${record.amount}",
                style = AppTypography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                "Date: ${record.record_date}",
                style = AppTypography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (!record.notes.isNullOrBlank()) {
                Text(
                    "Notes: ${record.notes}",
                    style = AppTypography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
            Text(
                "Recorded: ${record.created_at}",
                style = AppTypography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}