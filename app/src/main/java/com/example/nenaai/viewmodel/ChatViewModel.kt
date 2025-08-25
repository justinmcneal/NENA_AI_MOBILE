package com.example.nenaai.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nenaai.data.model.ChatMessage
import com.example.nenaai.data.model.ChatRequest
import com.example.nenaai.data.repository.ChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val repository: ChatRepository
) : ViewModel() {

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages

    private val _conversationId = MutableStateFlow<String?>(null)
    val conversationId: StateFlow<String?> = _conversationId

    init {
        // Add a welcome message when the ViewModel is created
        _messages.value = listOf(
            ChatMessage("Hello! I am NENA, your AI Financial Assistant. How can I help you today?", isFromUser = false)
        )
    }

    fun sendMessage(text: String) {
        if (text.isBlank()) return

        // Add the user's message immediately
        val userMessage = ChatMessage(text, isFromUser = true)
        _messages.value = _messages.value + userMessage

        // Launch a coroutine to get the AI's response from the repository
        viewModelScope.launch {
            try {
                val request = ChatRequest(message = text, conversation_id = _conversationId.value)
                val response = repository.sendMessage(request)
                val aiResponse = ChatMessage(response.reply, isFromUser = false)
                _messages.value = _messages.value + aiResponse
                _conversationId.value = response.conversation_id // Update conversation ID from response
            } catch (e: Exception) {
                // Handle error, e.g., show an error message in the chat
                val errorResponse = ChatMessage("Sorry, I encountered an error. Please try again.", isFromUser = false)
                _messages.value = _messages.value + errorResponse
            }
        }
    }

    // Function to reset conversation ID, useful for starting a new chat session
    fun startNewConversation() {
        _conversationId.value = null
        _messages.value = listOf(
            ChatMessage("Hello! I am NENA, your AI Financial Assistant. How can I help you today?", isFromUser = false)
        )
    }
}
