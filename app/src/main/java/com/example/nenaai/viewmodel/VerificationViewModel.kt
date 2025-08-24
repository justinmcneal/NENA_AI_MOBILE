package com.example.nenaai.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nenaai.data.local.TokenManager
import com.example.nenaai.data.model.UserVerificationRequest
import com.example.nenaai.data.repository.VerificationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VerificationViewModel @Inject constructor(
    private val repository: VerificationRepository,
    private val tokenManager: TokenManager // 🔹 Injected here

) : ViewModel() {

    // This map will hold the bitmaps for each upload field
    private val _documentBitmaps = MutableStateFlow<Map<String, Bitmap>>(emptyMap())
    val documentBitmaps: StateFlow<Map<String, Bitmap>> = _documentBitmaps

    private val _verificationStatus = MutableStateFlow<String?>(null)
    val verificationStatus: StateFlow<String?> = _verificationStatus

    fun submitVerificationDetails(request: UserVerificationRequest) {
        viewModelScope.launch {
            val token = tokenManager.getToken()
            if (token == null) {
                _verificationStatus.value = "No token found. Please log in again."
                return@launch
            }

            try {
                val response = repository.submitVerificationDetails("Bearer $token", request)
                if (response.isSuccessful) {
                    _verificationStatus.value = response.body()?.message ?: "Success"
                } else {
                    _verificationStatus.value = "Error: ${response.errorBody()?.string()}"
                }
            } catch (e: Exception) {
                _verificationStatus.value = "Exception: ${e.message}"
            }
        }
    }

    fun onDocumentCaptured(label: String, bitmap: Bitmap?) {
        bitmap ?: return
        _documentBitmaps.value = _documentBitmaps.value + (label to bitmap)
    }

    fun submitVerification() {
        viewModelScope.launch {
            _documentBitmaps.value.forEach { (label, bitmap) ->
                try {
                    repository.uploadDocument(label, bitmap)
                    // TODO: Handle success for each upload (e.g., show a message, clear the bitmap)
                    println("Successfully uploaded $label")
                } catch (e: Exception) {
                    // TODO: Handle error for each upload (e.g., show an error message)
                    println("Failed to upload $label: ${e.message}")
                }
            }
        }
    }
}
