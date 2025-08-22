package com.example.nenaai.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.nenaai.viewmodel.ApplyLoanViewModel

@Composable
fun ApplyLoanScreen(
    viewModel: ApplyLoanViewModel,
    onBack: () -> Unit
) {
    var monthlyIncome by remember { mutableStateOf("") }
    var loanAmount by remember { mutableStateOf("") }
    var loanTerm by remember { mutableStateOf("") }

    val loanResponse by viewModel.loanResponse.collectAsState()
    val error by viewModel.error.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        OutlinedTextField(
            value = monthlyIncome,
            onValueChange = { monthlyIncome = it },
            label = { Text("Monthly Income") }
        )

        OutlinedTextField(
            value = loanAmount,
            onValueChange = { loanAmount = it },
            label = { Text("Loan Amount Request") }
        )

        OutlinedTextField(
            value = loanTerm,
            onValueChange = { loanTerm = it },
            label = { Text("Loan Term (months)") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            if (monthlyIncome.isNotEmpty() && loanAmount.isNotEmpty() && loanTerm.isNotEmpty()) {
                viewModel.applyLoan(
                    monthlyIncome.toDouble(),
                    loanAmount.toDouble(),
                    loanTerm.toInt()
                )
            }
        }) {
            Text("Submit Loan Application")
        }

        Spacer(modifier = Modifier.height(16.dp))

        loanResponse?.let {
            Text("Loan Code: ${it.loan_code}")
            Text("Loaned Amount: ${it.loaned_amount}")
            Text("Monthly Repayment: ${it.monthly_repayment}")
            Text("Months Left: ${it.months_left}")
        }

        error?.let {
            Text("Error: $it", color = Color.Red)
        }
    }
}

