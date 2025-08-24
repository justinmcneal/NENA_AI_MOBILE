package com.example.nenaai.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nenaai.data.local.TokenManager
import com.example.nenaai.data.model.UserAnalyticsResponse
import com.example.nenaai.data.repository.LoanRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserAnalyticsViewModel @Inject constructor(
    private val loanRepository: LoanRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _userAnalytics = MutableStateFlow<UserAnalyticsState>(UserAnalyticsState.Loading)
    val userAnalytics: StateFlow<UserAnalyticsState> = _userAnalytics

    init {
        fetchUserAnalytics()
    }

    fun fetchUserAnalytics() {
        viewModelScope.launch {
            _userAnalytics.value = UserAnalyticsState.Loading
            val token = tokenManager.getToken()

            if (token.isNullOrEmpty()) {
                _userAnalytics.value = UserAnalyticsState.Error("Authentication token missing.")
                return
            }

            val result = loanRepository.getUserAnalytics(token)
            if (result.isSuccess) {
                result.getOrNull()?.let {
                    _userAnalytics.value = UserAnalyticsState.Success(it)
                } ?: _userAnalytics.value = UserAnalyticsState.Error("Empty response from server.")
            } else {
                _userAnalytics.value = UserAnalyticsState.Error(result.exceptionOrNull()?.message ?: "Unknown error occurred.")
            }
        }
    }
}

sealed class UserAnalyticsState {
    object Loading : UserAnalyticsState()
    data class Success(val analytics: UserAnalyticsResponse) : UserAnalyticsState()
    data class Error(val message: String) : UserAnalyticsState()
}