package com.example.nenaai.ui.screens

import android.app.DatePickerDialog
import android.widget.DatePicker
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.nenaai.ui.components.CommonSnackbar
import com.example.nenaai.viewmodel.AddIncomeRecordEvent
import com.example.nenaai.viewmodel.IncomeRecordViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddIncomeRecordScreen(
    viewModel: IncomeRecordViewModel = hiltViewModel(),
    onRecordAdded: () -> Unit
) {
    var amount by remember { mutableStateOf("") }
    var recordDate by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.addIncomeRecordEvent.collect {
            when (it) {
                is AddIncomeRecordEvent.Success -> {
                    Toast.makeText(context, "Income record added successfully!", Toast.LENGTH_SHORT).show()
                    onRecordAdded()
                }
                is AddIncomeRecordEvent.Error -> {
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = it.message,
                            withDismissAction = true
                        )
                    }
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Add New Income Record",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        OutlinedTextField(
            value = amount,
            onValueChange = { newValue ->
                if (newValue.all { it.isDigit() || it == '.' } && newValue.count { it == '.' } <= 1) {
                    amount = newValue
                }
            },
            label = { Text("Amount") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        OutlinedTextField(
            value = recordDate,
            onValueChange = {},
            label = { Text("Record Date (YYYY-MM-DD)") },
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = {
                    DatePickerDialog(context, { _: DatePicker, selectedYear: Int, selectedMonth: Int, selectedDayOfMonth: Int ->
                        val selectedDate = Calendar.getInstance().apply {
                            set(selectedYear, selectedMonth, selectedDayOfMonth)
                        }
                        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        recordDate = formatter.format(selectedDate.time)
                    }, year, month, day).show()
                }) {
                    Icon(Icons.Default.DateRange, contentDescription = "Select Date")
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = notes,
            onValueChange = { notes = it },
            label = { Text("Notes (Optional)") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                val parsedAmount = amount.toDoubleOrNull()
                if (parsedAmount != null && recordDate.isNotBlank()) {
                    viewModel.createIncomeRecord(parsedAmount, recordDate, notes.ifBlank { null })
                } else {
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = "Please enter a valid amount and select a record date.",
                            withDismissAction = true
                        )
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add Record")
        }
    }
    CommonSnackbar(snackbarHostState = snackbarHostState)
}
