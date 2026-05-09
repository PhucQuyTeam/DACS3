package com.example.dacs3.adapter

import android.graphics.Color
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.dacs3.R
import com.example.dacs3.model.NotificationDTO

class NotificationAdapter(private var notiList: List<NotificationDTO>) :
    RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {

    fun updateData(newList: List<NotificationDTO>) {
        this.notiList = newList
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTitle: TextView = view.findViewById(R.id.tvNotiTitle)
        val tvMessage: TextView = view.findViewById(R.id.tvNotiMessage)
        val tvDate: TextView = view.findViewById(R.id.tvNotiDate)
        val viewUnreadIndicator: View = view.findViewById(R.id.viewUnreadIndicator) // Nhớ có dòng này
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_notification, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val noti = notiList[position]
        holder.tvTitle.text = noti.title
        holder.tvMessage.text = noti.message
        holder.tvDate.text = noti.createdAt
    }

    override fun getItemCount() = notiList.size
}