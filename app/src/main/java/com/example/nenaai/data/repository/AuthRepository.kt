package com.example.nenaai.data.repository

import android.util.Log
import com.example.nenaai.data.network.ApiService
import com.example.nenaai.data.model.dto.LoginWithPINRequest
import com.example.nenaai.data.model.dto.OTPVerificationRequest
import com.example.nenaai.data.model.dto.ProfileCompletionRequest
import com.example.nenaai.data.model.dto.SetPINRequest
import com.example.nenaai.data.model.dto.UserRegistrationRequest
import javax.inject.Inject

class AuthRepository @Inject constructor(private val apiService: ApiService) {

    suspend fun registerUser(phoneNumber: String) : com.example.nenaai.data.model.dto.AuthResponse {
        val request = UserRegistrationRequest(phoneNumber)
        Log.d("AuthRepository", "Sending registration request: $request")
        return apiService.registerUser(request)
    }

    suspend fun verifyOTP(phoneNumber: String, otp: String) = apiService.verifyOTP(OTPVerificationRequest(phoneNumber, otp))

    suspend fun completeProfile(firstName: String, middleName: String?, lastName: String) =
        apiService.completeProfile(ProfileCompletionRequest(firstName, middleName, lastName))

    suspend fun setPIN(pin: String) = apiService.setPIN(SetPINRequest(pin))

    suspend fun loginWithPIN(phoneNumber: String, pin: String) = apiService.loginWithPIN(LoginWithPINRequest(phoneNumber, pin))
}