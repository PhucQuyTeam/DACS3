package com.example.dacs3.ui.checkout

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dacs3.databinding.ActivityAddressSelectionBinding
import com.example.dacs3.network.RetrofitClient
import kotlinx.coroutines.launch

class AddressSelectionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddressSelectionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddressSelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarAddress)
        binding.toolbarAddress.setNavigationOnClickListener { finish() }
        binding.rvAddresses.layoutManager = LinearLayoutManager(this)

        // Bắt sự kiện bấm nút "+ Thêm địa chỉ mới" ở dưới đáy
        binding.btnAddAddress.setOnClickListener {
            val intent = Intent(this, AddEditAddressActivity::class.java)
            startActivity(intent)
        }
    }

    // Dùng onResume để mỗi lần màn hình này hiện lên (kể cả lúc từ trang Thêm/Sửa quay lại),
    // nó sẽ tự động gọi API lấy danh sách địa chỉ mới nhất.
    override fun onResume() {
        super.onResume()
        loadAddresses()
    }

    private fun loadAddresses() {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.getInstance(this@AddressSelectionActivity).getMyAddresses()
                if (response.isSuccessful && response.body() != null) {
                    val list = response.body()!!
                    // Khởi tạo Adapter với 2 sự kiện: Chọn địa chỉ và Bấm Sửa
                    val adapter = AddressSelectionAdapter(
                        addresses = list,
                        onAddressSelected = { selectedAddress ->
                            // Khi người dùng bấm vào 1 dòng địa chỉ -> Trả về trang Thanh toán
                            val returnIntent = Intent()
                            returnIntent.putExtra("SELECTED_ADDRESS", selectedAddress)
                            setResult(Activity.RESULT_OK, returnIntent)
                            finish()
                        },
                        onEditClick = { addressToEdit ->
                            // Khi người dùng bấm chữ "Sửa" -> Mở trang AddEditAddressActivity và mang theo data
                            val intent = Intent(this@AddressSelectionActivity, AddEditAddressActivity::class.java)
                            intent.putExtra("EXTRA_ADDRESS", addressToEdit)
                            startActivity(intent)
                        }
                    )
                    binding.rvAddresses.adapter = adapter
                }
            } catch (e: Exception) {
                Toast.makeText(this@AddressSelectionActivity, "Lỗi tải địa chỉ", Toast.LENGTH_SHORT).show()
            }
        }
    }
}