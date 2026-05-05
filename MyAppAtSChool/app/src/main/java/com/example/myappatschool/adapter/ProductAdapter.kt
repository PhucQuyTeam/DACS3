package com.example.myappatschool.adapter

import com.example.myappatschool.R
import android.view.*
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myappatschool.model.Product
class ProductAdapter(
    private val list: List<Product>,
    private val onClick: (Product) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name = view.findViewById<TextView>(R.id.txtName)
        val price = view.findViewById<TextView>(R.id.txtPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = list[position]

        holder.name.text = product.name
        holder.price.text = "${product.price} VND"

        holder.itemView.setOnClickListener {
            onClick(product)
        }
    }
}