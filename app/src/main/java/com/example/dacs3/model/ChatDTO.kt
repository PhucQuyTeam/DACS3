package com.example.dacs3.model

data class ChatDTO (
    val id: Int,
    val conversationId: Int,
    val senderId: Int,
    val senderName: String?,
    val senderAvatar: String?,
    val message: String,
    val messageType: String,
    val isRead: Boolean,
    val createdAt: String
)

