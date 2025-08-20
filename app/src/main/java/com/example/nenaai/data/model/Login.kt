package com.example.nenaai.data.model

data class PinLoginRequest(
    val phone: String,
    val pin: String
)

data class PinLoginResponse(
    val success: Boolean,
    val token: String? = null,
    val message: String
)