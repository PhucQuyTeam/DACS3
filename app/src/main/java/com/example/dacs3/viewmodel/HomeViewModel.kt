package com.example.dacs3.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dacs3.model.ProductHomeDTO
import com.example.dacs3.repository.ProductRepository
import kotlinx.coroutines.launch

class HomeViewModel (private val repository: ProductRepository) : ViewModel() {

    private val _products = MutableLiveData<List<ProductHomeDTO>>()
    val products: LiveData<List<ProductHomeDTO>> get() = _products

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    fun fetchProducts() {
        viewModelScope.launch {
            try {
                val response = repository.getProducts()
                if (response.isSuccessful) {
                    _products.postValue(response.body())
                } else {
                    _errorMessage.postValue("Lỗi máy chủ: ${response.code()}")
                }
            } catch (e: Exception) {
                _errorMessage.postValue("Lỗi kết nối: ${e.message}")
            }
        }
    }
}