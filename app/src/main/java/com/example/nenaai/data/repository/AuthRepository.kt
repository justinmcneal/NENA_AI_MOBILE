package com.example.nenaai.data.repository

import com.example.nenaai.data.model.OTPRequest
import com.example.nenaai.data.model.OTPResponse
import com.example.nenaai.data.model.PinLoginRequest
import com.example.nenaai.data.model.PinLoginResponse
import com.example.nenaai.data.model.RegisterRequest
import com.example.nenaai.data.model.RegisterResponse
import com.example.nenaai.services.ApiClient
import com.example.nenaai.services.ApiService

class AuthRepository {
    private val api: ApiService = ApiClient.api

    suspend fun register(request: RegisterRequest): RegisterResponse =
        api.register(request)

    suspend fun verifyOtp(request: OTPRequest): OTPResponse =
        api.verifyOtp(request)

    suspend fun completeProfile(data: Map<String, Any>): OTPResponse =
        api.completeProfile(data)

    suspend fun setPin(data: Map<String, Any>): OTPResponse =
        api.setPin(data)

    suspend fun loginWithPin(request: PinLoginRequest): PinLoginResponse =
        api.loginWithPin(request)
}
