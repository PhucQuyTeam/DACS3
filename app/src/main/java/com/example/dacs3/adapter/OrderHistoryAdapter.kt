package com.example.dacs3.ui.order

import android.content.Intent
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
        // Đã xóa imgProduct và tvName
        val tvId: TextView = view.findViewById(R.id.tvOrderId)
        val tvDate: TextView = view.findViewById(R.id.tvOrderDate)
        val tvAddress: TextView = view.findViewById(R.id.tvAddress)
        val tvPrice: TextView = view.findViewById(R.id.tvTotalPrice)
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

        // Chuẩn bị cho tính năng xem chi tiết đơn hàng
        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = android.content.Intent(context, OrderDetailActivity::class.java)

            // Truyền các thông tin quan trọng sang màn hình chi tiết
            intent.putExtra("ORDER_ID", order.id)
            intent.putExtra("ORDER_TOTAL", order.totalPrice)
            intent.putExtra("ORDER_ADDRESS", order.addressDetail)
            intent.putExtra("ORDER_DATE", order.orderDate)

            context.startActivity(intent)
        }
    }

    override fun getItemCount() = orders.size
}