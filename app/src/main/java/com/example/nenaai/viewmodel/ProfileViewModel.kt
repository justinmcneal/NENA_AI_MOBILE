package com.example.nenaai.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nenaai.data.local.TokenManager
import com.example.nenaai.data.model.User
import com.example.nenaai.data.network.BackendException
import com.example.nenaai.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _profileState = MutableStateFlow<ProfileState>(ProfileState.Idle)
    val profileState: StateFlow<ProfileState> = _profileState

    private val _oneTimeEvent = MutableSharedFlow<ProfileOneTimeEvent>()
    val oneTimeEvent: SharedFlow<ProfileOneTimeEvent> = _oneTimeEvent

    private val _userProfile = MutableStateFlow<User?>(null)
    val userProfile: StateFlow<User?> = _userProfile

    fun fetchUserProfile() {
        viewModelScope.launch {
            _profileState.value = ProfileState.Loading
            try {
                val token = tokenManager.getToken()
                if (token.isNullOrEmpty()) {
                    throw Exception("No access token available")
                }

                val user = authRepository.getUserProfile(token)
                _userProfile.value = user
                _profileState.value = ProfileState.Success
            } catch (e: BackendException) {
                _profileState.value = ProfileState.Error(e.message ?: "Backend error occurred")
                _oneTimeEvent.emit(ProfileOneTimeEvent.Error(e.message ?: "Backend error"))
            } catch (e: Exception) {
                _profileState.value = ProfileState.Error(e.message ?: "Unexpected error")
                _oneTimeEvent.emit(ProfileOneTimeEvent.Error(e.message ?: "Unexpected error"))
            }
        }
    }


    fun setPIN(phoneNumber: String, pin: String) {
        viewModelScope.launch {
            _profileState.value = ProfileState.Loading
            try {
                val response = authRepository.setPIN(phoneNumber, pin)
                tokenManager.saveAuthFlowState(TokenManager.KEY_PIN_SET, true) // Save PIN set state
                _oneTimeEvent.emit(ProfileOneTimeEvent.Success(response.message))
            } catch (e: BackendException) {
                _oneTimeEvent.emit(ProfileOneTimeEvent.Error(e.message ?: "Backend error occurred"))
            } catch (e: Exception) {
                _oneTimeEvent.emit(ProfileOneTimeEvent.Error(e.message ?: "An unexpected error occurred"))
            } finally {
                _profileState.value = ProfileState.Idle
            }
        }
    }
}

sealed class ProfileState {
    object Idle : ProfileState()
    object Loading : ProfileState()
    object Success : ProfileState()
    data class Error(val message: String) : ProfileState()
}

sealed class ProfileOneTimeEvent {
    data class Success(val message: String) : ProfileOneTimeEvent()
    data class Error(val message: String) : ProfileOneTimeEvent()
}