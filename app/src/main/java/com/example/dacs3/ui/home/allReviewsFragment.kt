package com.example.dacs3.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dacs3.R
import com.example.dacs3.adapter.ReviewAdapter
import com.example.dacs3.databinding.FragmentAllReviewsBinding
import com.example.dacs3.model.ReviewDTO
import com.example.dacs3.network.RetrofitClient
import com.example.dacs3.repository.ProductDetailRepository
import com.example.dacs3.viewmodel.ProductDetailViewModel
import com.example.dacs3.viewmodel.ProductDetailViewModelFactory

class allReviewsFragment : Fragment() {

    private var _binding: FragmentAllReviewsBinding? = null
    private val binding get() = _binding!!

    private lateinit var reviewAdapter: ReviewAdapter
    private lateinit var viewModel: ProductDetailViewModel

    private var productId: Int = -1

    // Biến lưu trữ dữ liệu để lọc
    private var originalReviews = listOf<ReviewDTO>()
    private var currentStarFilter: Int? = null // null nghĩa là lấy tất cả sao
    private var isImageOnly: Boolean = false   // Có yêu cầu hình ảnh không?

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAllReviewsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Nhận ProductId từ trang Chi tiết gửi sang
        productId = arguments?.getInt("productId") ?: -1

        setupTopBar()
        setupRecyclerView()
        setupViewModel()
        setupFilters()

        // 2. Gọi API lấy dữ liệu
        if (productId != -1) {
            viewModel.fetchProductData(productId)
        } else {
            Toast.makeText(requireContext(), "Lỗi không tìm thấy sản phẩm", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupTopBar() {
        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setupRecyclerView() {
        reviewAdapter = ReviewAdapter()
        binding.rvAllReviews.apply {
            adapter = reviewAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun setupViewModel() {
        val apiService = RetrofitClient.getInstance(requireContext())
        val repository = ProductDetailRepository(apiService)
        val factory = ProductDetailViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[ProductDetailViewModel::class.java]

        viewModel.review.observe(viewLifecycleOwner) { reviews ->
            if (reviews != null) {
                // Lọc bỏ những phần tử null (nếu có) và lưu vào bản gốc
                originalReviews = reviews.filterNotNull()

                // Hiển thị lần đầu tiên (Tất cả)
                applyFilters()
            }
        }
    }

    // --- XỬ LÝ LOGIC BỘ LỌC ---
    private fun setupFilters() {
        // 1. Khi ấn "Tất cả"
        binding.chipAll.setOnClickListener {
            // Reset các biến lọc
            currentStarFilter = null
            isImageOnly = false

            // Reset UI của các Chip khác
            binding.chipAll.isChecked = true
            binding.chipHasImage.isChecked = false
            binding.chipStarDropdown.text = "Lọc số sao"

            applyFilters()
        }

        // 2. Khi ấn "Có hình ảnh"
        binding.chipHasImage.setOnCheckedChangeListener { _, isChecked ->
            isImageOnly = isChecked

            // Nếu đã chọn lọc ảnh thì phải tắt chip "Tất cả" đi
            if (isChecked) binding.chipAll.isChecked = false

            applyFilters()
        }

        // 3. Khi ấn "Lọc số sao" (Sổ Popup Menu)
        binding.chipStarDropdown.setOnClickListener { view ->
            showStarDropdownMenu(view)
        }
    }

    private fun showStarDropdownMenu(anchorView: View) {
        val popupMenu = PopupMenu(requireContext(), anchorView)

        // Thêm các lựa chọn vào Menu (groupId, itemId, order, title)
        popupMenu.menu.add(0, 5, 0, "5 Sao ⭐⭐⭐⭐⭐")
        popupMenu.menu.add(0, 4, 1, "4 Sao ⭐⭐⭐⭐")
        popupMenu.menu.add(0, 3, 2, "3 Sao ⭐⭐⭐")
        popupMenu.menu.add(0, 2, 3, "2 Sao ⭐⭐")
        popupMenu.menu.add(0, 1, 4, "1 Sao ⭐")

        popupMenu.setOnMenuItemClickListener { menuItem ->
            // Lấy ra số sao tương ứng với ID mình vừa đặt
            currentStarFilter = menuItem.itemId

            // Đổi chữ trên Chip thành "5 Sao" hoặc "4 Sao"...
            binding.chipStarDropdown.text = "${menuItem.itemId} Sao"

            // Tắt chip "Tất cả"
            binding.chipAll.isChecked = false

            applyFilters()
            true
        }
        popupMenu.show()
    }

    // --- HÀM LÕI: ÁP DỤNG CÁC ĐIỀU KIỆN LỌC ---
    private fun applyFilters() {
        var filteredList = originalReviews

        // Lọc theo số sao (Nếu có chọn)
        if (currentStarFilter != null) {
            filteredList = filteredList.filter { it.rating == currentStarFilter }
        }

        // Lọc theo hình ảnh (Chỉ lấy review nào có biến image khác null và rỗng)
        if (isImageOnly) {
            filteredList = filteredList.filter { !it.image.isNullOrEmpty() }
        }

        // Cập nhật lên màn hình
        reviewAdapter.submitList(filteredList)

        // Tùy chọn: Hiện thông báo mờ nếu lọc không ra kết quả nào
        if (filteredList.isEmpty()) {
            Toast.makeText(requireContext(), "Không có đánh giá nào phù hợp!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}