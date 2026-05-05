package com.example.jit_vphuc_54.Viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.example.jit_vphuc_54.dao.ProductDatabase
import com.example.jit_vphuc_54.dao.ProductRepository
import com.example.jit_vphuc_54.model.Product
import kotlinx.coroutines.launch

class ProductViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ProductRepository
    val readAllData: LiveData<List<Product>>

    init {
        val dao = ProductDatabase.getDatabase(application).productDao()
        repository = ProductRepository(dao)
        readAllData = repository.readAllData
    }

    fun addProduct(product: Product) = viewModelScope.launch {
        repository.addProduct(product)
    }

    fun deleteProduct(product: Product) = viewModelScope.launch {
        repository.deleteProduct(product)
    }

    fun deleteSelected() = viewModelScope.launch {
        repository.deleteSelected()
    }
    fun updateProduct(product: Product) = viewModelScope.launch {
        repository.updateProduct(product)
    }
    fun deleteAll() = viewModelScope.launch {
        repository.deleteAll()
    }
}