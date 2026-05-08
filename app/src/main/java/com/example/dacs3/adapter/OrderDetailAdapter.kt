package com.example.dacs3.ui.order

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.dacs3.R
import com.example.dacs3.model.OrderItemDTO

class OrderDetailAdapter(private val items: List<OrderItemDTO>) :
    RecyclerView.Adapter<OrderDetailAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgItem: ImageView = view.findViewById(R.id.imgItem)
        val tvName: TextView = view.findViewById(R.id.tvItemName)
        val tvPrice: TextView = view.findViewById(R.id.tvItemPrice)
        val tvQty: TextView = view.findViewById(R.id.tvItemQty)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Nhớ tạo file item_order_detail.xml mà mình đã gửi ở tin nhắn trước nhé
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_order_detail, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        holder.tvName.text = item.productName
        holder.tvPrice.text = "${String.format("%,.0f", item.price)}đ"
        holder.tvQty.text = "x${item.quantity}"

        // Xử lý link ảnh an toàn (giống hệt bên OrderHistoryAdapter)
        val imageUrl = if (!item.productImage.isNullOrEmpty()) {
            if (item.productImage.startsWith("http")) {
                item.productImage.replace("localhost", "10.0.2.2")
            } else {
                "http://10.0.2.2:8081/upload/" + item.productImage
            }
        } else {
            ""
        }

        // Tải ảnh bằng Glide
        Glide.with(holder.itemView.context)
            .load(imageUrl)
            .placeholder(R.drawable.logoaquariumshop)
            .error(R.drawable.logoaquariumshop)
            .into(holder.imgItem)
    }

    override fun getItemCount() = items.size
}