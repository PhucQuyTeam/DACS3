package com.example.dacs3.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


object RetrofitClient {
    private const val BASE_URL = "http://10.0.2.2:8081/api/"

    // Cấu hình OkHttpClient để log network và setup timeout
    private val okHttpClient by lazy {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY // Xem được body của request/response
        }

        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS) // Thời gian đợi kết nối
            .readTimeout(30, TimeUnit.SECONDS)    // Thời gian đợi đọc dữ liệu
            .build()
    }

    val instance: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient) // Gắn OkHttpClient vào Retrofit
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}