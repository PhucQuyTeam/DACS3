package com.example.jit_vphuc_54.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "product")
data class Product(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val price: String,
    val description: String,
    val imageUri: String? = null,
    var isSelected: Boolean = false
)