package com.example.jit_vphuc_54.dao

import com.example.jit_vphuc_54.model.Product

class ProductRepository(private val dao: ProductDao) {

    val readAllData = dao.readAllData()

    suspend fun addProduct(product: Product) = dao.addProduct(product)
    suspend fun deleteProduct(product: Product) = dao.deleteProduct(product)

    suspend fun deleteSelected() = dao.deleteSelected()
    suspend fun updateProduct(product: Product) = dao.updateProduct(product)
    suspend fun deleteAll() {
        dao.deleteAll()
    }
}