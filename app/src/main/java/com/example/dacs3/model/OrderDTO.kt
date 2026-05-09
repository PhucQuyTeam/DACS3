package com.example.dacs3.model
data class OrderDTO(
    val id: Int,
    val totalPrice: Double,
    val status: Int,
    val orderDate: String,
    val productName: String,
    val productImage: String,
    val addressDetail: String,
    val paymentStatus: String?
)