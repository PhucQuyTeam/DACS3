package com.example.dacs3.ui.auth

import com.example.dacs3.network.RetrofitClient
import com.example.dacs3.Token.TokenManager
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.dacs3.databinding.ActivityLoginBinding
import com.example.dacs3.model.LoginRequest
import com.example.dacs3.ui.home.HomeActivity

import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var tokenManager: TokenManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tokenManager = TokenManager(this)
        tokenManager.clearTokens()

        // Chuyển sang màn đăng ký
        binding.txtGoToRegister.setOnClickListener {

            // 1. Tạo ra một "chuyến xe" (Intent) đi từ màn hình hiện tại (this) đến màn hình RegisterActivity
            val intent = Intent(this, RegisterActivity::class.java)

            // 2. Lệnh cho chuyến xe khởi hành
            startActivity(intent)
        }
        // Bấm nút đăng nhập
        binding.btnLogin.setOnClickListener {
            val email = binding.edtEmail.text.toString().trim()
            val password = binding.edtPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            performLogin(email, password)
        }
    }


    private fun performLogin(email: String, pass: String) {
        lifecycleScope.launch {
            try {
                val request = LoginRequest(email, pass)
                val response = RetrofitClient.getInstance(this@LoginActivity).login(request)

                if (response.isSuccessful && response.body() != null) {
                    val authResponse = response.body()!!

                    if (authResponse.success) {
                        Toast.makeText(this@LoginActivity, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show()

                        // --- ĐOẠN CODE LƯU TOKEN MỚI ---
                        val token = authResponse.token
                        val refToken = authResponse.refreshToken

                        if (!token.isNullOrEmpty()) {
                            // In ra Logcat để xác nhận là token đã về tới Android
                            println("Token lấy được: $token")
                            tokenManager.saveTokens(token, refToken ?: "")
                        } else {
                            println("LỖI: Server trả về thành công nhưng Token bị rỗng!")
                        }
                        // -------------------------------

                        val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this@LoginActivity, authResponse.message, Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@LoginActivity, "Lỗi kết nối máy chủ", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@LoginActivity, "Lỗi mạng: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}