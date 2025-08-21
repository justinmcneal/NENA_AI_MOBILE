package com.example.nenaai.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nenaai.data.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(private val repository: AuthRepository) : ViewModel() {

    private val _registrationState = MutableStateFlow<RegistrationState>(RegistrationState.Idle)
    val registrationState: StateFlow<RegistrationState> = _registrationState

    fun registerUser(phoneNumber: String) {
        viewModelScope.launch {
            _registrationState.value = RegistrationState.Loading
            try {
                val response = repository.registerUser(phoneNumber)
                _registrationState.value = RegistrationState.Success(response.message)
            } catch (e: Exception) {
                _registrationState.value = RegistrationState.Error(e.message ?: "An error occurred")
            }
        }
    }
}

sealed class RegistrationState {
    object Idle : RegistrationState()
    object Loading : RegistrationState()
    data class Success(val message: String) : RegistrationState()
    data class Error(val message: String) : RegistrationState()
}