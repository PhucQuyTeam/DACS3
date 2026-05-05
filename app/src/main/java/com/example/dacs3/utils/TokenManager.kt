package com.example.dacs3.utils
import android.content.Context
import android.content.SharedPreferences

class TokenManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    fun saveTokens(token: String, refreshToken: String) {
        val editor = prefs.edit()
        editor.putString("ACCESS_TOKEN", token)
        editor.putString("REFRESH_TOKEN", refreshToken)
        editor.apply()
    }

    fun getAccessToken(): String? = prefs.getString("ACCESS_TOKEN", null)

    fun clearTokens() {
        prefs.edit().clear().apply()
    }
}