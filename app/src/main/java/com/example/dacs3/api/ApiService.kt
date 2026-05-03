package com.example.dacs3.api

import com.example.dacs3.model.ProductHomeDTO
import retrofit2.Response
import retrofit2.http.GET

interface ApiService {

    @GET("home/products")
    suspend fun getHomeProducts(): Response<List<ProductHomeDTO>>
}