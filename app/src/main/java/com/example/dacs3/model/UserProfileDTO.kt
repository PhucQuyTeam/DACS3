package com.example.dacs3.model

data class UserProfileDTO(
    val id: Int,
    val name: String,
    val email: String,
    val numberPhone: String,
    val avatar: String?,
    val pendingCount: Int?,
    val shippingCount: Int?,
    val completedCount: Int?
)