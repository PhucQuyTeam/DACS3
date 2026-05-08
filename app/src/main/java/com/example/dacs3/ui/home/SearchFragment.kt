package com.example.dacs3.ui.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.dacs3.R
import com.example.dacs3.adapter.ProductHomeAdapter
import com.example.dacs3.databinding.FragmentSearchBinding
import com.example.dacs3.network.RetrofitClient
import com.example.dacs3.repository.ProductRepository
import com.example.dacs3.viewmodel.HomeViewModel
import com.example.dacs3.viewmodel.HomeViewModelFactory
import com.google.android.material.bottomsheet.BottomSheetDialog

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private lateinit var searchAdapter: ProductHomeAdapter
    private lateinit var viewModel: HomeViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Xử lý thanh trạng thái (Status Bar) để không lẹm vào TopBar
        ViewCompat.setOnApplyWindowInsetsListener(binding.layoutTopBar) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(0, systemBars.top, 0, 0)
            insets
        }

        setupRecyclerView()
        setupViewModel()
        setupTopBarActions()
        setupFilterPanel()

        // Mở màn hình lên là tự động focus và bật bàn phím
        binding.etSearchInput.requestFocus()
        showKeyboard(binding.etSearchInput)
    }

    private fun setupRecyclerView() {
        searchAdapter = ProductHomeAdapter { clickedProductId ->
            // Ấn vào sản phẩm -> Đi tới chi tiết
            val bundle = Bundle().apply { putInt("productId", clickedProductId) }
            findNavController().navigate(R.id.action_searchFragment_to_productDetailFragment, bundle)

            // Tắt bàn phím trước khi chuyển trang
            hideKeyboard(binding.etSearchInput)
        }
        binding.rvSearchResults.adapter = searchAdapter
    }

    private fun setupViewModel() {
        val repository = ProductRepository(RetrofitClient.getInstance(requireContext()))
        viewModel = ViewModelProvider(this, HomeViewModelFactory(repository))[HomeViewModel::class.java]

        viewModel.products.observe(viewLifecycleOwner) { productList ->
            searchAdapter.submitList(productList)

            // Hiện thông báo nếu không có kết quả
            if (productList.isEmpty() && binding.etSearchInput.text.isNotEmpty()) {
                binding.tvNoResult.visibility = View.VISIBLE
                binding.rvSearchResults.visibility = View.GONE
            } else {
                binding.tvNoResult.visibility = View.GONE
                binding.rvSearchResults.visibility = View.VISIBLE
            }
        }

        // --- QUAN TRỌNG LÀ KHÚC NÀY ---
        // Lấy chữ từ Home gửi sang
        val queryFromHome = arguments?.getString("SEARCH_QUERY") ?: ""

        if (queryFromHome.isNotEmpty()) {
            binding.etSearchInput.setText(queryFromHome)
            binding.etSearchInput.setSelection(queryFromHome.length) // Đưa con trỏ nháy về cuối chữ

            // Bắt ViewModel "học thuộc" từ khóa này trước
            viewModel.currentSearchQuery = queryFromHome
        }

        // Sau khi học thuộc từ khóa xong mới được đi gọi API.
        // Gọi xong API nó sẽ tự móc từ khóa đó ra để lọc!
        viewModel.fetchProducts()
    }

    private fun setupTopBarActions() {
        // 1. Nút Back
        binding.btnBack.setOnClickListener {
            hideKeyboard(binding.etSearchInput)
            findNavController().popBackStack()
        }

        // 2. Bắt sự kiện gõ chữ (Real-time Search)
        binding.etSearchInput.doOnTextChanged { text, _, _, _ ->
            val query = text?.toString()?.trim() ?: ""

            // Hiện/ẩn nút "X" (Clear text)
            binding.ivClearText.visibility = if (query.isNotEmpty()) View.VISIBLE else View.GONE

            // Truyền xuống ViewModel để lọc
            viewModel.searchProducts(query)
        }

        // 3. Nút X xóa nhanh chữ
        binding.ivClearText.setOnClickListener {
            binding.etSearchInput.text.clear()
            showKeyboard(binding.etSearchInput) // Bật lại bàn phím để gõ tiếp
        }
    }

    // --- CÁC HÀM TIỆN ÍCH BÀN PHÍM ---
    private fun showKeyboard(view: View) {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun hideKeyboard(view: View) {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun setupFilterPanel(){
        binding.btnFilter.setOnClickListener {
            hideKeyboard(binding.etSearchInput)

            val bottomSheet = BottomSheetDialog(requireContext())
            val view = layoutInflater.inflate(R.layout.layout_filter, null)
            bottomSheet.setContentView(view)

            val etMinRating = view.findViewById<EditText>(R.id.etMinRating)
            val etMaxRating = view.findViewById<EditText>(R.id.etMaxRating)
            val etMinPrice = view.findViewById<EditText>(R.id.etMinPrice)
            val etMaxPrice = view.findViewById<EditText>(R.id.etMaxPrice)
            val btnApply = view.findViewById<Button>(R.id.btnApplyFilter)

            btnApply.setOnClickListener {
                val minR = etMinRating.text.toString().toDoubleOrNull() ?: 0.0
                val maxR = etMaxRating.text.toString().toDoubleOrNull() ?: 5.0
                val minP = etMinPrice.text.toString().toIntOrNull() ?: 0
                val maxP = etMaxPrice.text.toString().toIntOrNull() ?: Int.MAX_VALUE

                viewModel.applyAdvancedFilters(minR, maxR, minP, maxP)

                bottomSheet.dismiss()
            }
            bottomSheet.show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}