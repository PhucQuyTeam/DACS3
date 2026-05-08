package com.example.dacs3.model

data class CartItemDTO(
    val cartId: Int,
    val productId: Int,
    val productName: String,
    val price: Double,
    var quantity: Int,
    val productImage: String?,
    var isSelected: Boolean = false
) : java.io.Serializable