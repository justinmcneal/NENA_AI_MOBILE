package com.example.nenaai.data.network

import com.example.nenaai.data.model.*
import com.example.nenaai.data.model.User
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @POST("users/register/")
    suspend fun registerUser(@Body request: UserRegistrationRequest): Response<AuthResponse>

    @POST("users/verify-otp/")
    suspend fun verifyOTP(@Body request: OTPVerificationRequest): Response<AuthResponse>

    @POST("users/resend-otp/")
    suspend fun resendOTP(@Body request: ResendOTPRequest): Response<AuthResponse>

    @POST("users/complete-profile/")
    suspend fun completeProfile(@Body request: ProfileCompletionRequest): Response<AuthResponse>

    @POST("users/set-pin/")
    suspend fun setPIN(@Body request: SetPINRequest): Response<AuthResponse>

    @POST("users/login-with-pin/")
    suspend fun loginWithPIN(@Body request: LoginWithPINRequest): Response<AuthResponse>

    @GET("users/profile/")
    suspend fun getUserProfile(
        @Header("Authorization") authorization: String
    ): Response<User>

    @GET("loans/get-loan-status/")
    suspend fun checkLoanStatus(
        @Header("Authorization") token: String
    ): Response<LoanStatusResponse>

    @GET("users/fetch-loan-details/")
    suspend fun fetchLoanDetails(
        @Header("Authorization") token: String
    ): Response<LoanDetailsResponse>

    @POST("loans/apply-loan/")
    suspend fun applyLoan(
        @Body request: ApplyLoanRequest
    ): Response<LoanResponse>

    @POST("chat/")
    suspend fun postChatMessage(@Body request: ChatRequest): Response<ChatResponse>

    @POST("users/verify-details/")
    suspend fun submitVerificationDetails(
        @Header("Authorization") token: String,
        @Body request: UserVerificationRequest
    ): Response<AuthResponse>

    @Multipart
    @POST("documents/upload/")
    suspend fun uploadDocument(
        @Part("document_type") documentType: RequestBody,
        @Part file: MultipartBody.Part
    ): Response<Unit>

    @POST("analytics/income-records/")
    suspend fun createIncomeRecord(@Body request: CreateIncomeRecordRequest): Response<IncomeRecordResponse>

    @GET("analytics/income-records/")
    suspend fun getIncomeRecords(@Header("Authorization") token: String): Response<List<IncomeRecordResponse>>

    @GET("analytics/user/")
    suspend fun getUserAnalytics(@Header("Authorization") token: String): Response<UserAnalyticsResponse>

    @GET("documents/list/")
    suspend fun getUserDocuments(@Header("Authorization") token: String): Response<List<UserDocumentResponse>>
}
