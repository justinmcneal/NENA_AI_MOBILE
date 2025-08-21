package com.example.nenaai.data.model

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
    val phone_number: String,
    val pin: String
)

data class LoginWithPINRequest(
    val phone_number: String,
    val pin: String
)

data class ResendOTPRequest(
    val phone_number: String,
    val otp_code: String = "" // Added otp_code with a default empty string
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

data class BackendErrorResponse(
    val phone_number: List<String>? = null,
    val detail: String? = null,
    // Add other potential error fields here if your backend returns them
    // e.g., val email: List<String>? = null,
    // val non_field_errors: List<String>? = null
)