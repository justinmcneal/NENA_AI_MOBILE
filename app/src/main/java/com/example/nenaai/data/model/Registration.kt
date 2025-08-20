package com.example.nenaai.data.model

data class RegisterRequest(
    val first_name: String,
    val last_name: String,
    val middle_name: String,
    val phone: String,
    val pin: String
)

data class RegisterResponse(
    val success: Boolean,
    val message: String,
    val userId: Int? = null
)