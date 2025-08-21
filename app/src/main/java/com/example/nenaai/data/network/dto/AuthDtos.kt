package com.example.nenaai.data.network.dto

data class UserRegistrationRequest(
    val phone_number: String
)

data class UserRegistrationResponse(
    val success: Boolean,
    val message: String
)

data class OTPVerificationRequest(
    val phone_number: String,
    val otp: String
)

data class ProfileCompletionRequest(
    val phone_number: String,
    val name: String,
    val address: String,
    val id_photo: String // Base64 encoded image
)

data class SetPINRequest(
    val phone_number: String,
    val pin: String
)

data class LoginWithPINRequest(
    val phone_number: String,
    val pin: String
)