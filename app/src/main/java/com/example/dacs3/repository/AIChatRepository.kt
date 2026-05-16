package com.example.dacs3.repository

import com.example.dacs3.api.ApiService
import com.example.dacs3.model.AIChatRequest

class AIChatRepository(private val apiService: ApiService) {
    suspend fun askAI(message: String) = apiService.askAIBot(AIChatRequest(message))
}