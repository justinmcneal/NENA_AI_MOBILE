package com.example.nenaai.services

import com.example.nenaai.data.model.OTPRequest
import com.example.nenaai.data.model.OTPResponse
import com.example.nenaai.data.model.PinLoginRequest
import com.example.nenaai.data.model.PinLoginResponse
import com.example.nenaai.data.model.RegisterRequest
import com.example.nenaai.data.model.RegisterResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("register/")
    suspend fun register(@Body request: RegisterRequest): RegisterResponse

    @POST("verify-otp/")
    suspend fun verifyOtp(@Body request: OTPRequest): OTPResponse

    @POST("complete-profile/")
    suspend fun completeProfile(@Body data: Map<String, Any>): OTPResponse

    @POST("set-pin/")
    suspend fun setPin(@Body data: Map<String, Any>): OTPResponse

    @POST("login-with-pin/")
    suspend fun loginWithPin(@Body request: PinLoginRequest): PinLoginResponse
}
