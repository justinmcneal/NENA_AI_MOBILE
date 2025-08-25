package com.example.nenaai.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.nenaai.ui.theme.AppTypography

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactSupport(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Contact Support",
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
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Need help with BPI NegosyoKo?",
                style = MaterialTheme.typography.titleLarge
            )

            SupportOption(
                icon = Icons.Default.Call,
                title = "Call Hotline",
                description = "(+63) 2 889-10000"
            )

            SupportOption(
                icon = Icons.Default.Email,
                title = "Email Support",
                description = "negosyoko@bpi.com.ph"
            )

            SupportOption(
                icon = Icons.Default.LocationOn,
                title = "Visit Nearest Branch",
                description = "Find the closest BPI branch to assist you"
            )

            SupportOption(
                icon = Icons.Default.Public,
                title = "Official Website",
                description = "www.bpi.com.ph/negosyoko"
            )
        }
    }
}

@Composable
fun SupportOption(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, description: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = title, style = MaterialTheme.typography.titleMedium)
                Text(text = description, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
