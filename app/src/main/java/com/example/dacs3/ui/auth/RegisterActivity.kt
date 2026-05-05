package com.example.dacs3.ui.auth

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.dacs3.databinding.ActivityRegisterBinding
import com.example.dacs3.model.RegisterRequest
import com.example.dacs3.network.RetrofitClient
import com.example.dacs3.utils.TokenManager
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var tokenManager: TokenManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tokenManager = TokenManager(this)
        tokenManager.clearTokens()

        // Quay lại đăng nhập
        binding.txtGoToLogin.setOnClickListener {
            finish() // Tắt màn đăng ký, tự lùi về màn đăng nhập
        }

        binding.btnRegister.setOnClickListener {
            val email = binding.edtRegEmail.text.toString().trim()
            val password = binding.edtRegPassword.text.toString().trim()
            val confirmPass = binding.edtConfirmPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty() || confirmPass.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirmPass) {
                Toast.makeText(this, "Mật khẩu xác nhận không khớp!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            performRegister(email, password)
        }
    }

    private fun performRegister(email: String, pass: String) {
        lifecycleScope.launch {
            try {
                // Gửi request với email và pass, tên và sđt lấy mặc định như khai báo trong Model
                val request = RegisterRequest(email, pass)
                val response = RetrofitClient.getInstance(this@RegisterActivity).register(request)

                if (response.isSuccessful && response.body() != null) {
                    val authResponse = response.body()!!

                    if (authResponse.success) {
                        Toast.makeText(this@RegisterActivity, "Đăng ký thành công! Vui lòng đăng nhập.", Toast.LENGTH_LONG).show()
                        finish() // Về màn hình đăng nhập
                    } else {
                        Toast.makeText(this@RegisterActivity, authResponse.message, Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@RegisterActivity, "Lỗi kết nối máy chủ", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@RegisterActivity, "Lỗi mạng: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}