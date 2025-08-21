package com.example.nenaai.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nenaai.data.repository.AuthRepository
import com.example.nenaai.data.local.TokenManager
import com.example.nenaai.data.model.dto.AuthResponse
import com.example.nenaai.data.network.BackendException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    private val _phoneNumber = MutableStateFlow("")
    val phoneNumber: StateFlow<String> = _phoneNumber

    private val _oneTimeEvent = MutableSharedFlow<OneTimeEvent>()
    val oneTimeEvent: SharedFlow<OneTimeEvent> = _oneTimeEvent

    fun setPhoneNumber(newValue: String) {
        _phoneNumber.value = newValue
    }

    fun setFullPhoneNumber(fullNumber: String) {
        _phoneNumber.value = fullNumber
    }

    fun resetAuthState() {
        _authState.value = AuthState.Idle
    }

    fun registerUser(phoneNumber: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            Log.d("AuthViewModel", "Registering user with phone number: $phoneNumber")
            try {
                val response = repository.registerUser(phoneNumber)
                // Store the full phone number after successful registration
                setFullPhoneNumber(phoneNumber)
                _oneTimeEvent.emit(OneTimeEvent.Success(response))
            } catch (e: BackendException) {
                _oneTimeEvent.emit(OneTimeEvent.Error(e.message ?: "Backend error occurred"))
            } catch (e: Exception) {
                _oneTimeEvent.emit(OneTimeEvent.Error(e.message ?: "An unexpected error occurred"))
            } finally {
                _authState.value = AuthState.Idle // Reset loading state
            }
        }
    }

    fun verifyOTP(phoneNumber: String, otp: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val response = repository.verifyOTP(phoneNumber, otp)
                response.access?.let { tokenManager.saveToken(it) }
                _oneTimeEvent.emit(OneTimeEvent.Success(response))
            } catch (e: BackendException) {
                _oneTimeEvent.emit(OneTimeEvent.Error(e.message ?: "Backend error occurred"))
            } catch (e: Exception) {
                _oneTimeEvent.emit(OneTimeEvent.Error(e.message ?: "An unexpected error occurred"))
            } finally {
                _authState.value = AuthState.Idle
            }
        }
    }

    fun completeProfile(firstName: String, middleName: String?, lastName: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val response = repository.completeProfile(firstName, middleName, lastName)
                _oneTimeEvent.emit(OneTimeEvent.Success(response))
            } catch (e: BackendException) {
                _oneTimeEvent.emit(OneTimeEvent.Error(e.message ?: "Backend error occurred"))
            } catch (e: Exception) {
                _oneTimeEvent.emit(OneTimeEvent.Error(e.message ?: "An unexpected error occurred"))
            } finally {
                _authState.value = AuthState.Idle
            }
        }
    }

    

    fun loginWithPIN(phoneNumber: String, pin: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val response = repository.loginWithPIN(phoneNumber, pin)
                response.access?.let { tokenManager.saveToken(it) }
                _oneTimeEvent.emit(OneTimeEvent.Success(response))
            } catch (e: BackendException) {
                _oneTimeEvent.emit(OneTimeEvent.Error(e.message ?: "Backend error occurred"))
            } catch (e: Exception) {
                _oneTimeEvent.emit(OneTimeEvent.Error(e.message ?: "An unexpected error occurred"))
            } finally {
                _authState.value = AuthState.Idle
            }
        }
    }
}

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    // Success and Error states are now handled by OneTimeEvent
}

sealed class OneTimeEvent {
    data class Success(val authResponse: AuthResponse) : OneTimeEvent()
    data class Error(val message: String) : OneTimeEvent()
}