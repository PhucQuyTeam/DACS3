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

    private var originnalProductList = listOf<ProductHomeDTO>()

    // 1. THÊM BIẾN LƯU SỐ LƯỢNG TIN NHẮN CHƯA ĐỌC
    private val _unreadCount = MutableLiveData<Int>()
    val unreadCount: LiveData<Int> get() = _unreadCount

    var currentSearchQuery: String = ""
    var minRating: Double = 0.0
    var maxRating: Double = 5.0
    var minPrice: Int = 0
    var maxPrice: Int = Int.MAX_VALUE

    fun fetchProducts() {
        viewModelScope.launch {
            try {
                val response = repository.getProducts()
                if (response.isSuccessful) {
                    val list = response.body() ?: emptyList()
                    originnalProductList = list
//                    _products.postValue(list)
                    searchProducts(currentSearchQuery)
                } else {
                    _errorMessage.postValue("Lỗi máy chủ: ${response.code()}")
                }
            } catch (e: Exception) {
                _errorMessage.postValue("Lỗi kết nối: ${e.message}")
            }
        }
    }

    fun filterByCategory(categoryId :Int){
        if(categoryId == -1){
            _products.value = originnalProductList
        }else{
            val filteredList = originnalProductList.filter { it.categoryId == categoryId }
            _products.value = filteredList
        }
    }

    fun searchProducts(query: String) {
        currentSearchQuery = query // Nhớ lại từ khóa người dùng gõ

        if (query.isBlank()) {
            _products.value = originnalProductList
        } else {
            val filteredList = originnalProductList.filter {
                it.name.contains(query, ignoreCase = true)
            }
            _products.value = filteredList
        }
        executeFiltering()
    }

    fun applyAdvancedFilters(minR: Double, maxR: Double, minP: Int, maxP: Int) {
        this.minRating = minR
        this.maxRating = maxR
        this.minPrice = minP
        this.maxPrice = maxP
        executeFiltering()
    }

    private fun executeFiltering() {
        var filteredList = originnalProductList

        // 1. Lọc theo chữ (Tên)
        if (currentSearchQuery.isNotBlank()) {
            filteredList = filteredList.filter { it.name.contains(currentSearchQuery, ignoreCase = true) }
        }

        // 2. Lọc theo Sao
        filteredList = filteredList.filter { it.averageRating in minRating..maxRating }

        // 3. Lọc theo Giá
        filteredList = filteredList.filter { it.price in minPrice..maxPrice }

        // Đẩy ra UI
        _products.postValue(filteredList)
    }

    fun fetchUnreadCount() {
        viewModelScope.launch {
            try {
                val response = repository.getUnreadMessageCount()
                if (response.isSuccessful) {
                    val count = response.body() ?: 0
                    _unreadCount.postValue(count)
                } else {
                    _unreadCount.postValue(0)
                }
            } catch (e: Exception) {
                // Nếu rớt mạng thì mặc định ẩn số đếm (gán = 0)
                _unreadCount.postValue(0)
            }
        }
    }

}