package com.example.nenaai.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.* // Import all Material3 components
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.nenaai.viewmodel.UserAnalyticsState
import com.example.nenaai.viewmodel.UserAnalyticsViewModel

@OptIn(ExperimentalMaterial3Api::class) // Add this annotation
@Composable
fun UserAnalyticsScreen(
    viewModel: UserAnalyticsViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val userAnalyticsState by viewModel.userAnalytics.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("User Analytics") },
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
            when (userAnalyticsState) {
                is UserAnalyticsState.Loading -> {
                    CircularProgressIndicator()
                }
                is UserAnalyticsState.Error -> {
                    Text("Error: ${(userAnalyticsState as UserAnalyticsState.Error).message}", color = MaterialTheme.colorScheme.error)
                    Button(onClick = { viewModel.fetchUserAnalytics() }) {
                        Text("Retry")
                    }
                }
                is UserAnalyticsState.Success -> {
                    val analytics = (userAnalyticsState as UserAnalyticsState.Success).analytics
                    Column(modifier = Modifier.fillMaxWidth()) {
                        AnalyticsItem("Total Loan Amount", "₱${String.format("%.2f", analytics.total_loan_amount)}")
                        AnalyticsItem("Total Amount Repaid", "₱${String.format("%.2f", analytics.total_amount_repaid)}")
                        AnalyticsItem("Average Monthly Income", "₱${String.format("%.2f", analytics.val_average_monthly_income)}")
                        AnalyticsItem("Business Consistency Score", "${String.format("%.2f", analytics.business_consistency_score)}")
                        AnalyticsItem("Last Updated", analytics.last_updated)
                    }
                }
            }
        }
    }
}

@Composable
fun AnalyticsItem(label: String, value: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(label, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(value, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
