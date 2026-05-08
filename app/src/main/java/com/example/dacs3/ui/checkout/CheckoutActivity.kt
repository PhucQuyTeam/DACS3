package com.example.dacs3.ui.checkout

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dacs3.databinding.ActivityCheckoutBinding
import com.example.dacs3.model.AddressDTO
import com.example.dacs3.model.CartItemDTO
import com.example.dacs3.network.RetrofitClient
import kotlinx.coroutines.launch

class CheckoutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCheckoutBinding
    private var selectedItems = ArrayList<CartItemDTO>()
    private var totalAmount = 0.0
    private val shippingFee = 30000.0

    // Biến lưu ID địa chỉ đang được chọn (để sau này gửi lên API Đặt hàng)
    private var currentAddressId: Int = -1

    // 1. Bộ thu nhận kết quả từ AddressSelectionActivity trả về
    private val addressSelectionLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            // Lấy AddressDTO đã chọn ra
            val selectedAddress = data?.getSerializableExtra("SELECTED_ADDRESS") as? AddressDTO

            // Cập nhật lại giao diện Checkout
            selectedAddress?.let {
                currentAddressId = it.id
                binding.tvCheckoutNamePhone.text = "${it.receiverName} | ${it.receiverPhone}"
                binding.tvCheckoutAddress.text = it.fullAddress
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Nút Back
        setSupportActionBar(binding.toolbarCheckout)
        binding.toolbarCheckout.setNavigationOnClickListener { finish() }

        // Nhận dữ liệu từ trang Giỏ hàng truyền sang
        selectedItems = intent.getSerializableExtra("SELECTED_ITEMS") as? ArrayList<CartItemDTO> ?: arrayListOf()
        totalAmount = intent.getDoubleExtra("TOTAL_AMOUNT", 0.0)

        setupUI()
        setupRecyclerView()

        // Sự kiện: Bấm vào khối Địa chỉ -> Mở trang Chọn địa chỉ
        // (Đảm bảo sếp đã thêm id="@+id/cardAddressSection" vào XML nhé)
        binding.cardAddressSection.setOnClickListener {
            val intent = Intent(this, AddressSelectionActivity::class.java)
            addressSelectionLauncher.launch(intent)
        }

        // Sự kiện: Nút Đặt hàng
        binding.btnPlaceOrder.setOnClickListener {
            performPlaceOrder()
        }
    }

    private fun setupUI() {
        // Tạm thời hiển thị text rỗng hoặc loading, hoặc có thể gọi API để lấy địa chỉ mặc định ngay lúc này
        binding.tvCheckoutNamePhone.text = "Vui lòng chọn địa chỉ"
        binding.tvCheckoutAddress.text = "Bấm vào đây để chọn địa chỉ nhận hàng"

        // Tính tiền
        binding.tvSubTotal.text = "${String.format("%,.0f", totalAmount)}đ"
        val finalTotal = totalAmount + shippingFee
        binding.tvFinalTotalDetail.text = "${String.format("%,.0f", finalTotal)}đ"
        binding.tvFinalTotalBottom.text = "${String.format("%,.0f", finalTotal)}đ"
    }

    private fun setupRecyclerView() {
        binding.rvCheckoutItems.layoutManager = LinearLayoutManager(this)
        binding.rvCheckoutItems.adapter = CheckoutItemAdapter(selectedItems)
    }

    private fun performPlaceOrder() {
        if (currentAddressId == -1) {
            Toast.makeText(this, "Vui lòng chọn địa chỉ nhận hàng!", Toast.LENGTH_SHORT).show()
            return
        }

        val paymentMethod = if (binding.rbCOD.isChecked) "COD" else "ZaloPay"

        if (paymentMethod == "ZaloPay") {
            Toast.makeText(this, "Tính năng ZaloPay đang được phát triển!", Toast.LENGTH_SHORT).show()
            return
        }

        // Khóa nút để tránh user bấm đúp nhiều lần
        binding.btnPlaceOrder.isEnabled = false

        lifecycleScope.launch {
            try {
                // Tính tổng tiền bao gồm cả phí ship
                val finalTotal = totalAmount + shippingFee

                // Đóng gói dữ liệu
                val request = com.example.dacs3.model.OrderRequest(
                    addressId = currentAddressId,
                    totalAmount = finalTotal,
                    paymentMethod = paymentMethod,
                    items = selectedItems
                )

                // Gọi API
                val response = RetrofitClient.getInstance(this@CheckoutActivity).placeOrder(request)

                if (response.isSuccessful) {
                    Toast.makeText(this@CheckoutActivity, "Đặt hàng thành công!", Toast.LENGTH_LONG).show()

                    // CHUYỂN SANG TRANG ĐƠN MUA
                    val intent = Intent(this@CheckoutActivity, com.example.dacs3.ui.order.OrderHistoryActivity::class.java)
                    intent.putExtra("TARGET_TAB", 0) // Mở tab "Chờ xác nhận"

                    // Xóa các trang trước đó (Cart, Checkout) để khi bấm Back không quay lại được trang thanh toán cũ
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this@CheckoutActivity, "Lỗi đặt hàng từ Server!", Toast.LENGTH_SHORT).show()
                    binding.btnPlaceOrder.isEnabled = true
                }
            } catch (e: Exception) {
                Toast.makeText(this@CheckoutActivity, "Lỗi kết nối mạng!", Toast.LENGTH_SHORT).show()
                binding.btnPlaceOrder.isEnabled = true
            }
        }
    }
}