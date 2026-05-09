package com.example.dacs3.ui.home

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import com.example.dacs3.R
import com.example.dacs3.adapter.BannerAdapter
import com.example.dacs3.adapter.ProductHomeAdapter
import com.example.dacs3.api.ApiService
import com.example.dacs3.databinding.FragmentHomeBinding
import com.example.dacs3.network.RetrofitClient
import com.example.dacs3.repository.ProductRepository
import com.example.dacs3.viewmodel.HomeViewModel
import com.example.dacs3.viewmodel.HomeViewModelFactory
import com.google.android.material.tabs.TabLayoutMediator
import kotlin.math.abs

class HomeFragment : Fragment() {

    // 1. CHUẨN KHAI BÁO BINDING CHO FRAGMENT (Tránh rò rỉ bộ nhớ)
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    // Biến dùng để auto-scroll
    private val sliderHandler = Handler(Looper.getMainLooper())
    private lateinit var sliderRunnable: Runnable

    // MVVM Components
    private lateinit var productAdapter: ProductHomeAdapter
    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root // Chỉ return giao diện ở đây, không viết thêm logic gì phía dưới
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 2. MỌI LOGIC XỬ LÝ CHUYỂN XUỐNG ĐÂY
        // Đẩy lề cho TopBar để tránh thanh trạng thái
        ViewCompat.setOnApplyWindowInsetsListener(binding.layoutTopBar) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(0, systemBars.top, 0, 0)
            insets
        }


        setupBanner()
        setupRecyclerView()
        setupViewModel()
        setupFilter()
        setupSearch()

    }

    private fun setupBanner() {
        val images = listOf(
            R.drawable.logoaquariumshop,
            R.drawable.hinhthuysinh1,
            R.drawable.hinhthuysinh2,
        )

        val adapter = BannerAdapter(images)
        binding.viewPagerBanner.adapter = adapter

        // Kết nối ViewPager2 với TabLayout (các dấu chấm)
//        TabLayoutMediator(binding.tabLayoutIndicator, binding.viewPagerBanner) { _, _ ->
//            // Không cần set text cho tab vì chỉ hiện dấu chấm
//        }.attach()

        // Thêm hiệu ứng trượt mượt mà
        binding.viewPagerBanner.clipToPadding = false
        binding.viewPagerBanner.clipChildren = false
        binding.viewPagerBanner.offscreenPageLimit = 3
        binding.viewPagerBanner.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER

        val compositePageTransformer = CompositePageTransformer()
        compositePageTransformer.addTransformer(MarginPageTransformer(40))
        compositePageTransformer.addTransformer { page, position ->
            val r = 1 - abs(position)
            page.scaleY = 0.85f + r * 0.15f
        }
        binding.viewPagerBanner.setPageTransformer(compositePageTransformer)

        // Cài đặt Auto-scroll
        sliderRunnable = Runnable {
            val currentItem = binding.viewPagerBanner.currentItem
            val totalItems = adapter.itemCount

            if (currentItem == totalItems - 1) {
                // Nếu đến ảnh cuối, quay lại ảnh đầu tiên
                binding.viewPagerBanner.currentItem = 0
            } else {
                // Chuyển sang ảnh tiếp theo
                binding.viewPagerBanner.currentItem = currentItem + 1
            }
        }

        // Lắng nghe sự kiện người dùng tương tác để tạm dừng/tiếp tục trượt
        binding.viewPagerBanner.registerOnPageChangeCallback(object : androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                sliderHandler.removeCallbacks(sliderRunnable)
                sliderHandler.postDelayed(sliderRunnable, 4000) // Trượt sau 3 giây
            }
        })
    }

    // Tạm dừng trượt khi Fragment bị ẩn đi để tiết kiệm tài nguyên
    override fun onPause() {
        super.onPause()
        sliderHandler.removeCallbacks(sliderRunnable)
    }

    // Tiếp tục trượt khi Fragment hiện lại
    override fun onResume() {
        super.onResume()
        sliderHandler.postDelayed(sliderRunnable, 4000)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // 3. DO DÙNG _binding (có dấu ? cho phép null) NÊN GÁN = NULL THOẢI MÁI
        _binding = null
    }

    private fun setupRecyclerView() {
        productAdapter = ProductHomeAdapter{clickedProductId ->
            val bundle = Bundle().apply {
                putInt("productId", clickedProductId)

            }
            findNavController().navigate(R.id.action_nav_home_to_productDetailFragment, bundle)
        }
        binding.rvAllProducts.adapter = productAdapter
        // LayoutManager đã được set là GridLayoutManager trong file XML của bạn rồi
    }

    private fun setupViewModel() {
        // 1. Khởi tạo API, Repository, Factory
        val apiService = RetrofitClient.getInstance(requireContext())
        val repository = ProductRepository(apiService)
        val factory = HomeViewModelFactory(repository)

            // 2. Lấy instance của ViewModel
        homeViewModel = ViewModelProvider(this, factory)[HomeViewModel::class.java]

        // 3. Lắng nghe dữ liệu thay đổi
        homeViewModel.products.observe(viewLifecycleOwner) { productList ->
            // Khi có dữ liệu từ mạng tải về, đẩy vào Adapter
            productAdapter.submitList(productList)
        }

        homeViewModel.errorMessage.observe(viewLifecycleOwner) { errorMsg ->
            Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_SHORT).show()
        }

        // 4. Gọi hàm fetch data từ Server
        homeViewModel.fetchProducts()
        homeViewModel.fetchUnreadCount() // BỔ SUNG DÒNG NÀY VÀO ĐÂY LÀ XONG!

        // Lắng nghe và vẽ huy hiệu (Bạn đã viết sẵn rồi, giữ nguyên)
        homeViewModel.unreadCount.observe(viewLifecycleOwner) { count ->
            if (count > 0) {
                binding.tvUnreadBadge.visibility = View.VISIBLE
                binding.tvUnreadBadge.text = count.toString()
            } else {
                binding.tvUnreadBadge.visibility = View.GONE
            }
        }

        binding.ivMessage.setOnClickListener {
            findNavController().navigate(R.id.action_nav_home_to_chatFragment)
        }
    }

    fun setupFilter(){
        binding.chipGroupFilter.setOnCheckedStateChangeListener { group, checkedId ->
            if(checkedId.isEmpty() ){
                homeViewModel.filterByCategory(-1)
                return@setOnCheckedStateChangeListener
            }

            val selectedChipId = checkedId.first()

            when(selectedChipId){
                R.id.chipAll -> homeViewModel.filterByCategory(-1)
                R.id.chipFish -> homeViewModel.filterByCategory(13)
                R.id.chipTep -> homeViewModel.filterByCategory(14)
                R.id.chipPlant -> homeViewModel.filterByCategory(15)
                R.id.chipTool -> homeViewModel.filterByCategory(16)
                R.id.vatTu -> homeViewModel.filterByCategory(17)

            }

        }

    }

    // --- XỬ LÝ TÌM KIẾM ---
    private fun setupSearch() {
        // 1. Khi ấn icon Kính lúp -> Hiện ô tìm kiếm, tự động nháy nháy con trỏ và bật bàn phím
        binding.ivSearch.setOnClickListener {
            binding.layoutSearchBar.visibility = View.VISIBLE
            binding.etSearch.requestFocus()
            showKeyboard(binding.etSearch)
        }

        // 2. Khi ấn nút Mũi tên quay lại trên thanh tìm kiếm -> Ẩn đi, tắt bàn phím
        binding.ivCloseSearch.setOnClickListener {
            binding.layoutSearchBar.visibility = View.GONE
            binding.etSearch.text.clear()
            hideKeyboard(binding.etSearch)
        }

        // 3. Bắt sự kiện khi người dùng ấn nút "Kính lúp / Enter" trên BÀN PHÍM ẢO
        binding.etSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val query = binding.etSearch.text.toString().trim()

                if (query.isNotEmpty()) {
                    // Tắt bàn phím
                    hideKeyboard(binding.etSearch)

                    // Ẩn thanh tìm kiếm ở trang chủ đi (để lỡ người dùng ấn nút Back từ trang Search về thì trang chủ nhìn vẫn gọn gàng)
                    binding.layoutSearchBar.visibility = View.GONE
                    binding.etSearch.text.clear()

                    // Gói chữ người dùng vừa nhập và chuyển sang SearchFragment
                    val bundle = Bundle().apply {
                        putString("SEARCH_QUERY", query)
                    }
                    findNavController().navigate(R.id.action_nav_home_to_searchFragment, bundle)
                }
                true // Trả về true báo hiệu là đã xử lý xong sự kiện này
            } else {
                false
            }
        }
    }

    // --- 2 Hàm tiện ích bật/tắt bàn phím ---
    private fun showKeyboard(view: View) {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun hideKeyboard(view: View) {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

}