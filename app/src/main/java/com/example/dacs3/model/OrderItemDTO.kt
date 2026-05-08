package com.example.dacs3.model

data class OrderItemDTO(
    val productName: String,
    val price: Double,
    val quantity: Int,
    val productImage: String?
)