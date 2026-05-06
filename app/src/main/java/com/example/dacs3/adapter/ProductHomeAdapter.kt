package com.example.dacs3.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.dacs3.R
import com.example.dacs3.databinding.ItemProductHomeBinding
import com.example.dacs3.model.ProductHomeDTO
import java.text.NumberFormat
import java.util.Locale

class ProductHomeAdapter : RecyclerView.Adapter<ProductHomeAdapter.ProductViewHolder>() {
    private var productList = listOf<ProductHomeDTO>()

    fun submitList(list: List<ProductHomeDTO>) {
        productList = list
        notifyDataSetChanged()
    }

    inner class ProductViewHolder(val binding: ItemProductHomeBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(product: ProductHomeDTO) {
            binding.tvProductName.text = product.name

            // Format giá tiền thành VND (Ví dụ: 100,000 đ)
            val format = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))
            binding.tvProductPrice.text = format.format(product.price)

            binding.tvProductSold.text = "Đã bán: ${product.totalProductQuantity}"

            // Xử lý load ảnh bằng Glide.
            // Lưu ý: Tùy thuộc vào đường dẫn ảnh lưu trong DB của bạn là full URL (http...) hay chỉ là tên file.
            val imageUrl = product.img ?: ""
            Glide.with(binding.root.context)
                .load(imageUrl)
                .placeholder(R.drawable.logoaquariumshop) // Ảnh hiển thị khi đang load
                .error(R.drawable.logoaquariumshop)       // Ảnh hiển thị khi lỗi
                .into(binding.ivProductImage)

            binding.root.tag = product.productId;
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ProductHomeAdapter.ProductViewHolder {
        val binding = ItemProductHomeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ProductHomeAdapter.ProductViewHolder,
        position: Int
    ) {
        holder.bind(productList[position])
    }

    override fun getItemCount(): Int = productList.size
}