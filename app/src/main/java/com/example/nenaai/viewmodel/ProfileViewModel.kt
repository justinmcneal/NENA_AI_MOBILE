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

    // This would ideally come from a dedicated API call to fetch user profile
    // For now, we'll use a placeholder or data from AuthResponse if available
    private val _userProfile = MutableStateFlow<User?>(null)
    val userProfile: StateFlow<User?> = _userProfile

    fun fetchUserProfile() {
        // In a real app, this would make an API call to get user profile details
        // For now, we might try to extract from stored AuthResponse or mock it
        // _profileState.value = ProfileState.Loading
        // viewModelScope.launch {
        //     try {
        //         val user = authRepository.getUserProfile() // Assuming such a function exists
        //         _userProfile.value = user
        //         _profileState.value = ProfileState.Success
        //     } catch (e: Exception) {
        //         _profileState.value = ProfileState.Error(e.message ?: "Failed to fetch profile")
        //     }
        // }
    }

    fun setPIN(pin: String) {
        viewModelScope.launch {
            _profileState.value = ProfileState.Loading
            try {
                val response = authRepository.setPIN(pin)
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