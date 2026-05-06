package com.example.dacs3.model

data class productDetailDTO (
    val productId: Int,
    val name: String,
    val price: Int,
    val description: String,
    val quantity: Int, // Số lượng trong kho
    val imgages: List<String>?, // Chú ý: Backend bạn viết là 'imgages'
    val total_ProductQuantity: Int, // Đã bán
    val categorieName: String,
    val averageRating: Double,
    val totalReviews: Int
)