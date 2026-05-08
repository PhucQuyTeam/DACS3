package com.example.dacs3.ui.order

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dacs3.databinding.ActivityOrderDetailBinding
import com.example.dacs3.network.RetrofitClient
import kotlinx.coroutines.launch

class OrderDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOrderDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 1. Nút Back
        setSupportActionBar(binding.toolbarOrderDetail)
        binding.toolbarOrderDetail.setNavigationOnClickListener { finish() }

        // 2. Nhận dữ liệu truyền từ OrderHistoryAdapter
        val orderId = intent.getIntExtra("ORDER_ID", 0)
        val orderTotal = intent.getDoubleExtra("ORDER_TOTAL", 0.0)
        val orderAddress = intent.getStringExtra("ORDER_ADDRESS") ?: "Chưa rõ địa chỉ"
        val orderDate = intent.getStringExtra("ORDER_DATE") ?: "Chưa rõ ngày đặt"

        // 3. Đổ dữ liệu vào phần Đầu và phần Đáy (Dưới gạch ngang)
        binding.tvDetailOrderId.text = "Mã đơn: #$orderId"
        binding.tvDetailDate.text = "Ngày đặt: $orderDate"
        binding.tvDetailAddress.text = orderAddress
        binding.tvDetailTotal.text = "${String.format("%,.0f", orderTotal)}đ"

        // Cài đặt RecyclerView (Danh sách món)
        binding.rvOrderItems.layoutManager = LinearLayoutManager(this)

        // 4. Gọi API lấy danh sách các món hàng thuộc đơn này
        fetchOrderItems(orderId)
    }

    private fun fetchOrderItems(orderId: Int) {
        lifecycleScope.launch {
            try {
                // Đảm bảo bạn đã thêm API getOrderItems(orderId) vào ApiService
                val response = RetrofitClient.getInstance(this@OrderDetailActivity).getOrderItems(orderId)

                if (response.isSuccessful && response.body() != null) {
                    val itemList = response.body()!!

                    // Gắn vào Adapter (File OrderDetailAdapter mình đã gửi ở bài trước)
                    val adapter = OrderDetailAdapter(itemList)
                    binding.rvOrderItems.adapter = adapter
                } else {
                    Toast.makeText(this@OrderDetailActivity, "Không thể tải chi tiết sản phẩm", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@OrderDetailActivity, "Lỗi mạng", Toast.LENGTH_SHORT).show()
            }
        }
    }
}