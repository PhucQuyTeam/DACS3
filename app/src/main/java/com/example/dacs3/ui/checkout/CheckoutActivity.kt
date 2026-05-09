package com.example.dacs3.ui.checkout

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
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
import com.example.dacs3.ui.order.OrderHistoryActivity
import kotlinx.coroutines.launch
import org.json.JSONObject

class CheckoutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCheckoutBinding
    private var selectedItems = ArrayList<CartItemDTO>()
    private var totalAmount = 0.0
    private val shippingFee = 30000.0

    private var currentAddressId: Int = -1

    private val addressSelectionLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            val selectedAddress = data?.getSerializableExtra("SELECTED_ADDRESS") as? AddressDTO
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

        setSupportActionBar(binding.toolbarCheckout)
        binding.toolbarCheckout.setNavigationOnClickListener { finish() }

        selectedItems = intent.getSerializableExtra("SELECTED_ITEMS") as? ArrayList<CartItemDTO> ?: arrayListOf()
        totalAmount = intent.getDoubleExtra("TOTAL_AMOUNT", 0.0)

        setupUI()
        setupRecyclerView()

        binding.cardAddressSection.setOnClickListener {
            val intent = Intent(this, AddressSelectionActivity::class.java)
            addressSelectionLauncher.launch(intent)
        }

        binding.btnPlaceOrder.setOnClickListener {
            performPlaceOrder()
        }
    }

    private fun setupUI() {
        binding.tvCheckoutNamePhone.text = "Vui lòng chọn địa chỉ"
        binding.tvCheckoutAddress.text = "Bấm vào đây để chọn địa chỉ nhận hàng"
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
        binding.btnPlaceOrder.isEnabled = false

        lifecycleScope.launch {
            try {
                val finalTotal = totalAmount + shippingFee
                val request = com.example.dacs3.model.OrderRequest(
                    addressId = currentAddressId,
                    totalAmount = finalTotal,
                    paymentMethod = paymentMethod,
                    items = selectedItems
                )

                if (paymentMethod == "ZaloPay") {
                    Toast.makeText(this@CheckoutActivity, "Đang tạo đơn ZaloPay...", Toast.LENGTH_SHORT).show()
                    val response = RetrofitClient.getInstance(this@CheckoutActivity).createZaloPayOrder(request)

                    if (response.isSuccessful) {
                        val responseString = response.body()?.string() ?: ""
                        val jsonObject = JSONObject(responseString)

                        if (jsonObject.has("return_code") && jsonObject.getInt("return_code") == 1) {
                            val orderUrl = jsonObject.getString("order_url")
                            val appTransId = jsonObject.getString("app_trans_id")

                            // CẤT VÀO KÉT SẮT TRƯỚC KHI ĐI SANG TRÌNH DUYỆT CHỐNG SẬP APP
                            val prefs = getSharedPreferences("ZaloPayPrefs", Context.MODE_PRIVATE)
                            prefs.edit()
                                .putString("pending_trans_id", appTransId)
                                .putInt("saved_address_id", currentAddressId)
                                .apply()

                            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(orderUrl))
                            startActivity(browserIntent)
                        } else {
                            val errorMsg = if (jsonObject.has("return_message")) jsonObject.getString("return_message") else "Lỗi không xác định"
                            Toast.makeText(this@CheckoutActivity, "ZaloPay lỗi: $errorMsg", Toast.LENGTH_LONG).show()
                            binding.btnPlaceOrder.isEnabled = true
                        }
                    } else {
                        Toast.makeText(this@CheckoutActivity, "Lỗi Server ZaloPay", Toast.LENGTH_SHORT).show()
                        binding.btnPlaceOrder.isEnabled = true
                    }
                } else {
                    val response = RetrofitClient.getInstance(this@CheckoutActivity).placeOrder(request)
                    if (response.isSuccessful) {
                        Toast.makeText(this@CheckoutActivity, "Đặt hàng thành công!", Toast.LENGTH_LONG).show()
                        val intent = Intent(this@CheckoutActivity, OrderHistoryActivity::class.java)
                        intent.putExtra("TARGET_TAB", 0)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this@CheckoutActivity, "Lỗi đặt hàng từ Server!", Toast.LENGTH_SHORT).show()
                        binding.btnPlaceOrder.isEnabled = true
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(this@CheckoutActivity, "LỖI THẬT: ${e.message}", Toast.LENGTH_LONG).show()
                binding.btnPlaceOrder.isEnabled = true
            }
        }
    }

    override fun onResume() {
        super.onResume()

        // MỞ KÉT SẮT XEM CÓ MÃ GIAO DỊCH NÀO ĐANG CHỜ KHÔNG
        val prefs = getSharedPreferences("ZaloPayPrefs", Context.MODE_PRIVATE)
        val savedTransId = prefs.getString("pending_trans_id", null)

        if (savedTransId != null) {
            // Lấy lại địa chỉ phòng khi App bị sập quên mất
            val savedAddressId = prefs.getInt("saved_address_id", -1)
            if (currentAddressId == -1) {
                currentAddressId = savedAddressId
            }

            // Xóa ngay mã trong két sắt để khỏi bị lặp lại
            prefs.edit().clear().apply()

            checkPaymentStatus(savedTransId)
        }
    }

    private fun checkPaymentStatus(appTransId: String) {
        Toast.makeText(this, "Đang kiểm tra giao dịch...", Toast.LENGTH_SHORT).show()
        binding.btnPlaceOrder.isEnabled = false

        lifecycleScope.launch {
            try {
                val finalTotal = totalAmount + shippingFee
                val request = com.example.dacs3.model.OrderRequest(
                    addressId = currentAddressId,
                    totalAmount = finalTotal,
                    paymentMethod = "ZaloPay",
                    items = selectedItems
                )

                val response = RetrofitClient.getInstance(this@CheckoutActivity).checkZaloPayStatus(appTransId, request)

                if (response.isSuccessful) {
                    Toast.makeText(this@CheckoutActivity, "Thanh toán ZaloPay thành công!", Toast.LENGTH_LONG).show()
                    val orderIntent = Intent(this@CheckoutActivity, OrderHistoryActivity::class.java)
                    orderIntent.putExtra("TARGET_TAB", 0)
                    orderIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(orderIntent)
                    finish()
                } else {
                    // NẾU THẤT BẠI HOẶC ZALOPAY ĐANG XỬ LÝ NÓ SẼ BÁO LỖI Ở ĐÂY
                    val errorBody = response.errorBody()?.string() ?: "Giao dịch thất bại hoặc bị hủy!"
                    Toast.makeText(this@CheckoutActivity, errorBody, Toast.LENGTH_LONG).show()
                    binding.btnPlaceOrder.isEnabled = true
                }
            } catch (e: Exception) {
                Toast.makeText(this@CheckoutActivity, "LỖI THẬT: ${e.message}", Toast.LENGTH_LONG).show()
                binding.btnPlaceOrder.isEnabled = true
            }
        }
    }
}