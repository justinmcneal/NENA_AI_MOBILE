package com.example.nenaai.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nenaai.data.repository.AuthRepository
import com.example.nenaai.data.local.TokenManager
import com.example.nenaai.data.model.AuthResponse
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
    val tokenManager: TokenManager
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    private val _phoneNumber = MutableStateFlow("")
    val phoneNumber: StateFlow<String> = _phoneNumber

    private val _oneTimeEvent = MutableSharedFlow<OneTimeEvent>()
    val oneTimeEvent: SharedFlow<OneTimeEvent> = _oneTimeEvent

    private val _navigationEvent = MutableSharedFlow<NavigationEvent>()
    val navigationEvent: SharedFlow<NavigationEvent> = _navigationEvent

    init { // ADDED init block
        tokenManager.getPhoneNumber()?.let {
            _phoneNumber.value = it
        }
    }

    fun setPhoneNumber(newValue: String) {
        // Ensure +63 prefix is always present when setting the phone number
        _phoneNumber.value = if (newValue.startsWith("+63")) newValue else "+63$newValue"
    }

    fun resetAuthState() {
        _authState.value = AuthState.Idle
    }

    fun clearPhoneNumber() {
        _phoneNumber.value = ""
        tokenManager.clearAuthFlowState() // This already clears KEY_PHONE_NUMBER
    }

    fun registerUser(phoneNumber: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            tokenManager.savePhoneNumber(phoneNumber) // Save phone number to TokenManager
            Log.d("AuthViewModel", "Registering user with phone number: $phoneNumber")
            try {
                val response = repository.registerUser(phoneNumber)
                if (response.is_login_flow == true) {
                    _navigationEvent.emit(NavigationEvent.ToPinLogin)
                } else {
                    tokenManager.saveAuthFlowState(TokenManager.KEY_OTP_SENT, true) // Save OTP sent state
                    _navigationEvent.emit(NavigationEvent.ToOtpVerification)
                }
                _oneTimeEvent.emit(OneTimeEvent.Success(response))
                _authState.value = AuthState.Idle
            } catch (e: BackendException) {
                _oneTimeEvent.emit(OneTimeEvent.Error(e.message ?: "Backend error occurred"))
                _authState.value = AuthState.Idle
            } catch (e: Exception) {
                _oneTimeEvent.emit(OneTimeEvent.Error(e.message ?: "An unexpected error occurred"))
                _authState.value = AuthState.Idle
            }
        }
    }

    fun verifyOTP(phoneNumber: String, otp: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            Log.d("AuthViewModel", "Verifying OTP for phone number: $phoneNumber, OTP: $otp")
            try {
                val response = repository.verifyOTP(phoneNumber, otp)
                response.access?.let { tokenManager.saveToken(it) }
                tokenManager.saveAuthFlowState(TokenManager.KEY_OTP_VERIFIED, true) // Save OTP verified state
                _oneTimeEvent.emit(OneTimeEvent.Success(response))
                _authState.value = AuthState.Idle
            } catch (e: BackendException) {
                _oneTimeEvent.emit(OneTimeEvent.Error(e.message ?: "Backend error occurred"))
                _authState.value = AuthState.Idle
            } catch (e: Exception) {
                _oneTimeEvent.emit(OneTimeEvent.Error(e.message ?: "An unexpected error occurred"))
                _authState.value = AuthState.Idle
            }
        }
    }

    fun resendOTP(phoneNumber: String, otpCode: String = "") {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            tokenManager.savePhoneNumber(phoneNumber) // Save phone number to TokenManager
            Log.d("AuthViewModel", "Resending OTP for phone number: $phoneNumber")
            try {
                val response = repository.resendOTP(phoneNumber, otpCode)
                tokenManager.saveAuthFlowState(TokenManager.KEY_OTP_SENT, true) // Save OTP sent state again
                _oneTimeEvent.emit(OneTimeEvent.Success(response))
                _authState.value = AuthState.Idle
            } catch (e: BackendException) {
                _oneTimeEvent.emit(OneTimeEvent.Error(e.message ?: "Backend error occurred"))
                _authState.value = AuthState.Idle
            } catch (e: Exception) {
                _oneTimeEvent.emit(OneTimeEvent.Error(e.message ?: "An unexpected error occurred"))
                _authState.value = AuthState.Idle
            }
        }
    }

    fun completeProfile(firstName: String, middleName: String?, lastName: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val response = repository.completeProfile(firstName, middleName, lastName)
                tokenManager.saveAuthFlowState(TokenManager.KEY_PROFILE_COMPLETE, true) // Save profile complete state
                _oneTimeEvent.emit(OneTimeEvent.Success(response))
                _authState.value = AuthState.Idle
            } catch (e: BackendException) {
                _oneTimeEvent.emit(OneTimeEvent.Error(e.message ?: "Backend error occurred"))
                _authState.value = AuthState.Idle
            } catch (e: Exception) {
                _oneTimeEvent.emit(OneTimeEvent.Error(e.message ?: "An unexpected error occurred"))
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
                // On successful login, clear all previous auth flow states as the user is now logged in
                tokenManager.clearAuthFlowState()
                _oneTimeEvent.emit(OneTimeEvent.Success(response))
                _authState.value = AuthState.Idle
            } catch (e: BackendException) {
                _oneTimeEvent.emit(OneTimeEvent.Error(e.message ?: "Backend error occurred"))
                _authState.value = AuthState.Idle
            } catch (e: Exception) {
                _oneTimeEvent.emit(OneTimeEvent.Error(e.message ?: "An unexpected error occurred"))
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

sealed class NavigationEvent {
    object ToOtpVerification : NavigationEvent()
    object ToPinLogin : NavigationEvent()
}