package com.example.nenaai.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.nenaai.viewmodel.LoanStatusViewModel
import com.example.nenaai.ui.theme.AppTypography
import com.example.nenaai.ui.theme.AppShapes
import java.util.Locale

@Composable
fun HomeScreen(
    onApplyLoanClick: () -> Unit,
    loanState: LoanStatusViewModel.LoanDetailsState,
    retryFetchLoan: () -> Unit,
    retryFetchVerif: () -> Unit,
    verificationStatus: String,
    onNavigateToVerification: () -> Unit
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val scrollState = rememberScrollState()

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                retryFetchLoan()
                retryFetchVerif()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                text = "Welcome to Nena AI",
                style = AppTypography.headlineLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            when (verificationStatus) {
                in listOf("UNVERIFIED_OTP", "OTP_VERIFIED", "PROFILE_COMPLETE") -> {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        shape = AppShapes.large,
                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                text = "You need to get verified first to apply for a loan.",
                                style = AppTypography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Button(
                                onClick = { onNavigateToVerification() },
                                modifier = Modifier.fillMaxWidth(0.7f),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                            ) {
                                Text("Verify here", color = MaterialTheme.colorScheme.onPrimary)
                            }
                        }
                    }
                }
                "DOCUMENTS_PENDING" -> {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        shape = AppShapes.large,
                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondary)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                text = "Your documents are under review.",
                                style = AppTypography.titleMedium,
                                color = MaterialTheme.colorScheme.onSecondary
                            )
                            Text(
                                text = "Please wait for your verification to be approved before applying for a loan.",
                                style = AppTypography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSecondary
                            )
                        }
                    }
                }
                else -> {
                    when (loanState) {
                        is LoanStatusViewModel.LoanDetailsState.Loading -> {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp),
                                shape = AppShapes.large,
                                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(20.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "Loading loan details...",
                                        style = AppTypography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }

                        is LoanStatusViewModel.LoanDetailsState.Error -> {
                            AnimatedVisibility(visible = true) {
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 8.dp),
                                    shape = AppShapes.large,
                                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.error)
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(20.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Info,
                                            contentDescription = "Error",
                                            tint = MaterialTheme.colorScheme.onError
                                        )
                                        Text(
                                            text = "Error: ${loanState.message}",
                                            style = AppTypography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onError
                                        )
                                        Button(
                                            onClick = {
                                                retryFetchLoan()
                                                retryFetchVerif()
                                            },
                                            modifier = Modifier.padding(top = 8.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                                        ) {
                                            Text("Retry", color = MaterialTheme.colorScheme.onPrimary)
                                        }
                                    }
                                }
                            }
                        }

                        is LoanStatusViewModel.LoanDetailsState.Success -> {
                            val loanDetails = loanState.loanDetails
                            val status = loanDetails.loan_status
                            AnimatedVisibility(visible = true) {
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 8.dp),
                                    shape = AppShapes.large,
                                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(24.dp),
                                        verticalArrangement = Arrangement.spacedBy(20.dp)
                                    ) {
                                        if (status in listOf("PENDING", "ACTIVE") && loanDetails.loans.isNotEmpty()) {
                                            val latestLoan = loanDetails.loans[0]
                                            Text(
                                                text = "Loan Tracker",
                                                style = AppTypography.titleMedium,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                            HorizontalDivider(
                                                thickness = 2.dp,
                                                color = MaterialTheme.colorScheme.secondary
                                            )
                                            // Loan Details
                                            LoanDetailRow("Loan Code:", latestLoan.loan_code)
                                            LoanDetailRow("Status:", status)
                                            LoanDetailRow("Loaned Amount:", "₱${String.format(Locale.US,"%.2f", latestLoan.loaned_amount)}")
                                            LoanDetailRow("Amount Payable:", "₱${String.format(Locale.US,"%.2f", latestLoan.amount_payable)}")
                                            LoanDetailRow("Monthly Repayment:", "₱${String.format(Locale.US, "%.2f", latestLoan.monthly_repayment)}")
                                            LoanDetailRow("Months Left:", "${latestLoan.months_left} of ${latestLoan.loan_term}")
                                            LoanDetailRow("Next Payment:", latestLoan.next_repayment_due_date)
                                            LoanDetailRow(
                                                "Repayment Status:",
                                                when (latestLoan.repayment_status) {
                                                    "ON_TIME" -> "On Time"
                                                    "OVERDUE" -> "Overdue"
                                                    else -> "N/A"
                                                }
                                            )
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = "Bank Verification:",
                                                    style = AppTypography.bodyMedium,
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                                val statusColor = when (latestLoan.status) {
                                                    "APPROVED" -> MaterialTheme.colorScheme.primary
                                                    "REJECTED" -> MaterialTheme.colorScheme.error
                                                    "PENDING" -> MaterialTheme.colorScheme.secondary
                                                    else -> MaterialTheme.colorScheme.onSurface
                                                }
                                                val statusText = when (latestLoan.status) {
                                                    "APPROVED" -> "Approved"
                                                    "REJECTED" -> "Rejected"
                                                    "PENDING" -> "Pending"
                                                    else -> "Unknown"
                                                }
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    modifier = Modifier.padding(vertical = 4.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Star,
                                                        contentDescription = "Verification Status",
                                                        tint = statusColor,
                                                        modifier = Modifier.padding(end = 4.dp)
                                                    )
                                                    Text(
                                                        text = statusText,
                                                        style = AppTypography.bodyMedium,
                                                        color = statusColor
                                                    )
                                                }
                                            }
                                            Spacer(modifier = Modifier.height(12.dp))
                                            Card(
                                                modifier = Modifier.fillMaxWidth(),
                                                shape = AppShapes.medium,
                                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiary)
                                            ) {
                                                Column(
                                                    modifier = Modifier.padding(12.dp),
                                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                                ) {
                                                    Text(
                                                        text = "Next Steps",
                                                        style = AppTypography.titleMedium,
                                                        color = MaterialTheme.colorScheme.onTertiary
                                                    )
                                                    Text(
                                                        text = "Please bring your downloaded loan receipt or loan code to the nearest BPI branch to claim your loan once approved.",
                                                        style = AppTypography.bodyMedium,
                                                        color = MaterialTheme.colorScheme.onTertiary
                                                    )
                                                }
                                            }
                                            Spacer(modifier = Modifier.height(8.dp))
                                            HorizontalDivider(
                                                thickness = 2.dp,
                                                color = MaterialTheme.colorScheme.secondary
                                            )
                                            Text(
                                                text = "Repayment Progress",
                                                style = AppTypography.titleMedium,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                            val progress = if (latestLoan.loan_term > 0) {
                                                (latestLoan.loan_term - latestLoan.months_left).toFloat() / latestLoan.loan_term
                                            } else 0f
                                            LinearProgressIndicator(
                                                progress = { progress },
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(8.dp),
                                                color = MaterialTheme.colorScheme.primary,
                                                trackColor = MaterialTheme.colorScheme.surfaceVariant
                                            )
                                            Text(
                                                text = "${(progress * 100).toInt()}% Complete",
                                                style = AppTypography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onSurface,
                                                modifier = Modifier.padding(top = 4.dp)
                                            )
                                        } else {
                                            Column(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(16.dp),
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                verticalArrangement = Arrangement.spacedBy(16.dp)
                                            ) {
                                                Text(
                                                    text = "You can now apply for a loan",
                                                    style = AppTypography.bodyMedium,
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                                Button(
                                                    onClick = { onApplyLoanClick() },
                                                    modifier = Modifier.fillMaxWidth(0.7f),
                                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                                                ) {
                                                    Text("Apply for a Loan", color = MaterialTheme.colorScheme.onPrimary)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        is LoanStatusViewModel.LoanDetailsState.Idle -> {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp),
                                shape = AppShapes.large,
                                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(20.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    Text(
                                        text = "No loan data available.",
                                        style = AppTypography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "Get started by applying for a loan!",
                                        style = AppTypography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Button(
                                        onClick = { onApplyLoanClick() },
                                        modifier = Modifier.fillMaxWidth(0.7f),
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                                    ) {
                                        Text("Apply Now", color = MaterialTheme.colorScheme.onPrimary)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LoanDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = AppTypography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = value,
            style = AppTypography.bodyMedium,
            color = MaterialTheme.colorScheme.primary
        )
    }
}