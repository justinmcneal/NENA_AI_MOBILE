package com.example.nenaai.data.model

// Represents a single message in the chat UI
data class ChatMessage(
    val text: String,
    val isFromUser: Boolean
)

// Data sent to the backend API
data class ChatRequest(
    val message: String
)

// Data received from the backend API
data class ChatResponse(
    val reply: String
)