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
import com.example.dacs3.adapter.ReviewAdapter
import com.example.dacs3.databinding.FragmentAllReviewsBinding
import com.example.dacs3.model.ReviewDTO
import com.example.dacs3.network.RetrofitClient
import com.example.dacs3.repository.ProductDetailRepository
import com.example.dacs3.viewmodel.ProductDetailViewModel
import com.example.dacs3.viewmodel.ProductDetailViewModelFactory

class AllReviewsFragment : Fragment() {

    private var _binding: FragmentAllReviewsBinding? = null
    private val binding get() = _binding!!

    private lateinit var reviewAdapter: ReviewAdapter
    private lateinit var viewModel: ProductDetailViewModel

    private var productId: Int = -1

    private var originalReviews = listOf<ReviewDTO>()
    private var currentStarFilter: Int? = null
    private var isImageOnly: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAllReviewsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        productId = arguments?.getInt("productId") ?: -1

        setupTopBar()
        setupRecyclerView()
        setupViewModel()
        setupFilters()

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

                originalReviews = reviews.filterNotNull()

                applyFilters()
            }
        }
    }


    private fun setupFilters() {

        binding.chipAll.setOnClickListener {
            currentStarFilter = null
            isImageOnly = false

            binding.chipAll.isChecked = true
            binding.chipHasImage.isChecked = false
            binding.chipStarDropdown.text = "Lọc số sao"

            applyFilters()
        }

        binding.chipHasImage.setOnCheckedChangeListener { _, isChecked ->
            isImageOnly = isChecked

            if (isChecked) binding.chipAll.isChecked = false

            applyFilters()
        }

        binding.chipStarDropdown.setOnClickListener { view ->
            showStarDropdownMenu(view)
        }
    }

    private fun showStarDropdownMenu(anchorView: View) {
        val popupMenu = PopupMenu(requireContext(), anchorView)

        popupMenu.menu.add(0, 5, 0, "5 Sao ⭐⭐⭐⭐⭐")
        popupMenu.menu.add(0, 4, 1, "4 Sao ⭐⭐⭐⭐")
        popupMenu.menu.add(0, 3, 2, "3 Sao ⭐⭐⭐")
        popupMenu.menu.add(0, 2, 3, "2 Sao ⭐⭐")
        popupMenu.menu.add(0, 1, 4, "1 Sao ⭐")

        popupMenu.setOnMenuItemClickListener { menuItem ->
            currentStarFilter = menuItem.itemId

            binding.chipStarDropdown.text = "${menuItem.itemId} Sao"

            binding.chipAll.isChecked = false

            applyFilters()
            true
        }
        popupMenu.show()
    }

    private fun applyFilters() {
        var filteredList = originalReviews

        if (currentStarFilter != null) {
            filteredList = filteredList.filter { it.rating == currentStarFilter }
        }

        if (isImageOnly) {
            filteredList = filteredList.filter { !it.image.isNullOrEmpty() }
        }

        reviewAdapter.submitList(filteredList)

        if (filteredList.isEmpty()) {
            Toast.makeText(requireContext(), "Không có đánh giá nào phù hợp!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}