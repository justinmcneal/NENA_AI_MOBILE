package com.example.nenaai.data.model.dto

data class UserRegistrationRequest(
    val phone_number: String
)

data class OTPVerificationRequest(
    val phone_number: String,
    val otp_code: String
)

data class ProfileCompletionRequest(
    val first_name: String,
    val middle_name: String?,
    val last_name: String
)

data class SetPINRequest(
    val pin: String
)

data class LoginWithPINRequest(
    val phone_number: String,
    val pin: String
)

data class AuthResponse(
    val message: String,
    val refresh: String?,
    val access: String?,
    val user_status: String?
)

data class User(
    val phone_number: String,
    val first_name: String,
    val middle_name: String?,
    val last_name: String,
    val verification_status: String
)
