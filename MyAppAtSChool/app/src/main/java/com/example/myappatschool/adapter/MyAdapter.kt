package com.example.myappatschool.adapter

import android.R
import android.view.*
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MyAdapter(
    private val list: List<String>,
    private val onClick: (String) -> Unit
) : RecyclerView.Adapter<MyAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val text: TextView = view.findViewById(R.id.text1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.simple_list_item_1, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.text.text = item

        holder.itemView.setOnClickListener {
            onClick(item)
        }
    }
}