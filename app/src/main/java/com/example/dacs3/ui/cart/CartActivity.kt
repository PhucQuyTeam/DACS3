package com.example.dacs3.ui.cart

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dacs3.databinding.ActivityCartBinding
import com.example.dacs3.model.CartItemDTO
import com.example.dacs3.network.RetrofitClient
import com.example.dacs3.ui.checkout.CheckoutActivity
import kotlinx.coroutines.launch

class CartActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCartBinding
    private lateinit var cartAdapter: CartAdapter
    private var cartList = mutableListOf<CartItemDTO>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarCart)
        binding.toolbarCart.setNavigationOnClickListener { finish() }
        binding.rvCart.layoutManager = LinearLayoutManager(this)

        binding.cbSelectAll.setOnCheckedChangeListener { _, isChecked ->
            cartList.forEach { it.isSelected = isChecked }
            if (::cartAdapter.isInitialized) cartAdapter.notifyDataSetChanged()
            calculateTotal()
        }

        binding.btnCheckout.setOnClickListener {
            val selectedItems = cartList.filter { it.isSelected }
            if (selectedItems.isEmpty()) {
                Toast.makeText(this, "Vui lòng chọn sản phẩm để mua!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val itemsToCheckout = ArrayList(selectedItems)
            var total = 0.0
            itemsToCheckout.forEach { total += (it.price * it.quantity) }

            val intent = Intent(this, CheckoutActivity::class.java)
            intent.putExtra("SELECTED_ITEMS", itemsToCheckout)
            intent.putExtra("TOTAL_AMOUNT", total)
            startActivity(intent)
        }

        loadCartData()
    }

    private fun loadCartData() {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.getInstance(this@CartActivity).getMyCart()
                if (response.isSuccessful && response.body() != null) {
                    cartList.clear()
                    cartList.addAll(response.body()!!)

                    cartAdapter = CartAdapter(cartList,
                        onTotalChanged = {
                            calculateTotal()
                            checkIfAllSelected()
                        },
                        onDeleteClick = { itemToDelete, position ->
                            deleteCartItem(itemToDelete, position)
                        },
                        // BỔ SUNG: Bắt sự kiện cộng/trừ để gọi API cập nhật DB
                        onQuantityChanged = { updatedItem ->
                            updateCartQuantityAPI(updatedItem.cartId, updatedItem.quantity)
                        }
                    )
                    binding.rvCart.adapter = cartAdapter
                    calculateTotal()
                }
            } catch (e: Exception) {
                Toast.makeText(this@CartActivity, "Lỗi tải giỏ hàng!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateCartQuantityAPI(cartId: Int, newQty: Int) {
        lifecycleScope.launch {
            try {
                // Gọi API thật lên Spring Boot
                val response = RetrofitClient.getInstance(this@CartActivity).updateCartQuantity(cartId, newQty)

                if (!response.isSuccessful) {
                    Toast.makeText(this@CartActivity, "Lỗi đồng bộ số lượng với máy chủ", Toast.LENGTH_SHORT).show()
                }
                // Nếu thành công thì âm thầm cập nhật dưới DB, không cần báo Toast để đỡ phiền người dùng

            } catch (e: Exception) {
                Toast.makeText(this@CartActivity, "Lỗi mạng khi cập nhật số lượng", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun deleteCartItem(item: CartItemDTO, position: Int) {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.getInstance(this@CartActivity).removeCartItem(item.cartId)
                if (response.isSuccessful) {
                    cartList.removeAt(position)
                    cartAdapter.notifyItemRemoved(position)
                    cartAdapter.notifyItemRangeChanged(position, cartList.size)
                    calculateTotal()
                    checkIfAllSelected()
                    Toast.makeText(this@CartActivity, "Đã xóa sản phẩm", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@CartActivity, "Lỗi mạng!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun calculateTotal() {
        var totalAmount = 0.0
        var totalItems = 0
        for (item in cartList) {
            if (item.isSelected) {
                totalAmount += (item.price * item.quantity)
                totalItems++
            }
        }
        binding.tvCartTotal.text = "${String.format("%,.0f", totalAmount)}đ"
        binding.btnCheckout.text = "Mua hàng ($totalItems)"
    }

    private fun checkIfAllSelected() {
        binding.cbSelectAll.setOnCheckedChangeListener(null)
        binding.cbSelectAll.isChecked = cartList.isNotEmpty() && cartList.all { it.isSelected }
        binding.cbSelectAll.setOnCheckedChangeListener { _, isChecked ->
            cartList.forEach { it.isSelected = isChecked }
            cartAdapter.notifyDataSetChanged()
            calculateTotal()
        }
    }
}