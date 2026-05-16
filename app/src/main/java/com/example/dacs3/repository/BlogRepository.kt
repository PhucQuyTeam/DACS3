package com.example.dacs3.repository

import com.example.dacs3.api.ApiService

class BlogRepository(private val apiService: ApiService) {
    suspend fun getAllBlogs() = apiService.getAllBlogs()
}