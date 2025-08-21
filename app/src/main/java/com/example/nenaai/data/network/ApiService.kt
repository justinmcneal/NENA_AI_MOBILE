package com.example.nenaai.data.network

import com.example.nenaai.data.model.dto.AuthResponse
import com.example.nenaai.data.model.dto.LoginWithPINRequest
import com.example.nenaai.data.model.dto.OTPVerificationRequest
import com.example.nenaai.data.model.dto.ProfileCompletionRequest
import com.example.nenaai.data.model.dto.SetPINRequest
import com.example.nenaai.data.model.dto.UserRegistrationRequest
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("register/")
    suspend fun registerUser(@Body request: UserRegistrationRequest): AuthResponse

    @POST("verify-otp/")
    suspend fun verifyOTP(@Body request: OTPVerificationRequest): AuthResponse

    @POST("complete-profile/")
    suspend fun completeProfile(@Body request: ProfileCompletionRequest): AuthResponse

    @POST("set-pin/")
    suspend fun setPIN(@Body request: SetPINRequest): AuthResponse

    @POST("login-with-pin/")
    suspend fun loginWithPIN(@Body request: LoginWithPINRequest): AuthResponse
}