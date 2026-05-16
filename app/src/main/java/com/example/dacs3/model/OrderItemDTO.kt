package com.example.dacs3.model

data class OrderItemDTO(
    val productName: String,
    val productId: Int,
    val price: Double,
    val quantity: Int,
    val productImage: String?,
    var isReviewed: Boolean = false
)