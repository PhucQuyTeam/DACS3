package com.example.dacs3.ui.cart

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.dacs3.R
import com.example.dacs3.model.CartItemDTO

class CartAdapter(
    private val items: MutableList<CartItemDTO>,
    private val onTotalChanged: () -> Unit,
    private val onDeleteClick: (CartItemDTO, Int) -> Unit,
    // BỔ SUNG: Callback báo về Activity khi số lượng thay đổi để gọi API
    private val onQuantityChanged: (CartItemDTO) -> Unit
) : RecyclerView.Adapter<CartAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cbSelect: CheckBox = view.findViewById(R.id.cbCartItem)
        val imgProduct: ImageView = view.findViewById(R.id.imgCartProduct)
        val tvName: TextView = view.findViewById(R.id.tvCartProductName)
        val tvPrice: TextView = view.findViewById(R.id.tvCartPrice)
        val tvQty: TextView = view.findViewById(R.id.tvCartQty)
        val btnMinus: TextView = view.findViewById(R.id.btnCartMinus)
        val btnPlus: TextView = view.findViewById(R.id.btnCartPlus)
        val btnDelete: ImageView = view.findViewById(R.id.btnDeleteCartItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_cart, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        holder.tvName.text = item.productName
        holder.tvPrice.text = "${String.format("%,.0f", item.price)}đ"
        holder.tvQty.text = item.quantity.toString()

        holder.cbSelect.setOnCheckedChangeListener(null)
        holder.cbSelect.isChecked = item.isSelected

        // FIX LỖI ẢNH: Xóa chữ 's' trong 'uploads', gộp logic lấy ảnh cho gọn
        val imageUrl = "http://10.0.2.2:8081/upload/${item.productImage}"

        Glide.with(holder.itemView.context)
            .load(imageUrl)
            .placeholder(R.drawable.logoaquariumshop)
            .error(R.drawable.logoaquariumshop) // Bổ sung bắt lỗi nhỡ URL die
            .into(holder.imgProduct)

        holder.btnDelete.setOnClickListener {
            onDeleteClick(item, holder.adapterPosition)
        }

        holder.cbSelect.setOnCheckedChangeListener { _, isChecked ->
            item.isSelected = isChecked
            onTotalChanged()
        }

        holder.btnMinus.setOnClickListener {
            if (item.quantity > 1) {
                item.quantity--
                holder.tvQty.text = item.quantity.toString()
                if (item.isSelected) onTotalChanged()

                // Gọi callback để lưu API
                onQuantityChanged(item)
            }
        }

        holder.btnPlus.setOnClickListener {
            item.quantity++
            holder.tvQty.text = item.quantity.toString()
            if (item.isSelected) onTotalChanged()

            // Gọi callback để lưu API
            onQuantityChanged(item)
        }
    }

    override fun getItemCount() = items.size
}