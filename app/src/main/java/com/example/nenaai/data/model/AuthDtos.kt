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
    val user_status: String?,
    val is_login_flow: Boolean? // ADDED THIS LINE
)

data class User(
    val id: Int,
    val phone_number: String,
    val first_name: String,
    val middle_name: String?,
    val last_name: String,
    val verification_status: String,
    val income: Double?,
    val loan_status: String?
)

data class UserVerificationRequest(
    val date_of_birth: String,
    val gender: String,
    val civil_status: String,
    val education_level: String,
    val region: String,
    val province: String,
    val city_town: String,
    val barangay: String,
    val business_name: String,
    val business_address: String,
    val business_industry: String
)


data class BackendErrorResponse(
    val phone_number: List<String>? = null,
    val detail: String? = null,
    // Add other potential error fields here if your backend returns them
    // e.g., val email: List<String>? = null,
    // val non_field_errors: List<String>? = null
)