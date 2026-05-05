package com.example.dacs3.api

import com.example.dacs3.model.AuthResponse
import com.example.dacs3.model.LoginRequest
import com.example.dacs3.model.ProductHomeDTO
import com.example.dacs3.model.RegisterRequest
import com.example.dacs3.model.UserProfileDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    @GET("home/products")
    suspend fun getHomeProducts(): Response<List<ProductHomeDTO>>
    @GET("user/profile") // Thay đổi đường dẫn này theo đúng backend của bạn
    suspend fun getUserProfile(): Response<UserProfileDTO>
}