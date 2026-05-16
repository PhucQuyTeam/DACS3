package com.example.dacs3.model

data class BlogDTO(
    val id: Int,
    val title: String?,
    val description: String?,
    val content: String?,
    val image: String?,
    val categoryId: Int?,
    val categoryName: String?,
    val createdAt: String?
): java.io.Serializable