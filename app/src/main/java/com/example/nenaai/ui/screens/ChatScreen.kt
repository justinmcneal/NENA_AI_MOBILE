package com.example.nenaai.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nenaai.data.model.ChatMessage
import com.example.nenaai.ui.theme.NENA_AI_MOBILETheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    messages: List<ChatMessage>,
    onSendMessage: (String) -> Unit
) {
    var textInput by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "NENA AI Assistant",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        bottomBar = {
            MessageInput(
                text = textInput,
                onTextChange = { textInput = it },
                onSendMessage = {
                    onSendMessage(textInput)
                    textInput = ""
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 12.dp),
            reverseLayout = true
        ) {
            items(messages.reversed()) { message ->
                MessageBubble(message = message)
                Spacer(modifier = Modifier.height(6.dp))
            }
        }
    }
}

@Composable
fun MessageBubble(message: ChatMessage) {
    val bubbleColor =
        if (message.isFromUser) MaterialTheme.colorScheme.primary
        else MaterialTheme.colorScheme.surfaceVariant
    val textColor =
        if (message.isFromUser) MaterialTheme.colorScheme.onPrimary
        else MaterialTheme.colorScheme.onSurfaceVariant
    val alignment =
        if (message.isFromUser) Alignment.CenterEnd else Alignment.CenterStart

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = if (message.isFromUser) 48.dp else 0.dp,
                end = if (message.isFromUser) 0.dp else 48.dp
            ),
        contentAlignment = alignment
    ) {
        Surface(
            color = bubbleColor,
            shape = RoundedCornerShape(18.dp),
            tonalElevation = 2.dp,
            shadowElevation = 2.dp
        ) {
            Text(
                text = message.text,
                color = textColor,
                modifier = Modifier.padding(12.dp),
                fontSize = 16.sp
            )
        }
    }
}

@Composable
fun MessageInput(
    text: String,
    onTextChange: (String) -> Unit,
    onSendMessage: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(
            value = text,
            onValueChange = onTextChange,
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(24.dp)),
            placeholder = { Text("Ask NENA a question...") },
            singleLine = true,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            )
        )
        Spacer(modifier = Modifier.width(8.dp))
        IconButton(
            onClick = onSendMessage,
            enabled = text.isNotBlank(),
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary)
        ) {
            Icon(
                Icons.AutoMirrored.Filled.Send,
                contentDescription = "Send Message",
                tint = Color.White
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun ChatScreenPreview() {
    NENA_AI_MOBILETheme {
        val sampleMessages = listOf(
            ChatMessage("Hello! How can I help you with your business loan today?", isFromUser = false),
            ChatMessage("Paano po mag-apply ng loan?", isFromUser = true),
            ChatMessage("Madali lang po! First, you need to prepare your valid ID and proof of income.", isFromUser = false),
            ChatMessage("Salamat po!", isFromUser = true)
        )
        ChatScreen(messages = sampleMessages, onSendMessage = {})
    }
}
