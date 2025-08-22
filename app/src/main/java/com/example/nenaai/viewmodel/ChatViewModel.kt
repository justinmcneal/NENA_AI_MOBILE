package com.example.nenaai.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nenaai.data.model.ChatMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    // private val repository: ChatRepository // Will be added later
) : ViewModel() {

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages

    init {
        // Add a welcome message when the ViewModel is created
        _messages.value = listOf(
            ChatMessage("Hello! I am NENA, your AI Financial Assistant. How can I help you today?", isFromUser = false)
        )
    }

    fun sendMessage(text: String) {
        // Add the user's message immediately
        val userMessage = ChatMessage(text, isFromUser = true)
        _messages.value = _messages.value + userMessage

        // Launch a coroutine to simulate a network call and get the AI's response
        viewModelScope.launch {
            // TODO: Replace this with a real repository call
            val aiResponse = ChatMessage("This is a simulated response from NENA AI.", isFromUser = false)
            _messages.value = _messages.value + aiResponse
        }
    }
}
