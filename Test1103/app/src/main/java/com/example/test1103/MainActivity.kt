package com.example.test1103

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.test1103.databinding.ActivityMainBinding
import com.example.test1103.databinding.ActivityPtbac2Binding
import com.example.test1103.databinding.ActivityTamgiac1Binding
import com.example.test1103.databinding.ActivityTtsvBinding
import kotlin.math.sqrt


//
//     TÌM NGHIỆM CỦA PHƯƠNG TRÌNH BẬC 2
//
//    @Suppress("DEPRECATION")
//    class MainActivity : AppCompatActivity() {
//        lateinit var binding: ActivityPtbac2Binding
//
//
//        override fun onCreate(savedInstanceState: Bundle?) {
//            super.onCreate(savedInstanceState)
//            binding = ActivityPtbac2Binding.inflate(layoutInflater)
//            setContentView(binding.root)
//
//            binding.apply {
//                btnGiai.setOnClickListener {
//                    try {
//                        if (edtA.text.isEmpty() || edtB.text.isEmpty() || edtC.text.isEmpty()) {
//                            Toast.makeText(this@MainActivity,"Nhập đầy đủ a b c",Toast.LENGTH_SHORT).show()
//                            return@setOnClickListener
//                        }
//
//                        val a = edtA.text.toString().toDouble()
//                        val b = edtB.text.toString().toDouble()
//                        val c = edtC.text.toString().toDouble()
//                        var ketqua =""
//                if (a == 0.0) {
//                    if (b == 0.0) {
//                         ketqua = "Kết quả: Phương trình vô nghiệm"
//                    } else {
//                        val x = -c / b
//                         ketqua  = "Kết quả:  x = $x"
//                    }
//                } else {
//
//                    val delta = b*b - 4*a*c
//
//                    if (delta < 0) {
//                         ketqua  = "Kết quả: Phương trình vô nghiệm"
//                    } else if (delta == 0.0) {
//                        val x = -b / (2*a)
//                         ketqua  = "Kết quả: Nghiệm kép x = $x"
//                    } else {
//                        val x1 = (-b + sqrt(delta)) / (2*a)
//                        val x2 = (-b - sqrt(delta)) / (2*a)
//                         ketqua = "Kết quả:\n x1 = $x1\nx2 = $x2"
//                    }
//                }
//                        val intent = Intent(this@MainActivity, PTBac2Activity::class.java)
//                        intent.putExtra("ketqua",ketqua)
//                        //intent.extras?.putInt("ketqua", tong)
//                        startActivityForResult(intent, 200)
//                    } catch (e: Exception) {
//                        Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_SHORT).show()
//                    }
//                }
//            }
//        }
//
//        override fun onActivityResult(
//            requestCode: Int,
//            resultCode: Int,
//            data: Intent?
//        ) {
//            super.onActivityResult(requestCode, resultCode, data)
//            if (requestCode == 200) {
//                if (resultCode == RESULT_OK) {
//                    val trangthai = data?.getStringExtra("trangthai")
//                    Toast.makeText(this@MainActivity, trangthai, Toast.LENGTH_SHORT).show()
//                }
//            }
//        }
//    }


//       TÍNH DIỆN TÍCH - CHU VI TAM GIÁC

//@Suppress("DEPRECATION")
//class MainActivity : AppCompatActivity(){
//    lateinit var binding: ActivityTamgiac1Binding
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        binding = ActivityTamgiac1Binding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        binding.apply {
//            btnGiai.setOnClickListener {
//                try {
//                    if (edtA.text.isEmpty() || edtB.text.isEmpty() || edtC.text.isEmpty()) {
//                        Toast.makeText(
//                            this@MainActivity,
//                            "Nhập đầy đủ 3 cạnh của tam giác",
//                            Toast.LENGTH_SHORT
//                        ).show()
//                        return@setOnClickListener
//                    }
//
//                val a = edtA.text.toString().toDouble()
//                val b = edtB.text.toString().toDouble()
//                val c = edtC.text.toString().toDouble()
//
//                var ketqua = ""
//                if (a + b <= c || a + c <= b || b + c <= a) {
//                    ketqua = "Kết quả: Tam giác không hợp lệ"
//                } else {
//                    val chuvi = a + b + c
//                    val p = chuvi / 2
//
//                    // Diện tích (Heron)
//                    val dientich = sqrt(p * (p - a) * (p - b) * (p - c))
//
//                    ketqua = """
//                            Chu vi: $chuvi
//                            Diện tích: $dientich
//                        """.trimIndent()
//
//                }
//
//                val intent = Intent(this@MainActivity, TamgiacActivity::class.java)
//                intent.putExtra("ketqua", ketqua)
//                //intent.extras?.putInt("ketqua", tong)
//                startActivityForResult(intent, 200)
//
//            }
//                catch ( e: Exception){
//                    Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_SHORT).show()
//                }
//            }
//        }
//    }
//    override fun onActivityResult(
//        requestCode: Int,
//        resultCode: Int,
//        data: Intent?
//    ){
//        super.onActivityResult(requestCode, resultCode, data)
//        if(requestCode == 200){
//            if(resultCode == RESULT_OK){
//               val trangthai =data?.getStringExtra("trangthai")
//                Toast.makeText(this,trangthai, Toast.LENGTH_SHORT).show()
//            }
//        }
//    }
//}


//  HIỂN THỊ THÔNG TIN SINH VIÊN

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityTtsvBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTtsvBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {

            btnGui.setOnClickListener {

                if (edtMaSV.text.isEmpty() || edtTenSV.text.isEmpty()) {
                    Toast.makeText(this@MainActivity,
                        "Nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val masv = edtMaSV.text.toString()
                val tensv = edtTenSV.text.toString()

                val intent = Intent(this@MainActivity, SinhvienActivity::class.java)

                intent.putExtra("masv", masv)
                intent.putExtra("tensv", tensv)

                startActivity(intent)
            }
        }
    }
}

