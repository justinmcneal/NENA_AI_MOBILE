package com.example.nenaai.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nenaai.data.AuthRepository
import com.example.nenaai.data.model.dto.AuthResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(private val repository: AuthRepository) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    fun registerUser(phoneNumber: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val response = repository.registerUser(phoneNumber)
                _authState.value = AuthState.Success(response)
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "An error occurred")
            }
        }
    }

    fun verifyOTP(phoneNumber: String, otp: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val response = repository.verifyOTP(phoneNumber, otp)
                _authState.value = AuthState.Success(response)
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "An error occurred")
            }
        }
    }

    fun completeProfile(firstName: String, middleName: String?, lastName: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val response = repository.completeProfile(firstName, middleName, lastName)
                _authState.value = AuthState.Success(response)
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "An error occurred")
            }
        }
    }

    fun setPIN(pin: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val response = repository.setPIN(pin)
                _authState.value = AuthState.Success(response)
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "An error occurred")
            }
        }
    }

    fun loginWithPIN(phoneNumber: String, pin: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val response = repository.loginWithPIN(phoneNumber, pin)
                _authState.value = AuthState.Success(response)
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "An error occurred")
            }
        }
    }
}

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val authResponse: AuthResponse) : AuthState()
    data class Error(val message: String) : AuthState()
}