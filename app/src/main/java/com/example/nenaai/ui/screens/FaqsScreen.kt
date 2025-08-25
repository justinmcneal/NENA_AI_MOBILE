package com.example.nenaai.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.nenaai.ui.theme.AppTypography

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FaqsScreen(onBack: () -> Unit) {
    val faqs = listOf(
        "What is NegosyoKo BPI?" to "NegosyoKo BPI is a financial solution that helps small business owners manage their accounts, payments, and transactions with ease.",
        "How do I open a NegosyoKo account?" to "You can open a NegosyoKo account through the BPI mobile app or by visiting the nearest BPI branch with valid IDs and business documents.",
        "What are the requirements?" to "Basic requirements include a valid government-issued ID, proof of address, and business documents like DTI/SEC registration.",
        "Can I use NegosyoKo for online payments?" to "Yes, NegosyoKo supports online transfers, QR payments, and bills payment via the BPI app.",
        "Is there a maintaining balance?" to "Yes, depending on the NegosyoKo account type. Please check the latest terms and conditions with BPI.",
        "Who can apply for NegosyoKo?" to "Any Filipino entrepreneur, sole proprietor, or small business owner can apply."
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("FAQs",   style = AppTypography.titleMedium,
                    color = MaterialTheme.colorScheme.primary) },
                navigationIcon = {
                    IconButton(onClick = { onBack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.primary)

                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )

            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {
            items(faqs) { (question, answer) ->
                FaqItem(question = question, answer = answer)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun FaqItem(question: String, answer: String) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = question,
                    style = MaterialTheme.typography.titleMedium
                )
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        imageVector = if (expanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                        contentDescription = if (expanded) "Collapse" else "Expand"
                    )
                }
            }
            if (expanded) {
                Text(
                    text = answer,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}
