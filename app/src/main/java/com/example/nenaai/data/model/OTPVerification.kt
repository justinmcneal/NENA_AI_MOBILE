package com.example.nenaai.data.model

data class OTPRequest(
    val userId: Int,
    val otp: String
)

data class OTPResponse(
    val success: Boolean,
    val message: String
)
