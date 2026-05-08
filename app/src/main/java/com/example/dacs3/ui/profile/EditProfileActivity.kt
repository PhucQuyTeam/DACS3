package com.example.dacs3.ui.profile

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.dacs3.R
import com.example.dacs3.databinding.ActivityEditProfileBinding
import com.example.dacs3.network.RetrofitClient
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding
    private var selectedImageUri: Uri? = null

    // Mở thư viện chọn ảnh
    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            selectedImageUri = uri
            Glide.with(this).load(uri).circleCrop().into(binding.imgEditAvatar)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 1. NHẬN DỮ LIỆU CŨ HIỂN THỊ LÊN
        val oldName = intent.getStringExtra("EXTRA_NAME") ?: ""
        val oldPhone = intent.getStringExtra("EXTRA_PHONE") ?: ""
        val oldEmail = intent.getStringExtra("EXTRA_EMAIL") ?: ""
        val oldAvatar = intent.getStringExtra("EXTRA_AVATAR") ?: ""

        binding.edtEditName.setText(oldName)
        binding.edtEditPhone.setText(oldPhone)
        binding.edtEditEmail.setText(oldEmail) // Ô này bị khóa ở XML rồi

        if (oldAvatar.isNotEmpty()) {
            val imageUrl = "http://10.0.2.2:8081/upload/$oldAvatar"
            Glide.with(this).load(imageUrl).circleCrop()
                .placeholder(R.drawable.logoaquariumshop)
                .into(binding.imgEditAvatar)
        }

        // 2. BẤM VÀO ẢNH ĐỂ ĐỔI
        binding.imgEditAvatar.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        // 3. BẤM LƯU
        binding.btnSaveProfile.setOnClickListener {
            val newName = binding.edtEditName.text.toString().trim()
            val newPhone = binding.edtEditPhone.text.toString().trim()

            if (newName.isEmpty() || newPhone.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đủ thông tin!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            uploadData(newName, newPhone)
        }
    }

    private fun uploadData(name: String, phone: String) {
        binding.btnSaveProfile.isEnabled = false
        binding.btnSaveProfile.text = "Đang lưu..."

        lifecycleScope.launch {
            try {
                // Đóng gói Text
                val namePart = name.toRequestBody("text/plain".toMediaTypeOrNull())
                val phonePart = phone.toRequestBody("text/plain".toMediaTypeOrNull())

                // Đóng gói Ảnh
                var imagePart: MultipartBody.Part? = null
                if (selectedImageUri != null) {
                    val file = uriToFile(this@EditProfileActivity, selectedImageUri!!)
                    if (file != null) {
                        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                        imagePart = MultipartBody.Part.createFormData("file", file.name, requestFile)
                    }
                }

                // Gọi Server
                val response = RetrofitClient.getInstance(this@EditProfileActivity).updateProfile(namePart, phonePart, imagePart)

                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!
                    if (body.success) {
                        finish() // Đóng Activity, lùi về Fragment
                    } else {
                        Toast.makeText(this@EditProfileActivity, body.message, Toast.LENGTH_SHORT).show()
                        resetBtn()
                    }
                } else {
                    Toast.makeText(this@EditProfileActivity, "Lỗi Server", Toast.LENGTH_SHORT).show()
                    resetBtn()
                }
            } catch (e: Exception) {
                Toast.makeText(this@EditProfileActivity, "Lỗi mạng", Toast.LENGTH_SHORT).show()
                resetBtn()
            }
        }
    }

    private fun resetBtn() {
        binding.btnSaveProfile.isEnabled = true
        binding.btnSaveProfile.text = "Lưu thay đổi"
    }

    // Hàm chuyển đổi Uri thành File vật lý
    private fun uriToFile(context: Context, uri: Uri): File? {
        try {
            val contentResolver = context.contentResolver
            val file = File(context.cacheDir, "temp_image_${System.currentTimeMillis()}.jpg")
            val inputStream = contentResolver.openInputStream(uri) ?: return null
            val outputStream = FileOutputStream(file)
            inputStream.copyTo(outputStream)
            inputStream.close()
            outputStream.close()
            return file
        } catch (e: Exception) {
            return null
        }
    }
}