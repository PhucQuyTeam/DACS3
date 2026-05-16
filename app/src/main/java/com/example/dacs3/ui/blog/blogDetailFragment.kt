package com.example.dacs3.ui.blog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.dacs3.R
import com.example.dacs3.databinding.FragmentBlogDetailBinding
import com.example.dacs3.model.BlogDTO

class blogDetailFragment : Fragment() {

    private var _binding: FragmentBlogDetailBinding? = null
    private val binding get() = _binding!!
    private val BASE_IMAGE_URL = "http://10.0.2.2:8081/upload/"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentBlogDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBackDetail.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        // Bóc gói hàng BlogDTO từ Bundle
        val blog = arguments?.getSerializable("BLOG_DATA") as? BlogDTO

        if (blog != null) {
            // Đổ dữ liệu lên giao diện
            binding.tvDetailCategory.text = blog.categoryName ?: "CHƯA PHÂN LOẠI"
            binding.tvDetailTitle.text = blog.title
            binding.tvDetailDescription.text = blog.description
            binding.tvDetailContent.text = blog.content

            // Xử lý ngày tháng tạm thời lấy luôn chuỗi (hoặc bạn có thể copy hàm formatDate sang đây)
            binding.tvDetailDate.text = "Ngày đăng: ${blog.createdAt?.substringBefore("T")}"

            // Load ảnh header
            var imageName = blog.image ?: ""
            if (imageName.startsWith("upload/")) imageName = imageName.replaceFirst("upload/", "")

            Glide.with(this)
                .load(BASE_IMAGE_URL + imageName)
                .placeholder(R.drawable.logoaquariumshop)
                .error(R.drawable.logoaquariumshop)
                .into(binding.ivDetailHeader)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}