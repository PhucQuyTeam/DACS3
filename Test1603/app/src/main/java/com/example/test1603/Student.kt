package com.example.test1603

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Student(
    val masv: String,
    val tensv: String,
    val gioiTinh: String,
    val soThich: String
) : Parcelable