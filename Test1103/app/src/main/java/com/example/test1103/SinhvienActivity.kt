package com.example.test1103

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.test1103.databinding.ActivityHienthittsvBinding
import com.example.test1103.databinding.ActivityTtsvBinding

class SinhvienActivity : AppCompatActivity() {

    lateinit var binding: ActivityHienthittsvBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHienthittsvBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val masv = intent.getStringExtra("masv")
        val tensv = intent.getStringExtra("tensv")

        binding.tvThongTin.text =
            """
            Mã sinh viên: $masv
            Tên sinh viên: $tensv
            """.trimIndent()
        binding.btnTroLai.setOnClickListener {
            val intent = Intent()
            intent.putExtra("trangthai", "Thuc hien phep toan thanh cong")
            setResult(RESULT_OK, intent)
            finish()
        }
    }
}