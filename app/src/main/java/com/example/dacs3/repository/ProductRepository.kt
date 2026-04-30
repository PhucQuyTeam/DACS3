package com.example.dacs3.repository

import com.example.dacs3.api.ApiService
import com.example.dacs3.model.ProductHomeDTO
import retrofit2.Response

class ProductRepository (private val apiService: ApiService) {
    suspend fun getProducts(): Response<List<ProductHomeDTO>> {
        return apiService.getHomeProducts()
    }
}