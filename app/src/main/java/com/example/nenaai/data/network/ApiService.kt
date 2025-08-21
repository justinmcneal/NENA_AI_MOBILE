package com.example.nenaai.data.network

import com.example.nenaai.data.network.dto.LoginWithPINRequest
import com.example.nenaai.data.network.dto.OTPVerificationRequest
import com.example.nenaai.data.network.dto.ProfileCompletionRequest
import com.example.nenaai.data.network.dto.SetPINRequest
import com.example.nenaai.data.network.dto.UserRegistrationRequest
import com.example.nenaai.data.network.dto.UserRegistrationResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("register/")
    suspend fun registerUser(@Body request: UserRegistrationRequest): UserRegistrationResponse

    @POST("verify-otp/")
    suspend fun verifyOTP(@Body request: OTPVerificationRequest)

    @POST("complete-profile/")
    suspend fun completeProfile(@Body request: ProfileCompletionRequest)

    @POST("set-pin/")
    suspend fun setPIN(@Body request: SetPINRequest)

    @POST("login-with-pin/")
    suspend fun loginWithPIN(@Body request: LoginWithPINRequest)
}