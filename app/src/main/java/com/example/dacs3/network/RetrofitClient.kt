package com.example.dacs3.network

import android.content.Context
import com.example.dacs3.api.ApiService
import com.example.dacs3.utils.TokenManager
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private const val BASE_URL = "http://10.0.2.2:8081/api/"

    // Biến lưu trữ apiService để không phải tạo lại nhiều lần
    private var apiService: ApiService? = null

    // Hàm gọi Retrofit, bắt buộc truyền Context vào
    fun getInstance(context: Context): ApiService {
        if (apiService == null) {

            // 1. TẠO INTERCEPTOR: Tự động nhét Token vào Header
            val authInterceptor = Interceptor { chain ->
                val tokenManager = TokenManager(context.applicationContext)
                val token = tokenManager.getAccessToken()

                val requestBuilder = chain.request().newBuilder()

                // Nếu có Token trong máy, dán nhãn "Bearer " và nhét vào Authorization
                if (token != null) {
                    requestBuilder.addHeader("Authorization", "Bearer $token")
                }

                chain.proceed(requestBuilder.build())
            }

            val loggingInterceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }

            // 2. Gắn AuthInterceptor vào OkHttpClient
            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(authInterceptor)    // 1. CHÈN TOKEN VÀO TRƯỚC
                .addInterceptor(loggingInterceptor) // 2. RỒI MỚI GHI LOG RA MÀN HÌNH
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
}