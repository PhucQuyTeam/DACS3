package com.example.test1103

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.test1103.databinding.ActivitySecondBinding

class SecondActivity : AppCompatActivity() {
    lateinit var binding: ActivitySecondBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySecondBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            //tvKetQua.text = "Tong :" + intent.getIntExtra("ketqua", 0).toString()
            tvKetQua.text = "Tổng 2 số  :" + intent.extras?.getInt("ketqua", 0).toString()

            btnTroLai.setOnClickListener {
                val intent = Intent()
                intent.putExtra("trangthai", "Thuc hien phep toan thanh cong")
                setResult(RESULT_OK, intent)
                finish()
            }
        }
    }
}