package com.example.dacs3.model

data class NotificationDTO(
    val id: Int,
    val title: String,
    val message: String,
    val isRead: Boolean,
    val createdAt: String
)