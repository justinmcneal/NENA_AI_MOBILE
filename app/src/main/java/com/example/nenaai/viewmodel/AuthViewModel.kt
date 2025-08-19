package com.example.nenaai.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    private val _phoneNumber = MutableStateFlow("")
    val phoneNumber: StateFlow<String> = _phoneNumber

    private val _otpSent = MutableStateFlow(false)
    val otpSent: StateFlow<Boolean> = _otpSent

    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun setPhoneNumber(number: String) {
        _phoneNumber.value = number
    }

    fun sendOtp(number: String) {
        viewModelScope.launch {
            _error.value = null
            if (number.length == 10) { // Simple validation for demo
                _otpSent.value = true
                // Simulate OTP sending
                delay(1000) // Simulate network delay
                _error.value = null // Clear any previous error
            } else {
                _error.value = "Please enter a valid 10-digit phone number."
            }
        }
    }

    fun verifyOtp(otp: String) {
        viewModelScope.launch {
            _error.value = null
            if (otp == "123456") { // Hardcoded OTP for demo
                _isAuthenticated.value = true
                _otpSent.value = false // Reset OTP state
            } else {
                _error.value = "Invalid OTP. Please try again."
            }
        }
    }

    fun resetOtpState() {
        _otpSent.value = false
        _error.value = null
    }

    fun clearError() {
        _error.value = null
    }
}
