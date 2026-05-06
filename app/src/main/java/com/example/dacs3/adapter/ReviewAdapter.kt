package com.example.dacs3.adapter

import android.os.flagging.AconfigPackage.load
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.dacs3.R
import com.example.dacs3.databinding.ItemReviewBinding
import com.example.dacs3.model.ReviewDTO

class ReviewAdapter: RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>() {
    private var reviewList = listOf<ReviewDTO>()

    fun submitList(list: List<ReviewDTO>) {
        reviewList = list
        notifyDataSetChanged()
    }

    inner class ReviewViewHolder(val binding: ItemReviewBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(review: ReviewDTO) {
            binding.tvReviewerName.text = review.reviewerName
            binding.ratingBar.rating = review.rating.toFloat()
            binding.tvReviewComment.text = review.comment
            binding.tvReviewDate.text = review.createdAt.toString().substringBefore("T")

            if(!review.image.isNullOrEmpty()){
                binding.ivReviewImage.visibility = View.VISIBLE
                Glide.with(binding.root.context)
                    .load(review.image)
                    .placeholder(R.drawable.logoaquariumshop)
                    .into(binding.ivReviewImage)
            }else{
                binding.ivReviewImage.visibility = View.GONE
            }

        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ReviewAdapter.ReviewViewHolder {
        val binding = ItemReviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ReviewViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReviewAdapter.ReviewViewHolder, position: Int) {
        holder.bind(reviewList[position])
    }

    override fun getItemCount(): Int = reviewList.size
}