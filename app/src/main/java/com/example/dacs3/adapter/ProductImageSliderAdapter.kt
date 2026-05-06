package com.example.dacs3.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.dacs3.R

class ProductImageSliderAdapter(private val imageUrls: List<String>) : RecyclerView.Adapter<ProductImageSliderAdapter.SliderViewHolder>() {

    inner class SliderViewHolder(val imageView: ImageView) : RecyclerView.ViewHolder(imageView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SliderViewHolder {
        val imageView = ImageView(parent.context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            scaleType = ImageView.ScaleType.CENTER_CROP
        }
        return SliderViewHolder(imageView)
    }

    override fun onBindViewHolder(holder: SliderViewHolder, position: Int) {
        Glide.with(holder.imageView.context)
            .load(imageUrls[position])
            .placeholder(R.drawable.logoaquariumshop)
            .into(holder.imageView)
    }

    override fun getItemCount(): Int = imageUrls.size
}