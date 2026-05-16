package com.example.dacs3.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.dacs3.R
import com.example.dacs3.databinding.ItemBlogBinding
import com.example.dacs3.model.BlogDTO
import java.text.SimpleDateFormat
import java.util.Locale

class BlogAdapter(private val onItemClick: (BlogDTO) -> Unit) :
    ListAdapter<BlogDTO, BlogAdapter.BlogViewHolder>(BlogDiffCallback()) {

    private val BASE_IMAGE_URL = "http://10.0.2.2:8081/upload/"

    inner class BlogViewHolder(val binding: ItemBlogBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(blog: BlogDTO) {
            binding.tvBlogTitle.text = blog.title
            binding.tvBlogCategory.text = blog.categoryName ?: "Chưa phân loại"

            // Xử lý cắt chuỗi ngày tháng cho đẹp (Từ 2026-05-10T... -> 10/05/2026)
            binding.tvBlogDate.text = formatDate(blog.createdAt)

            // Xử lý Load Ảnh
            var imageName = blog.image ?: ""
            if (imageName.startsWith("upload/")) imageName = imageName.replaceFirst("upload/", "")

            Glide.with(binding.root.context)
                .load(BASE_IMAGE_URL + imageName)
                .placeholder(R.drawable.logoaquariumshop)
                .error(R.drawable.logoaquariumshop)
                .into(binding.ivBlogThumbnail)

            // Sự kiện click vào bài viết
            binding.root.setOnClickListener {
                onItemClick(blog)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlogViewHolder {
        val binding = ItemBlogBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BlogViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BlogViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    private fun formatDate(dateString: String?): String {
        if (dateString.isNullOrEmpty()) return ""
        try {
            // Giả sử server trả về chuẩn ISO
            val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val date = parser.parse(dateString)
            return if (date != null) formatter.format(date) else dateString
        } catch (e: Exception) {
            return dateString.substringBefore(" ") // Fix cứng nếu lỗi
        }
    }
}

class BlogDiffCallback : DiffUtil.ItemCallback<BlogDTO>() {
    override fun areItemsTheSame(oldItem: BlogDTO, newItem: BlogDTO) = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: BlogDTO, newItem: BlogDTO) = oldItem == newItem
}