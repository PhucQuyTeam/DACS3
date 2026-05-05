package com.example.myappatschool

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.fragment.app.Fragment
import com.example.myappatschool.fragment.DetailFragment
import com.example.myappatschool.fragment.HomeFragment
import com.example.myappatschool.fragment.ProfileFragment
import com.example.myappatschool.fragment.SettingFragment

class MainActivity : AppCompatActivity() {
    //Khai báo biến của bài Tính tổng 2 số
//    lateinit var edtA: EditText
//    lateinit var edtB: EditText
//    lateinit var btnTinh: Button
//    lateinit var btnHuy: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


// Code của tính tổng 2 số
//        // Ánh xạ view
//        val edtHeSoA = findViewById<EditText>(R.id.edtHeSoA)
//        val edtHeSoB = findViewById<EditText>(R.id.edtHeSoB)
//        val tvKetQua = findViewById<TextView>(R.id.tvKetQua)
//        val btnTinh = findViewById<Button>(R.id.btnTinh)
//        val btnHuy = findViewById<Button>(R.id.btnHuy)
//
//        // Xử lý nút Tính
//        btnTinh.setOnClickListener {
//            val strA = edtHeSoA.text.toString()
//            val strB = edtHeSoB.text.toString()
//
//            if (strA.isEmpty() || strB.isEmpty()) {
//                Toast.makeText(this, "Vui lòng nhập đầy đủ 2 số!", Toast.LENGTH_SHORT).show()
//            } else {
//                try {
//                    val a = strA.toDouble()
//                    val b = strB.toDouble()
//                    val tong = a + b
//                    tvKetQua.text = "Kết quả: $tong"
//                } catch (e: Exception) {
//                    Toast.makeText(this, "Định dạng số không hợp lệ!", Toast.LENGTH_SHORT).show()
//                }
//            }
//        }
//
//        // Xử lý nút Hủy
//        btnHuy.setOnClickListener {
//            edtHeSoA.text.clear()
//            edtHeSoB.text.clear()
//            tvKetQua.text = "Kết quả: "
//            edtHeSoA.requestFocus() // Đưa con trỏ chuột về ô A
//        }
//
//        // Tự code
//
//        edtA = findViewById(R.id.edtHeSoA)
//        edtB = findViewById(R.id.edtHeSoB)
//        btnTinh = findViewById(R.id.btnTinh)
//        btnHuy = findViewById(R.id.btnHuy)
//
//        btnTinh.setOnClickListener {
//            val a = edtA.text.toString().toInt()
//            val b = edtB.text.toString().toInt()
//            val tong = a + b;
//
//            Toast.makeText(this, "Tổng = $tong", Toast.LENGTH_SHORT).show()
//        }
//        btnHuy.setOnClickListener {
//            edtA.text.clear()
//            edtB.text.clear()
//            edtA.requestFocus()
//        }



// Code của thực hiện giải phương trình bậc 2, tính chu vi diện tích tam giác
//
//        lateinit var edtA: EditText
//        lateinit var edtB: EditText
//        lateinit var edtC: EditText
//        lateinit var btnGiai: Button
//        lateinit var tvKetQua: TextView
//
//        edtA=findViewById(R.id.edtA)
//        edtB=findViewById(R.id.edtB)
//        edtC=findViewById(R.id.edtC)
//        btnGiai=findViewById(R.id.btnGiai)
//        tvKetQua=findViewById(R.id.tvKetQua)

        //Button giải pt bậc 2
//        btnGiai.setOnClickListener {
//
//            val a = edtA.text.toString().toDouble()
//            val b = edtB.text.toString().toDouble()
//            val c = edtC.text.toString().toDouble()
//            if (edtA.text.isEmpty() || edtB.text.isEmpty() || edtC.text.isEmpty()) {
//                Toast.makeText(this,"Nhập đầy đủ a b c",Toast.LENGTH_SHORT).show()
//                return@setOnClickListener
//            }
//            if (a == 0.0) {
//                if (b == 0.0) {
//                    tvKetQua.text = "Kết quả: Phương trình vô nghiệm"
//                } else {
//                    val x = -c / b
//                    tvKetQua.text = "Kết quả:  x = $x"
//                }
//            } else {
//
//                val delta = b*b - 4*a*c
//
//                if (delta < 0) {
//                    tvKetQua.text = "Kết quả: Phương trình vô nghiệm"
//                } else if (delta == 0.0) {
//                    val x = -b / (2*a)
//                    tvKetQua.text = "Kết quả: Nghiệm kép x = $x"
//                } else {
//                    val x1 = (-b + sqrt(delta)) / (2*a)
//                    val x2 = (-b - sqrt(delta)) / (2*a)
//                    tvKetQua.text = "Kết quả:\n x1 = $x1\nx2 = $x2"
//                }
//            }
//            Toast.makeText(this, "Đang xử lý ", Toast.LENGTH_SHORT).show()
//        }
        //Button giải diện tích chu vi tam giác
//        btnGiai.setOnClickListener {
//
//            val a = edtA.text.toString().toDouble()
//            val b = edtB.text.toString().toDouble()
//            val c = edtC.text.toString().toDouble()
//
//            if (a + b > c && a + c > b && b + c > a) {
//
//                val chuVi = a + b + c
//                val p = chuVi / 2
//                val dienTich = sqrt(p * (p - a) * (p - b) * (p - c))
//
//                tvKetQua.text = "Chu vi = $chuVi\nDiện tích = $dienTich"
//
//            } else {
//                tvKetQua.text = "3 cạnh không tạo thành tam giác"
//            }
//        }
//    }

        loadFragment(HomeFragment())

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_nav)

        bottomNav.setOnItemSelectedListener {
            when(it.itemId){
                R.id.nav_home -> loadFragment(HomeFragment())
                R.id.nav_detail -> loadFragment(DetailFragment())
                R.id.nav_profile -> loadFragment(ProfileFragment())
                R.id.nav_setting -> loadFragment(SettingFragment())
            }
            true
        }
    }

    private fun loadFragment(fragment: Fragment){
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}