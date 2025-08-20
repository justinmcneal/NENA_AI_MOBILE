package com.example.nenaai.viewmodel

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import androidx.lifecycle.ViewModel
import com.example.nenaai.data.model.OTPRequest
import com.example.nenaai.data.model.PinLoginRequest
import com.example.nenaai.data.model.RegisterRequest
import com.example.nenaai.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AuthViewModel(
    private val repository: AuthRepository
) : ViewModel() {

    private val _status = MutableStateFlow("")
    val status: StateFlow<String> get() = _status

    fun register(first: String, last: String, middle: String, phone: String, pin: String) {
        viewModelScope.launch {
            try {
                val response = repository.register(RegisterRequest(first, last, middle, phone, pin))
                _status.value = response.message
            } catch (e: Exception) {
                _status.value = "Error: ${e.localizedMessage}"
            }
        }
    }

    fun verifyOtp(userId: Int, otp: String) {
        viewModelScope.launch {
            try {
                val response = repository.verifyOtp(OTPRequest(userId, otp))
                _status.value = response.message
            } catch (e: Exception) {
                _status.value = "Error: ${e.localizedMessage}"
            }
        }
    }

    fun loginWithPin(phone: String, pin: String) {
        viewModelScope.launch {
            try {
                val response = repository.loginWithPin(PinLoginRequest(phone, pin))
                _status.value = if (response.success) "Login success!" else response.message
            } catch (e: Exception) {
                _status.value = "Error: ${e.localizedMessage}"
            }
        }
    }
}
