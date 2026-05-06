package com.example.dacs3.ui.home

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
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
    }
}