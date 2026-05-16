package com.example.dacs3.ui.order

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.dacs3.R
import com.example.dacs3.model.OrderItemDTO
import com.google.android.material.button.MaterialButton

// ĐÃ SỬA: Đổi từ (Int) -> Unit thành (Int, Int) -> Unit
class OrderDetailAdapter(
    private val items: List<OrderItemDTO>,
    private val deliveryStatus: String,
    private val onReviewClick: (Int, Int) -> Unit
) : RecyclerView.Adapter<OrderDetailAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgItem: ImageView = view.findViewById(R.id.imgItem)
        val tvName: TextView = view.findViewById(R.id.tvItemName)
        val tvPrice: TextView = view.findViewById(R.id.tvItemPrice)
        val tvQty: TextView = view.findViewById(R.id.tvItemQty)
        val btnReview: MaterialButton = view.findViewById(R.id.btnReviewProduct)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_order_detail, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        holder.tvName.text = item.productName
        holder.tvPrice.text = "${String.format("%,.0f", item.price)}đ"
        holder.tvQty.text = "x${item.quantity}"

        // Xử lý link ảnh an toàn
        val imageUrl = if (!item.productImage.isNullOrEmpty()) {
            if (item.productImage.startsWith("http")) {
                item.productImage.replace("localhost", "10.0.2.2")
            } else {
                "http://10.0.2.2:8081/upload/" + item.productImage
            }
        } else {
            ""
        }

        Glide.with(holder.itemView.context)
            .load(imageUrl)
            .placeholder(R.drawable.logoaquariumshop)
            .error(R.drawable.logoaquariumshop)
            .into(holder.imgItem)

        // LOGIC HIỆN NÚT ĐÁNH GIÁ
        if (deliveryStatus.equals("delivered", ignoreCase = true)) {
            holder.btnReview.visibility = View.VISIBLE

            if (item.isReviewed) {
                // ĐÃ ĐÁNH GIÁ -> Khóa nút
                holder.btnReview.text = "Đã đánh giá"
                holder.btnReview.isEnabled = false
                holder.btnReview.setBackgroundColor(Color.parseColor("#BDBDBD"))
                holder.btnReview.setIconTintResource(android.R.color.white)
                holder.btnReview.alpha = 1f
            } else {
                // CHƯA ĐÁNH GIÁ -> Mở nút
                holder.btnReview.text = "Viết đánh giá"
                holder.btnReview.isEnabled = true
                holder.btnReview.setBackgroundResource(R.drawable.bg_btn_review)

                holder.btnReview.alpha = 0f
                holder.btnReview.animate().alpha(1f).setDuration(500).start()

                holder.btnReview.setOnClickListener {
                    // ĐÃ SỬA: Ném thêm cái position ra ngoài để Activity biết đang bấm dòng nào
                    onReviewClick(item.productId, position)
                }
            }
        } else {
            holder.btnReview.visibility = View.GONE
        }
    }

    override fun getItemCount() = items.size
}