package com.example.dacs3.ui.auth

import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.animation.OvershootInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.auth0.android.jwt.JWT
import com.example.dacs3.databinding.ActivitySplashBinding
import com.example.dacs3.ui.home.HomeActivity
import com.example.dacs3.Token.TokenManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.Date

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private lateinit var tokenManager: TokenManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tokenManager = TokenManager(this)

        // =========================================================
        // 1. HIỆU ỨNG GIAO DIỆN KIỂU DUOLINGO
        // =========================================================

        // Hiệu ứng nảy (Bounce) cho cụm Logo
        binding.llMascot.scaleX = 0f
        binding.llMascot.scaleY = 0f
        binding.llMascot.animate()
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(1000)
            .setInterpolator(OvershootInterpolator(1.5f))
            .start()

        // Hiệu ứng thay đổi câu thoại vui nhộn
        val funnyMessages = listOf(
            "Đang dọn rêu bể cá...",
            "Đang cho cá ăn...",
            "Đang bơm sục oxy...",
            "Sắp xong rồi sếp ơi!"
        )
        val handler = Handler(Looper.getMainLooper())
        var msgIndex = 0
        val textRunnable = object : Runnable {
            override fun run() {
                binding.tvLoadingText.text = funnyMessages[msgIndex]
                msgIndex++
                if (msgIndex < funnyMessages.size) {
                    handler.postDelayed(this, 700) // 0.7s đổi chữ 1 lần
                }
            }
        }
        handler.postDelayed(textRunnable, 500)

        // Hiệu ứng thanh loading chạy mượt tới 100%
        val animator = ValueAnimator.ofInt(0, 100)
        animator.duration = 2800 // Chạy mất 2.8 giây
        animator.addUpdateListener { animation ->
            binding.loadingBar.progress = animation.animatedValue as Int
        }
        animator.start()


        // =========================================================
        // 2. CHẠY NGẦM XỬ LÝ TOKEN VÀ ĐIỀU HƯỚNG
        // =========================================================
        lifecycleScope.launch(Dispatchers.IO) {
            // Tăng thời gian đợi lên 3 giây để khách xem hết hiệu ứng UI
            delay(4000)
            checkAndNavigate()
        }
    }

    private suspend fun checkAndNavigate() {
        val accessToken = tokenManager.getAccessToken()
        val refreshToken = tokenManager.getRefreshToken()

        if (accessToken.isNullOrEmpty() || refreshToken.isNullOrEmpty()) {
            goToLogin()
            return
        }

        // KIỂM TRA HẠN SỬ DỤNG VÉ
        if (isTokenExpiringSoon(accessToken)) {
            Log.d("Splash", "Vé cũ đã hết hạn! Xin vé mới ngay tại cổng...")
            val isRefreshSuccess = refreshAccessTokenSync(refreshToken)
            if (isRefreshSuccess) {
                goToHome()
            } else {
                Log.e("Splash", "Vé chết, Thẻ VIP cũng chết -> Yêu cầu Đăng nhập lại.")
                tokenManager.clearTokens()
                goToLogin()
            }
        } else {
            Log.d("Splash", "Vé còn sống nguyên! Xin mời vào...")
            goToHome()
        }
    }

    private suspend fun goToHome() {
        withContext(Dispatchers.Main) {
            startActivity(Intent(this@SplashActivity, HomeActivity::class.java))
            finish()
        }
    }

    private suspend fun goToLogin() {
        withContext(Dispatchers.Main) {
            startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
            finish()
        }
    }

    private fun isTokenExpiringSoon(token: String): Boolean {
        try {
            val jwt = JWT(token)
            val expiresAt: Date? = jwt.expiresAt
            if (expiresAt != null) {
                // Kiểm tra xem vé còn hạn trên 1 phút không
                val timeRemaining = expiresAt.time - System.currentTimeMillis()
                return timeRemaining < 60000
            }
        } catch (e: Exception) {
            return true
        }
        return true
    }

    private fun refreshAccessTokenSync(refreshToken: String): Boolean {
        try {
            val client = OkHttpClient()
            val json = JSONObject().apply { put("refreshToken", refreshToken) }
            val body = json.toString().toRequestBody("application/json".toMediaTypeOrNull())

            val request = Request.Builder()
                .url("http://10.0.2.2:8081/api/auth/refresh")
                .post(body)
                .build()

            val response = client.newCall(request).execute()

            if (response.isSuccessful) {
                val responseString = response.body?.string()
                if (responseString != null) {
                    val jsonObject = JSONObject(responseString)
                    if (jsonObject.optBoolean("success", false)) {
                        val newToken = jsonObject.optString("accessToken", jsonObject.optString("token"))
                        if (newToken.isNotEmpty()) {
                            tokenManager.saveTokens(newToken, refreshToken)
                            return true
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }
}