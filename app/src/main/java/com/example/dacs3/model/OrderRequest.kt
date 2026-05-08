package com.example.dacs3.model

data class OrderRequest(
    val addressId: Int,
    val totalAmount: Double,
    val paymentMethod: String,
    val items: List<CartItemDTO>
)