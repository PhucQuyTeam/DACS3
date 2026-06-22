package com.example.dacs3.model

data class productDetailDTO (
    val productId: Int,
    val name: String,
    val price: Int,
    val description: String,
    val quantity: Int,
    val imgages: List<String>?,
    val total_ProductQuantity: Int,
    val categorieName: String,
    val averageRating: Double,
    val totalReviews: Int
)