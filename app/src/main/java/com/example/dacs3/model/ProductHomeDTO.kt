package com.example.dacs3.model

import com.google.gson.annotations.SerializedName

data class ProductHomeDTO(
    @SerializedName("productId") val productId: Int,
    @SerializedName("name") val name: String,
    @SerializedName("img") val img: String?,
    @SerializedName("total_ProductQuantity") val totalProductQuantity: Int,
    @SerializedName("description") val description: String?,
    @SerializedName("price") val price: Int,
    @SerializedName("quantity") val quantity: Int,
    @SerializedName("categorieId") val categoryId: Int
)