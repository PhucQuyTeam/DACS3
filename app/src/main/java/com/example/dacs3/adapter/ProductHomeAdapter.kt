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

class ProductHomeAdapter(private val onItemClick: (Int) -> Unit): RecyclerView.Adapter<ProductHomeAdapter.ProductViewHolder>() {
    private var productList = listOf<ProductHomeDTO>()

    private val BASE_IMAGE_URL = "http://10.0.2.2:8081/upload/"

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
            binding.tvRating.text = "${product.averageRating} ⭐"

            var imageName = product.img ?: ""
            if (imageName.startsWith("upload/")) {
                imageName = imageName.replaceFirst("upload/", "")
            }


            val fullImageUrl = BASE_IMAGE_URL + imageName

            // Xử lý load ảnh bằng Glide.
            Glide.with(binding.root.context)
                .load(fullImageUrl)
                .placeholder(R.drawable.logoaquariumshop) // Ảnh hiển thị khi đang load
                .error(R.drawable.logoaquariumshop)       // Ảnh hiển thị khi lỗi
                .into(binding.ivProductImage)

            binding.root.setOnClickListener {
                onItemClick(product.productId)
            }
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