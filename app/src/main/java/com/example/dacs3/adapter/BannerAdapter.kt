package com.example.dacs3.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.dacs3.databinding.ItemBannerBinding

class BannerAdapter(private val imageList: List<Int>) : RecyclerView.Adapter<BannerAdapter.BannerViewHolder>() {

    // 1. ViewHolder giờ đây sẽ nhận vào một đối tượng Binding thay vì View thông thường
    inner class BannerViewHolder(val binding: ItemBannerBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannerViewHolder {
        // 2. Sử dụng class Binding để inflate giao diện
        val binding = ItemBannerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BannerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BannerViewHolder, position: Int) {
        // 3. Gọi trực tiếp ID thông qua biến binding, không cần findViewById nữa
        holder.binding.imageViewBanner.setImageResource(imageList[position])
    }

    override fun getItemCount(): Int = imageList.size
}