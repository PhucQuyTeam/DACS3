package com.example.dacs3.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.dacs3.model.BlogDTO
import com.example.dacs3.repository.BlogRepository
import kotlinx.coroutines.launch

class BlogViewModel(private val repository: BlogRepository) : ViewModel() {

    private val _blogs = MutableLiveData<List<BlogDTO>>()
    val blogs: LiveData<List<BlogDTO>> get() = _blogs

    fun fetchBlogs() {
        viewModelScope.launch {
            try {
                val response = repository.getAllBlogs()
                if (response.isSuccessful) {
                    _blogs.postValue(response.body() ?: emptyList())
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}


class BlogViewModelFactory(private val repository: BlogRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BlogViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BlogViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}