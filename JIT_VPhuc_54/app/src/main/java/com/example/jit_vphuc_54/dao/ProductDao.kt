package com.example.jit_vphuc_54.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.jit_vphuc_54.model.Product

@Dao
interface ProductDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addProduct(product: Product)

    @Update
    suspend fun updateProduct(product: Product)

    @Delete
    suspend fun deleteProduct(product: Product)

    @Query("SELECT * FROM product ORDER BY id DESC")
    fun readAllData(): LiveData<List<Product>>

    @Query("DELETE FROM product WHERE isSelected = 1")
    suspend fun deleteSelected()
    @Query("DELETE FROM product")
    suspend fun deleteAll()

}
