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
    val name: String = "Thành viên mới",
    val numberPhone: String = ""
)

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
    val token: String?,
    val refreshToken: String?
)

data class UpdateProfileRequest(
    val name: String,
    val numberPhone: String
)

data class BaseResponse(
    val success: Boolean,
    val message: String
)