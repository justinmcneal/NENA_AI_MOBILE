package com.example.nenaai.data.network

import com.example.nenaai.data.model.*
import com.example.nenaai.data.model.User
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @POST("register/")
    suspend fun registerUser(@Body request: UserRegistrationRequest): Response<AuthResponse>

    @POST("verify-otp/")
    suspend fun verifyOTP(@Body request: OTPVerificationRequest): Response<AuthResponse>

    @POST("resend-otp/")
    suspend fun resendOTP(@Body request: ResendOTPRequest): Response<AuthResponse>

    @POST("complete-profile/")
    suspend fun completeProfile(@Body request: ProfileCompletionRequest): Response<AuthResponse>

    @POST("set-pin/")
    suspend fun setPIN(@Body request: SetPINRequest): Response<AuthResponse>

    @POST("login-with-pin/")
    suspend fun loginWithPIN(@Body request: LoginWithPINRequest): Response<AuthResponse>

    @GET("profile/")
    suspend fun getUserProfile(
        @Header("Authorization") authorization: String
    ): Response<User>

    @POST("chat/")
    suspend fun postChatMessage(@Body request: ChatRequest): Response<ChatResponse>

    @Multipart
    @POST("upload-document/")
    suspend fun uploadDocument(
        @Part("label") label: RequestBody,
        @Part document: MultipartBody.Part
    ): Response<Unit> // Assuming the backend returns an empty success response
}