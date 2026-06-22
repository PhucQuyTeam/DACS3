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


        private const val BASE_IMAGE_URL = "http://10.0.2.2:8081/upload/"
    }


    override fun getItemViewType(position: Int): Int {
        val message = getItem(position)
        return if (message.senderId == adminId) {
            VIEW_TYPE_RECEIVED
        } else {
            VIEW_TYPE_SENT
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_SENT) {
            val binding = ItemChatSentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            SentMessageViewHolder(binding)
        } else {
            val binding = ItemChatReceivedBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            ReceivedMessageViewHolder(binding)
        }
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = getItem(position)
        if (holder is SentMessageViewHolder) {
            holder.bind(message)
        } else if (holder is ReceivedMessageViewHolder) {
            holder.bind(message)
        }
    }


    inner class SentMessageViewHolder(private val binding: ItemChatSentBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(chat: ChatDTO) {


            binding.tvTime.text = formatTime(chat.createdAt)

            if (chat.messageType == "image") {

                binding.tvMessage.visibility = View.GONE
                binding.ivChatImage.visibility = View.VISIBLE

                val imageUrl = BASE_IMAGE_URL + chat.message
                Glide.with(binding.root.context)
                    .load(imageUrl)
                    .into(binding.ivChatImage)
            } else {

                binding.ivChatImage.visibility = View.GONE
                binding.tvMessage.visibility = View.VISIBLE
                binding.tvMessage.text = chat.message
            }



        }
    }

    inner class ReceivedMessageViewHolder(private val binding: ItemChatReceivedBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(chat: ChatDTO) {


            binding.tvTime.text = formatTime(chat.createdAt)

            if (chat.messageType == "image") {

                binding.tvMessage.visibility = View.GONE
                binding.ivChatImage.visibility = View.VISIBLE

                val imageUrl = BASE_IMAGE_URL + chat.message
                Glide.with(binding.root.context)
                    .load(imageUrl)
                    .into(binding.ivChatImage)
            } else {

                binding.ivChatImage.visibility = View.GONE
                binding.tvMessage.visibility = View.VISIBLE
                binding.tvMessage.text = chat.message
            }


            binding.tvSenderName.visibility = View.GONE


            var imageName = chat.senderAvatar ?: ""
            if (imageName.startsWith("upload/")) {
                imageName = imageName.replaceFirst("upload/", "")
            }

            val fullImageUrl = if (imageName.isNotEmpty() && !imageName.startsWith("http")) {
                BASE_IMAGE_URL + imageName
            } else {
                imageName
            }

            Glide.with(binding.root.context)
                .load(fullImageUrl)
                .placeholder(R.drawable.logoaquariumshop1)
                .error(R.drawable.logoaquariumshop1)
                .into(binding.ivAvatar)
        }
    }


    private fun formatTime(dateTimeString: String?): String {
        if (dateTimeString.isNullOrEmpty()) return ""
        return try {
            val timePart = if (dateTimeString.contains("T")) {
                dateTimeString.substringAfter("T").substring(0, 5)
            } else {
                dateTimeString.substringAfter(" ").substring(0, 5)
            }
            timePart
        } catch (e: Exception) {
            dateTimeString
        }
    }
}


class ChatDiffCallback : DiffUtil.ItemCallback<ChatDTO>() {
    override fun areItemsTheSame(oldItem: ChatDTO, newItem: ChatDTO): Boolean {
        return oldItem.id == newItem.id
    }


    override fun areContentsTheSame(oldItem: ChatDTO, newItem: ChatDTO): Boolean {
        return oldItem == newItem
    }
}