package com.example.dacs3.repository

import com.example.dacs3.api.ApiService

class ProductDetailRepository(private val apiService: ApiService) {

    suspend fun getProductDetail(productId: Int) = apiService.getProductDetail(productId)
    suspend fun getProductReviews(productId: Int) = apiService.getProductReviews(productId)

}