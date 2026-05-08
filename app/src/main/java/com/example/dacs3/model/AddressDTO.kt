package com.example.dacs3.model
import java.io.Serializable

data class AddressDTO(
    val id: Int,
    val receiverName: String,
    val receiverPhone: String,
    val streetDetail: String,
    val provinceId: Int,
    val wardId: Int,
    val fullAddress: String,
    var isSelected: Boolean = false // Dùng để UI biết nút Radio nào đang chọn
) : Serializable