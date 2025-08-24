package com.example.nenaai.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.nenaai.viewmodel.UserAnalyticsState
import com.example.nenaai.viewmodel.UserAnalyticsViewModel

@OptIn(ExperimentalMaterial3Api::class)
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
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (userAnalyticsState) {
                is UserAnalyticsState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is UserAnalyticsState.Error -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            "Error: ${(userAnalyticsState as UserAnalyticsState.Error).message}",
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.fetchUserAnalytics() }) {
                            Text("Retry")
                        }
                    }
                }
                is UserAnalyticsState.Success -> {
                    val analytics = (userAnalyticsState as UserAnalyticsState.Success).analytics
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            "Financial Overview",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        AnalyticsItem("Total Loan Amount", "₱${String.format("%.2f", analytics.total_loan_amount)}")
                        AnalyticsItem("Total Amount Repaid", "₱${String.format("%.2f", analytics.total_amount_repaid)}")
                        AnalyticsItem("Last Updated", analytics.last_updated)

                        Spacer(modifier = Modifier.height(24.dp))

                        Text(
                            "Key Metrics",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        // Average Monthly Income Bar Chart (simple horizontal bar)
                        IncomeBarChart(
                            label = "Average Monthly Income",
                            value = analytics.average_monthly_income,
                            maxValue = 100000f // Assumed max for visualization; adjust based on context
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Business Consistency Score Gauge
                        ConsistencyGauge(
                            label = "Business Consistency Score",
                            score = analytics.business_consistency_score
                        )
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
        elevation = CardDefaults.cardElevation(4.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(label, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(value, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
fun IncomeBarChart(label: String, value: Double, maxValue: Float) {
    val progress = (value / maxValue).coerceIn(0.0, 1.0).toFloat()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(label, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(16.dp),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "₱${String.format("%.2f", value)} (out of assumed max ₱${String.format("%.0f", maxValue)})",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.End,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun ConsistencyGauge(label: String, score: Double) {
    val progress = score.toFloat().coerceIn(0f, 1f)
    val percentage = (score * 100).toInt()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(label, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(120.dp)
                    .align(Alignment.CenterHorizontally)
            ) {
                CircularProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxSize(),
                    color = when {
                        progress > 0.7f -> Color.Green
                        progress > 0.4f -> Color.Yellow
                        else -> Color.Red
                    },
                    strokeWidth = 8.dp
                )
                Text(
                    "$percentage%",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Score: ${String.format("%.2f", score)} (1.0 = 100%)",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}