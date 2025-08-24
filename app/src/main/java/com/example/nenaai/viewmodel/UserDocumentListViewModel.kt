package com.example.nenaai.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nenaai.data.local.TokenManager
import com.example.nenaai.data.model.UserDocumentResponse
import com.example.nenaai.data.repository.VerificationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserDocumentListViewModel @Inject constructor(
    private val verificationRepository: VerificationRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _userDocuments = MutableStateFlow<List<UserDocumentResponse>>(emptyList())
    val userDocuments: StateFlow<List<UserDocumentResponse>> = _userDocuments

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        fetchUserDocuments()
    }

    fun fetchUserDocuments() {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            val token = tokenManager.getToken()

            if (token.isNullOrEmpty()) {
                _error.value = "Authentication token missing."
                _loading.value = false
                return@launch
            }

            val result = verificationRepository.getUserDocuments(token)
            if (result.isSuccess) {
                _userDocuments.value = result.getOrNull() ?: emptyList()
            } else {
                _error.value = result.exceptionOrNull()?.message ?: "Unknown error occurred."
            }
            _loading.value = false
        }
    }
}