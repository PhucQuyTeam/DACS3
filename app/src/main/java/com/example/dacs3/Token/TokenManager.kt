package com.example.dacs3.Token
import android.content.Context
import android.content.SharedPreferences

class TokenManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    fun saveTokens(accessToken: String, refreshToken: String) {
        prefs.edit()
            .putString("ACCESS_TOKEN", accessToken)
            .putString("REFRESH_TOKEN", refreshToken)
            .apply()
    }

    fun getAccessToken(): String? =
        prefs.getString("ACCESS_TOKEN", null)

    fun getRefreshToken(): String? =
        prefs.getString("REFRESH_TOKEN", null)

    fun saveUserId(id: Int) {
        prefs.edit().putInt("USER_ID", id).apply()
    }

    fun getUserId(): Int {
        return prefs.getInt("USER_ID", -1) // Trả về -1 nếu chưa đăng nhập
    }

    fun clearTokens() {
        prefs.edit().clear().apply()
    }
}