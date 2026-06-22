package com.example.dacs3.api

import com.example.dacs3.model.AIChatRequest
import com.example.dacs3.model.AIChatResponse
import com.example.dacs3.model.AuthResponse
import com.example.dacs3.model.BaseResponse
import com.example.dacs3.model.BlogDTO
import com.example.dacs3.model.CartItemDTO
import com.example.dacs3.model.ChatDTO
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
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    data class ProvinceDTO(val id: Int, val name: String)
    data class WardDTO(val id: Int, val name: String)
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    @GET("home/products")
    suspend fun getHomeProducts(): Response<List<ProductHomeDTO>>
    @GET("user/profile")
    suspend fun getUserProfile(): Response<UserProfileDTO>


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

    @GET("/api/order/my-orders")
    suspend fun getMyOrders(
        @Query("status") status: Int
    ): Response<List<OrderDTO>>

    @GET("/api/order/order-items")
    suspend fun getOrderItems(
        @Query("orderId") orderId: Int
    ): Response<List<OrderItemDTO>>


    @POST("/cart/add")
    suspend fun addToCart(
        @Query("productId") productId: Int,
        @Query("quantity") quantity: Int
    ): Response<okhttp3.ResponseBody>


    @GET("/cart/my-cart")
    suspend fun getMyCart(): Response<List<CartItemDTO>>

    @DELETE("/cart/remove/{cartId}")
    suspend fun removeCartItem(@Path("cartId") cartId: Int): retrofit2.Response<okhttp3.ResponseBody>

    @GET("/api/user/address/my-addresses")
    suspend fun getMyAddresses(): retrofit2.Response<List<com.example.dacs3.model.AddressDTO>>


    @GET("/api/user/address/provinces")
    suspend fun getProvinces(): retrofit2.Response<List<ProvinceDTO>>

    @GET("/api/user/address/wards")
    suspend fun getWards(@Query("provinceId") provinceId: Int): retrofit2.Response<List<WardDTO>>
    @POST("/api/user/address/add")
    suspend fun addAddress(@Body request: HashMap<String, Any>): retrofit2.Response<okhttp3.ResponseBody>


    @PUT("/api/user/address/update")
    suspend fun updateAddress(@Body request: HashMap<String, Any>): retrofit2.Response<okhttp3.ResponseBody>

    // API Xóa địa chỉ
    @DELETE("/api/user/address/delete/{id}")
    suspend fun deleteAddress(@Path("id") id: Int): retrofit2.Response<okhttp3.ResponseBody>

    @PUT("/cart/update")
    suspend fun updateCartQuantity(
        @Query("cartId") cartId: Int,
        @Query("quantity") quantity: Int
    ): retrofit2.Response<okhttp3.ResponseBody>

    @POST("/api/order/create")
    suspend fun placeOrder(@Body request: com.example.dacs3.model.OrderRequest): retrofit2.Response<okhttp3.ResponseBody>

    @POST("/api/order/create-zalopay")
    suspend fun createZaloPayOrder(
        @Body request: com.example.dacs3.model.OrderRequest
    ): retrofit2.Response<okhttp3.ResponseBody>

    @POST("/api/order/check-zalopay")
    suspend fun checkZaloPayStatus(
        @Query("app_trans_id") appTransId: String,
        @Body request: com.example.dacs3.model.OrderRequest
    ): retrofit2.Response<okhttp3.ResponseBody>
    @GET("/api/notifications/my-notifications")
    suspend fun getMyNotifications(
        @Header("Authorization") token: String
    ): retrofit2.Response<List<com.example.dacs3.model.NotificationDTO>>

    @PUT("/api/user/change-password")
    suspend fun changePassword(
        @Header("Authorization") token: String,
        @Body request: HashMap<String, String>
    ): retrofit2.Response<okhttp3.ResponseBody>

    @GET("/api/chat/history")
    suspend fun getChatHistory(@Query("adminId") adminId: Int): Response<List<ChatDTO>>

    @POST("/api/chat/send")
    suspend fun sendMessage(@Body request: HashMap<String, Any>): Response<ChatDTO>

    @GET("/api/chat/unread-count")
    suspend fun getUnreadMessageCount(): Response<Int>

    @PUT("/api/chat/mark-read")
    suspend fun markChatAsRead(@Query("adminId") adminId: Int): retrofit2.Response<okhttp3.ResponseBody>

    @Multipart
    @POST("/api/chat/upload-image") // Backend cần viết API này
    suspend fun uploadChatImage(@Part image: MultipartBody.Part): Response<BaseResponse>


    @Multipart
    @POST("/api/reviews/add")
    suspend fun addReview(
        @Part("productId") productId: okhttp3.RequestBody,
        @Part("orderId") orderId: okhttp3.RequestBody,
        @Part("rating") rating: okhttp3.RequestBody,
        @Part("comment") comment: okhttp3.RequestBody,
        @Part image: okhttp3.MultipartBody.Part?
    ): retrofit2.Response<okhttp3.ResponseBody>


    @GET("/api/blogs")
    suspend fun getAllBlogs(): retrofit2.Response<List<BlogDTO>>

    @POST("/api/ai-chat/ask")
    suspend fun askAIBot(@Body request: AIChatRequest): retrofit2.Response<AIChatResponse>


}