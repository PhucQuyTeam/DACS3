package com.example.dacs3.model

data class UserProfileDTO(
    val id: Int,
    val name: String,
    val email: String,
    // Phải có đúng biến tên là "avatar" như thế này thì Fragment mới hết báo đỏ
    val avatar: String?,
    val pendingCount: Int?,
    val shippingCount: Int?,
    val completedCount: Int?
)