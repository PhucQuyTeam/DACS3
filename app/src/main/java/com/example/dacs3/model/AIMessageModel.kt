package com.example.dacs3.model

data class AIChatRequest(val message: String)

data class AIChatResponse(val reply: String)

// Class này dùng để vẽ lên màn hình chat (Biết được ai là người gửi)
data class AIMessageModel(
    val id: String = System.currentTimeMillis().toString(), // ID ảo để DiffUtil hoạt động
    val text: String,
    val isUser: Boolean, // true = User gửi (Màu xanh), false = AI gửi (Màu xám)
    val isTyping: Boolean = false // true = Hiện dấu 3 chấm AI đang suy nghĩ
)