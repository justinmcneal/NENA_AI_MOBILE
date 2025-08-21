package com.example.nenaai.data.repository

import android.util.Log
import com.example.nenaai.data.network.ApiService
import com.example.nenaai.data.model.dto.LoginWithPINRequest
import com.example.nenaai.data.model.dto.OTPVerificationRequest
import com.example.nenaai.data.model.dto.ProfileCompletionRequest
import com.example.nenaai.data.model.dto.SetPINRequest
import com.example.nenaai.data.model.dto.UserRegistrationRequest
import com.example.nenaai.data.model.dto.AuthResponse
import com.example.nenaai.data.model.dto.BackendErrorResponse
import com.example.nenaai.data.network.BackendException
import com.google.gson.Gson
import retrofit2.Response
import javax.inject.Inject

class AuthRepository @Inject constructor(private val apiService: ApiService) {

    private fun <T> handleApiResponse(response: Response<T>): T {
        if (response.isSuccessful) {
            return response.body()!!
        } else {
            val errorBody = response.errorBody()?.string()
            val errorMessage = try {
                val errorResponse = Gson().fromJson(errorBody, BackendErrorResponse::class.java)
                errorResponse.phone_number?.firstOrNull() ?: errorResponse.detail ?: "Unknown error"
            } catch (e: Exception) {
                errorBody ?: "Unknown error"
            }
            Log.e("AuthRepository", "API Error: ${response.code()} - $errorMessage")
            throw BackendException(errorMessage, response.code(), errorBody)
        }
    }

    suspend fun registerUser(phoneNumber: String) : AuthResponse {
        val request = UserRegistrationRequest(phoneNumber)
        Log.d("AuthRepository", "Sending registration request: $request")
        val response = apiService.registerUser(request)
        return handleApiResponse(response)
    }

    suspend fun verifyOTP(phoneNumber: String, otp: String) : AuthResponse {
        val response = apiService.verifyOTP(OTPVerificationRequest(phoneNumber, otp))
        return handleApiResponse(response)
    }

    suspend fun completeProfile(firstName: String, middleName: String?, lastName: String) : AuthResponse {
        val response = apiService.completeProfile(ProfileCompletionRequest(firstName, middleName, lastName))
        return handleApiResponse(response)
    }

    suspend fun setPIN(pin: String) : AuthResponse {
        val response = apiService.setPIN(SetPINRequest(pin))
        return handleApiResponse(response)
    }

    suspend fun loginWithPIN(phoneNumber: String, pin: String) : AuthResponse {
        val response = apiService.loginWithPIN(LoginWithPINRequest(phoneNumber, pin))
        return handleApiResponse(response)
    }
}