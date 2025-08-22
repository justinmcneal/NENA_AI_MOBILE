package com.example.nenaai.data.repository

import com.example.nenaai.data.model.ChatRequest
import com.example.nenaai.data.model.ChatResponse
import com.example.nenaai.data.network.ApiService
import com.example.nenaai.data.network.BackendException
import retrofit2.Response
import javax.inject.Inject

class ChatRepository @Inject constructor(private val apiService: ApiService) {

    private fun <T> handleApiResponse(response: Response<T>): T {
        if (response.isSuccessful) {
            return response.body()!!
        } else {
            val errorBody = response.errorBody()?.string()
            // Using a generic error message as chat errors might not have specific fields like phone_number
            val errorMessage = errorBody ?: "Unknown chat error"
            throw BackendException(errorMessage, response.code(), errorBody)
        }
    }

    suspend fun sendMessage(message: String): ChatResponse {
        val request = ChatRequest(message)
        val response = apiService.postChatMessage(request)
        return handleApiResponse(response)
    }
}
