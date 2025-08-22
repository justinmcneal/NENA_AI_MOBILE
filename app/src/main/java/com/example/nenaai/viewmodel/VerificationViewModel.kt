package com.example.nenaai.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nenaai.data.repository.VerificationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VerificationViewModel @Inject constructor(
    private val repository: VerificationRepository
) : ViewModel() {

    // This map will hold the bitmaps for each upload field
    private val _documentBitmaps = MutableStateFlow<Map<String, Bitmap>>(emptyMap())
    val documentBitmaps: StateFlow<Map<String, Bitmap>> = _documentBitmaps

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
