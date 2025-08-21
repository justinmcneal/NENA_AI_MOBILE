package com.example.nenaai.data

import com.example.nenaai.data.network.ApiService
import com.example.nenaai.data.network.dto.LoginWithPINRequest
import com.example.nenaai.data.network.dto.OTPVerificationRequest
import com.example.nenaai.data.network.dto.ProfileCompletionRequest
import com.example.nenaai.data.network.dto.SetPINRequest
import com.example.nenaai.data.network.dto.UserRegistrationRequest
import javax.inject.Inject

class AuthRepository @Inject constructor(private val apiService: ApiService) {

    suspend fun registerUser(phoneNumber: String) = apiService.registerUser(UserRegistrationRequest(phoneNumber))

    suspend fun verifyOTP(phoneNumber: String, otp: String) = apiService.verifyOTP(OTPVerificationRequest(phoneNumber, otp))

    suspend fun completeProfile(phoneNumber: String, name: String, address: String, idPhoto: String) =
        apiService.completeProfile(ProfileCompletionRequest(phoneNumber, name, address, idPhoto))

    suspend fun setPIN(phoneNumber: String, pin: String) = apiService.setPIN(SetPINRequest(phoneNumber, pin))

    suspend fun loginWithPIN(phoneNumber: String, pin: String) = apiService.loginWithPIN(LoginWithPINRequest(phoneNumber, pin))
}
