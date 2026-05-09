package com.example.dacs3.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.dacs3.R
import com.example.dacs3.databinding.ItemChatReceivedBinding
import com.example.dacs3.databinding.ItemChatSentBinding
import com.example.dacs3.model.ChatDTO

class ChatAdapter(private val adminId: Int) : ListAdapter<ChatDTO, RecyclerView.ViewHolder>(ChatDiffCallback()) {

    companion object {
        private const val VIEW_TYPE_SENT = 1
        private const val VIEW_TYPE_RECEIVED = 2

        // Cấu hình URL Backend để load Avatar (Giống bên ReviewAdapter)
        private const val BASE_IMAGE_URL = "http://10.0.2.2:8081/upload/"
    }

    // 1. Phân loại giao diện: Tin nhắn đi (1) hay Tin nhắn đến (2)?
    override fun getItemViewType(position: Int): Int {
        val message = getItem(position)
        return if (message.senderId == adminId) {
            VIEW_TYPE_RECEIVED
        } else {
            VIEW_TYPE_SENT
        }
    }

    // 2. Khởi tạo Layout tương ứng với ViewType
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_SENT) {
            val binding = ItemChatSentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            SentMessageViewHolder(binding)
        } else {
            val binding = ItemChatReceivedBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            ReceivedMessageViewHolder(binding)
        }
    }

    // 3. Đổ dữ liệu vào Layout
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = getItem(position)
        if (holder is SentMessageViewHolder) {
            holder.bind(message)
        } else if (holder is ReceivedMessageViewHolder) {
            holder.bind(message)
        }
    }

    // --- VIEWHOLDER CHO TIN NHẮN GỬI ĐI (Màu cam) ---
    inner class SentMessageViewHolder(private val binding: ItemChatSentBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(chat: ChatDTO) {
//            binding.tvMessage.text = chat.message
//            binding.tvTime.text = formatTime(chat.createdAt)

            binding.tvTime.text = formatTime(chat.createdAt)

            if (chat.messageType == "image") {
                // Nếu là ảnh: Ẩn chữ, Hiện ảnh
                binding.tvMessage.visibility = View.GONE
                binding.ivChatImage.visibility = View.VISIBLE

                val imageUrl = BASE_IMAGE_URL + chat.message // chat.message lúc này đang lưu tên file ảnh
                Glide.with(binding.root.context)
                    .load(imageUrl)
                    .into(binding.ivChatImage)
            } else {
                // Nếu là text bình thường: Ẩn ảnh, Hiện chữ
                binding.ivChatImage.visibility = View.GONE
                binding.tvMessage.visibility = View.VISIBLE
                binding.tvMessage.text = chat.message
            }


            // Nếu có chức năng gửi ảnh, xử lý ẩn/hiện ImageView ở đây
        }
    }

    // --- VIEWHOLDER CHO TIN NHẮN NHẬN VỀ (Màu trắng + Avatar) ---
    inner class ReceivedMessageViewHolder(private val binding: ItemChatReceivedBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(chat: ChatDTO) {
//            binding.tvMessage.text = chat.message
//            binding.tvTime.text = formatTime(chat.createdAt)

            binding.tvTime.text = formatTime(chat.createdAt)

            if (chat.messageType == "image") {
                // Nếu là ảnh: Ẩn chữ, Hiện ảnh
                binding.tvMessage.visibility = View.GONE
                binding.ivChatImage.visibility = View.VISIBLE

                val imageUrl = BASE_IMAGE_URL + chat.message // chat.message lúc này đang lưu tên file ảnh
                Glide.with(binding.root.context)
                    .load(imageUrl)
                    .into(binding.ivChatImage)
            } else {
                // Nếu là text bình thường: Ẩn ảnh, Hiện chữ
                binding.ivChatImage.visibility = View.GONE
                binding.tvMessage.visibility = View.VISIBLE
                binding.tvMessage.text = chat.message
            }

            // Chat 1-1 thường không cần hiện tên Shop trên từng tin nhắn cho đỡ rối
            binding.tvSenderName.visibility = View.GONE

            // Load Avatar của Shop bằng Glide
            var imageName = chat.senderAvatar ?: ""
            if (imageName.startsWith("upload/")) {
                imageName = imageName.replaceFirst("upload/", "")
            }

            val fullImageUrl = if (imageName.isNotEmpty() && !imageName.startsWith("http")) {
                BASE_IMAGE_URL + imageName
            } else {
                imageName // Trường hợp avatar đã là một đường link URL hoàn chỉnh
            }

            Glide.with(binding.root.context)
                .load(fullImageUrl)
                .placeholder(R.drawable.logoaquariumshop)
                .error(R.drawable.logoaquariumshop)
                .into(binding.ivAvatar)
        }
    }

    // --- HÀM TIỆN ÍCH: Cắt chuỗi thời gian ---
    // Chuyển từ "2026-05-09 14:30:00" -> "14:30"
    private fun formatTime(dateTimeString: String?): String {
        if (dateTimeString.isNullOrEmpty()) return ""
        return try {
            val timePart = if (dateTimeString.contains("T")) {
                dateTimeString.substringAfter("T").substring(0, 5) // Lấy HH:mm từ chuỗi ISO
            } else {
                dateTimeString.substringAfter(" ").substring(0, 5) // Lấy HH:mm từ chuỗi JDBC
            }
            timePart
        } catch (e: Exception) {
            dateTimeString // Lỗi thì in nguyên chuỗi cũ
        }
    }
}

// --- THUẬT TOÁN DIFF UTIL (Chống giật lag khi WebSocket đẩy tin mới) ---
class ChatDiffCallback : DiffUtil.ItemCallback<ChatDTO>() {
    // So sánh ID xem có phải cùng 1 tin nhắn không
    override fun areItemsTheSame(oldItem: ChatDTO, newItem: ChatDTO): Boolean {
        return oldItem.id == newItem.id
    }

    // So sánh nội dung xem tin nhắn có bị chỉnh sửa không
    override fun areContentsTheSame(oldItem: ChatDTO, newItem: ChatDTO): Boolean {
        return oldItem == newItem
    }
}