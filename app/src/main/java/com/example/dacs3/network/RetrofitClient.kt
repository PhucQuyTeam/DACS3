package com.example.dacs3.network

import android.content.Context
import android.util.Log
import com.example.dacs3.Token.TokenManager
import com.example.dacs3.api.ApiService
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private const val BASE_URL = "http://10.0.2.2:8081/api/"

    private var apiService: ApiService? = null

    // Biến khóa để chống kẹt mạng khi nhiều API cùng bị 403 một lúc
    private val lock = Any()

    fun getInstance(context: Context): ApiService {
        if (apiService == null) {
            val tokenManager = TokenManager(context.applicationContext)

            // INTERCEPTOR CHUẨN: BẮT 401/403 VÀ GỌI LẠI REQUEST
            val authInterceptor = Interceptor { chain ->
                var request = chain.request()

                // 1. Lấy token hiện tại và gắn vào header
                val token = tokenManager.getAccessToken()
                if (token != null) {
                    request = request.newBuilder()
                        .header("Authorization", "Bearer $token")
                        .build()
                }

                // 2. Gửi request lên Server
                var response = chain.proceed(request)

                // 3. NẾU SERVER BÁO LỖI 401/403 (Token hết hạn/sai)
                if (response.code == 401 || response.code == 403) {
                    Log.d("RetrofitClient", "⚠️ Bị 403/401! Bắt đầu refresh token ngầm...")

                    synchronized(lock) {
                        // Kiểm tra xem có luồng nào khác đã xin token mới hộ chưa
                        val currentToken = tokenManager.getAccessToken()
                        if (currentToken != null && currentToken != token) {
                            // Đã có vé mới -> Gọi lại ngay với vé mới
                            val newRequest = chain.request().newBuilder()
                                .header("Authorization", "Bearer $currentToken")
                                .build()
                            response.close() // Đóng response lỗi cũ
                            return@Interceptor chain.proceed(newRequest)
                        }

                        // Tự đi xin vé mới
                        val refreshToken = tokenManager.getRefreshToken()
                        if (refreshToken != null) {
                            val newToken = refreshAccessTokenNgam(refreshToken)

                            if (newToken != null) {
                                Log.d("RetrofitClient", "✅ Lấy vé mới thành công! Gửi lại Request cũ...")
                                tokenManager.saveTokens(newToken, refreshToken)

                                // Gắn token vừa xin vào request cũ
                                val newRequest = chain.request().newBuilder()
                                    .header("Authorization", "Bearer $newToken")
                                    .build()

                                response.close() // Đóng cái response 403 đi
                                response = chain.proceed(newRequest) // GỌI LẠI API BỊ LỖI
                            } else {
                                Log.e("RetrofitClient", "❌ Thẻ VIP đã chết! Đăng xuất User.")
                                tokenManager.clearTokens()
                            }
                        } else {
                            tokenManager.clearTokens()
                        }
                    }
                }
                response
            }

            val loggingInterceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }

            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(authInterceptor)
                .addInterceptor(loggingInterceptor)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            apiService = retrofit.create(ApiService::class.java)
        }
        return apiService!!
    }

    // Hàm xin vé mới chạy đồng bộ
    private fun refreshAccessTokenNgam(refreshToken: String): String? {
        try {
            val client = OkHttpClient()
            val json = JSONObject().apply { put("refreshToken", refreshToken) }
            val body = json.toString().toRequestBody("application/json".toMediaTypeOrNull())

            val request = Request.Builder()
                .url("${BASE_URL}auth/refresh")
                .post(body)
                .build()

            val response = client.newCall(request).execute()

            if (response.isSuccessful) {
                val responseString = response.body?.string()
                if (responseString != null) {
                    val jsonObject = JSONObject(responseString)
                    // Nhớ check success = true để tránh lỗi rỗng
                    if (jsonObject.optBoolean("success", false)) {
                        val newToken = jsonObject.optString("accessToken", jsonObject.optString("token"))
                        if (newToken.isNotEmpty()) {
                            return newToken
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }
}