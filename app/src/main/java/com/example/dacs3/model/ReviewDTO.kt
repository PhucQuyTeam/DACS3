package com.example.dacs3.model

data class ReviewDTO (
    val productId: Int,
    val reviewerName: String,
    val createdAt: String, // Hoặc Long tuỳ định dạng backend trả về
    val rating: Int,
    val comment: String,
    val image: String?
)