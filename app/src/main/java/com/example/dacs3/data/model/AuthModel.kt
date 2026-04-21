package com.example.dacs3.data.model

class AuthModel {
    // Dữ liệu gửi lên server
    data class LoginRequest(
        val username: String,
        val password: String
    )

    // Dữ liệu server trả về (ví dụ trả về token)
    data class LoginResponse(
        val token: String,
        val message: String
    )
}