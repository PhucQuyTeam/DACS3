package com.example.test1103

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.test1103.databinding.ActivityTamgiac2Binding

class TamgiacActivity : AppCompatActivity() {
    lateinit var binding: ActivityTamgiac2Binding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTamgiac2Binding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.apply {
            val ketqua = intent.getStringExtra("ketqua")
            tvKetQua.text = ketqua

            btnTroLai.setOnClickListener {
                val intent = Intent()
                intent.putExtra("trangthai", "Thuc hien phep toan thanh cong")
                setResult(RESULT_OK, intent)
                finish()
            }

        }
    }
}