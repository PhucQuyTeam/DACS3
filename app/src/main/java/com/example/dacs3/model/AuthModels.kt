package com.example.dacs3.model

import com.google.gson.annotations.SerializedName

// Request gửi đi
data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val email: String,
    val password: String,
    val name: String = "Thành viên mới", // Giao diện của bạn chưa có ô nhập tên, mình set mặc định
    val numberPhone: String = ""
)

// Dữ liệu trả về
data class UserDTO(
    val id: Int,
    val name: String?,
    val email: String?,
    val phone: String?,
    val avatar: String?
)

data class AuthResponse(
    val success: Boolean,
    val message: String,
    val user: UserDTO?,
    val token: String?,         // Access Token
    val refreshToken: String?   // Refresh Token
)