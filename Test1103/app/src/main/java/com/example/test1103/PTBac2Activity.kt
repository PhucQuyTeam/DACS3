package com.example.test1103

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.test1103.databinding.ActivityPtbac22Binding
import com.example.test1103.databinding.ActivityPtbac2Binding
import com.example.test1103.databinding.ActivitySecondBinding
class PTBac2Activity : AppCompatActivity() {
    lateinit var binding: ActivityPtbac22Binding


        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            binding = ActivityPtbac22Binding.inflate(layoutInflater)
            setContentView(binding.root)

            binding.apply {
                tvKetQua.text =  intent.getStringExtra("ketqua")
//               tvKetQua.text = "Tổng 2 số  :" + intent.extras?.getInt("ketqua", 0).toString()

                btnTroLai.setOnClickListener {
                    val intent = Intent()
                    intent.putExtra("trangthai", "Thuc hien phep toan thanh cong")
                    setResult(RESULT_OK, intent)
                    finish()
                }
            }
        }
    }
