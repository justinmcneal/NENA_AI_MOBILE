package com.example.nenaai.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.nenaai.viewmodel.UserAnalyticsState
import com.example.nenaai.viewmodel.UserAnalyticsViewModel
import com.example.nenaai.ui.theme.AppTypography
import com.example.nenaai.ui.theme.AppShapes
import java.util.Locale

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
                title = {
                    Text(
                        "User Analytics",
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
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
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
                            style = AppTypography.bodyMedium,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.fetchUserAnalytics() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            shape = AppShapes.medium
                        ) {
                            Text("Retry", style = AppTypography.titleMedium)
                        }
                    }
                }
                is UserAnalyticsState.Success -> {
                    val analytics = (userAnalyticsState as UserAnalyticsState.Success).analytics
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            "Financial Overview",
                            style = AppTypography.headlineLarge,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        AnalyticsItem("Total Loan Amount", "₱${String.format(Locale.US,"%.2f", analytics.total_loan_amount)}")
                        AnalyticsItem("Total Amount Repaid", "₱${String.format(Locale.US,"%.2f", analytics.total_amount_repaid)}")
                        AnalyticsItem("Last Updated", analytics.last_updated)

                        Spacer(modifier = Modifier.height(24.dp))

                        Text(
                            "Key Metrics",
                            style = AppTypography.headlineLarge,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        IncomeBarChart(
                            label = "Average Monthly Income",
                            value = analytics.average_monthly_income,
                            maxValue = 100000f
                        )

                        Spacer(modifier = Modifier.height(16.dp))

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
        shape = AppShapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(label, style = AppTypography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
            Text(value, style = AppTypography.bodyMedium, color = MaterialTheme.colorScheme.primary)
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
        shape = AppShapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(label, style = AppTypography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(16.dp),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.secondary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "₱${String.format(Locale.US,"%.2f", value)} (out of assumed max ₱${String.format(Locale.US,"%.0f", maxValue)})",
                style = AppTypography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
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
        shape = AppShapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(label, style = AppTypography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
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
                        progress > 0.7f -> Color(0xFF4CAF50) // Green
                        progress > 0.4f -> Color(0xFFFFC107) // Yellow
                        else -> Color(0xFFF44336) // Red
                    },
                    strokeWidth = 8.dp
                )
                Text(
                    "$percentage%",
                    style = AppTypography.headlineLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Score: ${String.format(Locale.US,"%.2f", score)} (1.0 = 100%)",
                style = AppTypography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}