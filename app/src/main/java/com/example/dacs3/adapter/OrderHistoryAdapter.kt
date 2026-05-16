package com.example.dacs3.ui.order

import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.dacs3.R
import com.example.dacs3.model.OrderDTO

class OrderHistoryAdapter(private val orders: List<OrderDTO>) :
    RecyclerView.Adapter<OrderHistoryAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvId: TextView = view.findViewById(R.id.tvOrderId)
        val tvDate: TextView = view.findViewById(R.id.tvOrderDate)
        val tvAddress: TextView = view.findViewById(R.id.tvAddress)
        val tvPrice: TextView = view.findViewById(R.id.tvTotalPrice)
        val tvPaymentStatus: TextView = view.findViewById(R.id.tvPaymentStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_order_history, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val order = orders[position]

        holder.tvId.text = "Mã đơn: #${order.id}"
        holder.tvDate.text = "Ngày đặt: ${order.orderDate ?: "Chưa rõ"}"
        holder.tvAddress.text = "Địa chỉ nhận hàng: ${order.addressDetail ?: "Chưa cập nhật địa chỉ"}"
        holder.tvPrice.text = "${String.format("%,.0f", order.totalPrice)}đ"

        if (order.paymentStatus == "paid" || order.paymentStatus == "Đã thanh toán") {
            holder.tvPaymentStatus.text = "Đã thanh toán"
            holder.tvPaymentStatus.setTextColor(Color.parseColor("#4CAF50")) // Màu Xanh lá
        } else {
            holder.tvPaymentStatus.text = "Chưa thanh toán (COD)"
            holder.tvPaymentStatus.setTextColor(Color.parseColor("#FF9800")) // Màu Cam
        }

        // Bắt sự kiện click vào Item để xem chi tiết
        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, OrderDetailActivity::class.java)

            // Truyền thông tin cơ bản
            intent.putExtra("ORDER_ID", order.id)
            intent.putExtra("ORDER_TOTAL", order.totalPrice)
            intent.putExtra("ORDER_ADDRESS", order.addressDetail)
            intent.putExtra("ORDER_DATE", order.orderDate)
            intent.putExtra("ORDER_PAYMENT_STATUS", order.paymentStatus)

            // ĐÃ SỬA: Quy đổi status (0, 1, 2) ra chữ để truyền sang cho màn Chi Tiết
            val deliveryStatusStr = when (order.status) {
                1 -> "shipping"
                2 -> "delivered"
                else -> "pending"
            }
            intent.putExtra("ORDER_DELIVERY_STATUS", deliveryStatusStr)

            context.startActivity(intent)
        }
    }

    override fun getItemCount() = orders.size
}