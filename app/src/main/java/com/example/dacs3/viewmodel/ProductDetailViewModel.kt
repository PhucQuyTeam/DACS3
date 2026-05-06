package com.example.dacs3.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dacs3.model.ReviewDTO
import com.example.dacs3.model.productDetailDTO
import com.example.dacs3.repository.ProductDetailRepository
import kotlinx.coroutines.launch


class ProductDetailViewModel(private val repository: ProductDetailRepository): ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _productDetail = MutableLiveData<productDetailDTO?>()
    val productDetail: LiveData<productDetailDTO?> get() = _productDetail

    private val _reviews = MutableLiveData<List<ReviewDTO?>>()
    val review: LiveData<List<ReviewDTO?>> get() = _reviews

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    fun fetchProductData(productId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val detailRespone = repository.getProductDetail(productId)
                val reviewsResponse = repository.getProductReviews(productId)

                if(detailRespone.isSuccessful){
                    _productDetail.value = detailRespone.body()
                }else{
                    _errorMessage.value = "Lỗi máy chủ: ${detailRespone.code()}"
                }

                if(reviewsResponse.isSuccessful){
                    _reviews.value = reviewsResponse.body()
                }else{
                    _errorMessage.value = "Lỗi máy chủ: ${reviewsResponse.code()}"
                }

            } catch (e: Exception) {
                _errorMessage.value = "Mất kết nối máy chủ: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }


}