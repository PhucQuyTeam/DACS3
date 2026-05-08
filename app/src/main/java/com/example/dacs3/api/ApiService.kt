package com.example.dacs3.api

import com.example.dacs3.model.AuthResponse
import com.example.dacs3.model.LoginRequest
import com.example.dacs3.model.OrderDTO
import com.example.dacs3.model.OrderItemDTO
import com.example.dacs3.model.ProductHomeDTO
import com.example.dacs3.model.RegisterRequest
import com.example.dacs3.model.ReviewDTO
import com.example.dacs3.model.UserProfileDTO
import com.example.dacs3.model.productDetailDTO
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    @GET("home/products")
    suspend fun getHomeProducts(): Response<List<ProductHomeDTO>>
    @GET("user/profile") // Thay đổi đường dẫn này theo đúng backend của bạn
    suspend fun getUserProfile(): Response<UserProfileDTO>

    //sử lý api chi tiết sp
    @GET("home/products/{id}")
    suspend fun getProductDetail(@Path("id") productId: Int): Response<productDetailDTO>
    @GET("home/products/{id}/reviews")
    suspend fun getProductReviews(@Path("id") productId: Int): Response<List<ReviewDTO>>

    @Multipart
    @PUT("user/update-profile")
    suspend fun updateProfile(
        @Part("name") name: RequestBody,
        @Part("phone") phone: RequestBody,
        @Part file: MultipartBody.Part?
    ): Response<AuthResponse>

    @GET("/my-orders") // Hoặc /api/my-orders tùy cấu hình Spring Boot của bạn
    suspend fun getMyOrders(
        @Query("status") status: Int
    ): Response<List<OrderDTO>>
    @GET("/order-items")
    suspend fun getOrderItems(
        @Query("orderId") orderId: Int
    ): Response<List<OrderItemDTO>>
}