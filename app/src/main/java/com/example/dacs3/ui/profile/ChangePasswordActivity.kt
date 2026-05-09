package com.example.dacs3.ui.profile // Đổi lại package cho đúng nha sếp

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import com.example.dacs3.R
import com.example.dacs3.network.RetrofitClient
import kotlinx.coroutines.launch

class ChangePasswordActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)

        val toolbar = findViewById<Toolbar>(R.id.toolbarChangePass)
        toolbar.setNavigationOnClickListener { finish() } // Nút Back

        val edtOldPass = findViewById<EditText>(R.id.edtOldPassword)
        val edtNewPass = findViewById<EditText>(R.id.edtNewPassword)
        val edtConfirmPass = findViewById<EditText>(R.id.edtConfirmPassword)
        val btnSave = findViewById<Button>(R.id.btnSavePassword)

        btnSave.setOnClickListener {
            val oldPass = edtOldPass.text.toString().trim()
            val newPass = edtNewPass.text.toString().trim()
            val confirmPass = edtConfirmPass.text.toString().trim()

            // 1. Kiểm tra Validate dữ liệu cơ bản
            if (oldPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (newPass.length < 6) {
                Toast.makeText(this, "Mật khẩu mới phải từ 6 ký tự trở lên!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (newPass != confirmPass) {
                Toast.makeText(this, "Mật khẩu xác nhận không khớp!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 2. Đóng gói dữ liệu gửi lên Server
            val requestBody = HashMap<String, String>()
            requestBody["oldPassword"] = oldPass
            requestBody["newPassword"] = newPass

            // 3. Lấy Token và Gọi API
            lifecycleScope.launch {
                try {
                    val prefs = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
                    val token = prefs.getString("TOKEN", "") ?: ""
                    val authHeader = "Bearer $token"

                    val response = RetrofitClient.getInstance(this@ChangePasswordActivity).changePassword(authHeader, requestBody)

                    if (response.isSuccessful) {
                        Toast.makeText(this@ChangePasswordActivity, "Đổi mật khẩu thành công!", Toast.LENGTH_SHORT).show()
                        finish() // Đổi thành công thì đóng màn hình này lại
                    } else {
                        // Backend trả về mã lỗi 400 (Pass cũ sai chẳng hạn)
                        val errorMsg = response.errorBody()?.string() ?: "Đổi mật khẩu thất bại"
                        Toast.makeText(this@ChangePasswordActivity, errorMsg, Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@ChangePasswordActivity, "Lỗi mạng: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}