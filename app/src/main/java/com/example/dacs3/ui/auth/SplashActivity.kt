package com.example.dacs3.ui.auth

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.dacs3.databinding.ActivitySplashBinding
import com.example.dacs3.ui.home.HomeActivity
import com.example.dacs3.utils.TokenManager // Nhớ import cho đúng đường dẫn của bạn nhé

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private lateinit var tokenManager: TokenManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tokenManager = TokenManager(this)

        // Dùng Handler để tạo độ trễ 2 giây (2000 milliseconds) làm màu hiện logo
        Handler(Looper.getMainLooper()).postDelayed({
            checkAutoLogin()
        }, 2000)
    }

    private fun checkAutoLogin() {
        val savedToken = tokenManager.getAccessToken()

        if (!savedToken.isNullOrEmpty()) {
            // Có vé -> Mời khách VIP vào thẳng HomeActivity
            startActivity(Intent(this, HomeActivity::class.java))
        } else {
            // Không có vé -> Mời ra cửa Đăng nhập
            startActivity(Intent(this, LoginActivity::class.java))
        }

        // Cực kỳ quan trọng: Hủy luôn màn hình Splash này đi để người dùng
        // bấm nút Back không bị quay lại nhìn thấy cái logo quay quay nữa
        finish()
    }
}