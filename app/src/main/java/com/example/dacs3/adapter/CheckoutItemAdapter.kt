package com.example.dacs3.ui.checkout

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.dacs3.R
import com.example.dacs3.model.CartItemDTO
import java.text.DecimalFormat

class CheckoutItemAdapter(private val items: List<CartItemDTO>) :
    RecyclerView.Adapter<CheckoutItemAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // ID ở đây đã được sửa khớp 100% với file XML của sếp
        val imgProduct: ImageView = view.findViewById(R.id.imgCheckoutProduct)
        val tvName: TextView = view.findViewById(R.id.tvCheckoutProductName)
        val tvPrice: TextView = view.findViewById(R.id.tvCheckoutPrice)
        val tvQuantity: TextView = view.findViewById(R.id.tvCheckoutQty)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_checkout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        holder.tvName.text = item.productName

        val formatter = DecimalFormat("#,###")
        holder.tvPrice.text = "${formatter.format(item.price)}đ"

        holder.tvQuantity.text = "x${item.quantity}"

        // Load ảnh bằng Glide
        // (Sếp nhớ check lại xem trong CartItemDTO biến ảnh tên là 'image' hay 'productImage' nhé)
        val imageUrl = "http://10.0.2.2:8081/upload/${item.productImage}"

        Glide.with(holder.itemView.context)
            .load(imageUrl)
            .placeholder(R.drawable.logoaquariumshop) // Ảnh lúc đang tải
            .error(R.drawable.logoaquariumshop)       // Ảnh lúc bị lỗi link
            .into(holder.imgProduct)
    }

    override fun getItemCount() = items.size
}