package com.example.dacs3.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.dacs3.databinding.ItemChatReceivedBinding
import com.example.dacs3.databinding.ItemChatSentBinding
import com.example.dacs3.model.AIMessageModel

class AIChatAdapter : ListAdapter<AIMessageModel, RecyclerView.ViewHolder>(AIDiffCallback()) {

    companion object {
        private const val VIEW_TYPE_USER = 1
        private const val VIEW_TYPE_AI = 2
    }

    override fun getItemViewType(position: Int): Int {
        return if (getItem(position).isUser) VIEW_TYPE_USER else VIEW_TYPE_AI
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_USER) {
            val binding = ItemChatSentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            UserViewHolder(binding)
        } else {
            val binding = ItemChatReceivedBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            AIViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = getItem(position)
        if (holder is UserViewHolder) holder.bind(message)
        else if (holder is AIViewHolder) holder.bind(message)
    }

    inner class UserViewHolder(private val binding: ItemChatSentBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(message: AIMessageModel) {
            binding.tvMessage.text = message.text
            binding.tvTime.visibility = View.GONE
        }
    }

    inner class AIViewHolder(private val binding: ItemChatReceivedBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(message: AIMessageModel) {
            binding.tvMessage.text = message.text
            binding.tvSenderName.text = "Trợ lý ảo AI"
            binding.tvTime.visibility = View.GONE

            // Nếu AI đang gõ thì làm chữ in nghiêng màu nhạt
            if (message.isTyping) {
                binding.tvMessage.setTypeface(null, android.graphics.Typeface.ITALIC)
            } else {
                binding.tvMessage.setTypeface(null, android.graphics.Typeface.NORMAL)
            }


        }
    }
}

class AIDiffCallback : DiffUtil.ItemCallback<AIMessageModel>() {
    override fun areItemsTheSame(oldItem: AIMessageModel, newItem: AIMessageModel) = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: AIMessageModel, newItem: AIMessageModel) = oldItem == newItem
}