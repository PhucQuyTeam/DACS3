package com.example.dacs3.ui.home

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.example.dacs3.R
import com.example.dacs3.adapter.ProductImageSliderAdapter
import com.example.dacs3.adapter.ReviewAdapter
import com.example.dacs3.databinding.FragmentProductDetailBinding
import com.example.dacs3.network.RetrofitClient
import com.example.dacs3.repository.ProductDetailRepository
import com.example.dacs3.viewmodel.ProductDetailViewModel
import com.example.dacs3.viewmodel.ProductDetailViewModelFactory
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.abs

class ProductDetailFragment : Fragment() {
    private var _binding: FragmentProductDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewmodel: ProductDetailViewModel
    private lateinit var reviewAdapter: ReviewAdapter

    // Biến lưu trữ dữ liệu
    private var productId: Int = -1
    private var maxStock: Int = 1
    private var currentQuantity: Int = 1

    // Biến cho auto-scroll Slider ảnh
    private val sliderHandler = Handler(Looper.getMainLooper())
    private lateinit var sliderRunnable: Runnable


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProductDetailBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        productId = arguments?.getInt("productId") ?: -1

        if(productId != -1){
            setupViewModel()
            setupRecyclerView()
            setupObservers()
            setupTopBar()
            setupBottomActionBar()

            viewmodel.fetchProductData(productId)
        }else{
            Toast.makeText(requireContext(), "Không tìm thấy sản phẩm", Toast.LENGTH_SHORT).show()

        }
    }

    private fun setupViewModel(){
        val apiService = RetrofitClient.getInstance(requireContext())
        val repository = ProductDetailRepository(apiService)
        val factory = ProductDetailViewModelFactory(repository)
        viewmodel = ViewModelProvider(this, factory).get(ProductDetailViewModel::class.java)
    }

    private fun setupRecyclerView(){
        reviewAdapter = ReviewAdapter()

        binding.layoutReviews.rvPreviewReviews.apply{
            adapter = reviewAdapter
            layoutManager = LinearLayoutManager(requireContext())
            isNestedScrollingEnabled = false
        }

        binding.layoutReviews.btnViewAllReviews.setOnClickListener {
            Toast.makeText(requireContext(), "Chuyển sang màn hình Tất cả Đánh giá", Toast.LENGTH_SHORT).show()
            // Thực hiện chuyển Fragment tại đây và truyền productId đi
        }
    }

    private fun setupObservers(){
        viewmodel.productDetail.observe(viewLifecycleOwner) { product ->
            product?.let {
                maxStock = it.quantity // Cập nhật giới hạn tồn kho

                // 1. Gắn Slider Hình Ảnh Sản Phẩm
                if (!it.imgages.isNullOrEmpty()) {
                    setupImageSlider(it.imgages) // imgages (sai chính tả do API cũ của bạn)
                }

                val format = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))
                binding.layoutInfo.tvPrice.text = format.format(it.price)

                binding.layoutInfo.tvSold.text = "Đã bán ${it.total_ProductQuantity}"
                binding.layoutInfo.tvProductName.text = it.name
                binding.layoutInfo.chipCategory.text = it.categorieName

                // Xử lý hiển thị Tình trạng kho hàng
                if (it.quantity > 0) {
                    binding.layoutInfo.tvStatus.text = "Tình trạng: Còn hàng (${it.quantity})"
                    binding.layoutInfo.tvStatus.setTextColor(android.graphics.Color.parseColor("#1976D2")) // Xanh dương
                } else {
                    binding.layoutInfo.tvStatus.text = "Tình trạng: Hết hàng"
                    binding.layoutInfo.tvStatus.setTextColor(android.graphics.Color.parseColor("#D32F2F")) // Đỏ
                }

                // 3. Cập nhật Mô tả (layout_detail_description)
                binding.layoutDescription.tvDescription.text = it.description

                // Cập nhật Header thống kê Đánh giá
                binding.layoutReviews.tvAvgRating.text = "⭐ ${it.averageRating}/5"
                binding.layoutReviews.tvTotalReviews.text = "(${it.totalReviews} đánh giá)"
            }
        }

        viewmodel.review.observe(viewLifecycleOwner) { reviews ->
            reviews?.let{
                val previewList = it.filterNotNull().take(3)
                reviewAdapter.submitList(previewList)

                if(it.isEmpty()){
                    binding.layoutReviews.btnViewAllReviews.visibility = View.GONE
                }else{
                    binding.layoutReviews.btnViewAllReviews.visibility = View.VISIBLE
                }

            }
        }

        viewmodel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
        }

    }

    private fun setupTopBar() {
        // 1. Xử lý nút Back (Quay lại trang chủ)
        binding.layoutInfo.btnBack.setOnClickListener {
            // Lệnh này giúp Fragment tự động lùi lại 1 trang giống như khi ấn nút Back trên điện thoại
            findNavController().popBackStack()
        }

        // 2. Xử lý nút Giỏ hàng
        binding.layoutInfo.btnCart.setOnClickListener {
            Toast.makeText(requireContext(), "Chuyển sang màn hình Giỏ hàng", Toast.LENGTH_SHORT).show()

            // TODO: Mở Fragment Giỏ Hàng tại đây
            // Ví dụ: findNavController().navigate(R.id.action_productDetailFragment_to_cartFragment)
        }
    }

    private fun setupBottomActionBar(){
        binding.btnDecreaseQty.setOnClickListener {
            if(currentQuantity > 1){
                currentQuantity--
                binding.tvCurrentQuantity.text = currentQuantity.toString()

            }

        }

        binding.btnIncreaseQty.setOnClickListener {
            if(currentQuantity < maxStock){
                currentQuantity++
                binding.tvCurrentQuantity.text = currentQuantity.toString()
            }else {
                Toast.makeText(requireContext(), "Trong kho chỉ còn $maxStock sản phẩm", Toast.LENGTH_SHORT).show()
            }

        }

        binding.btnAddToCart.setOnClickListener {
            Toast.makeText(requireContext(), "Đã thêm $currentQuantity sản phẩm vào giỏ", Toast.LENGTH_SHORT).show()
        }

        binding.btnBuyNow.setOnClickListener {
            Toast.makeText(requireContext(), "Chuyển sang màn hình Thanh toán", Toast.LENGTH_SHORT).show()
        }


    }

    private fun setupImageSlider(imageUrls: List<String>) {
        // Giả sử ViewPager2 của bạn nằm trong layoutInfo có id là viewPagerProductImages
        val viewPager = binding.layoutInfo.viewPagerProductImages
        val adapter = ProductImageSliderAdapter(imageUrls)
        viewPager.adapter = adapter

        // Hiệu ứng giống Home
        viewPager.clipToPadding = false
        viewPager.clipChildren = false
        viewPager.offscreenPageLimit = 3
        viewPager.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER

        val compositePageTransformer = CompositePageTransformer()
        compositePageTransformer.addTransformer(MarginPageTransformer(40))
        compositePageTransformer.addTransformer { page, position ->
            val r = 1 - abs(position)
            page.scaleY = 0.85f + r * 0.15f
        }
        viewPager.setPageTransformer(compositePageTransformer)

        // Auto-scroll
        sliderRunnable = Runnable {
            val currentItem = viewPager.currentItem
            val totalItems = adapter.itemCount
            if (currentItem == totalItems - 1) viewPager.currentItem = 0
            else viewPager.currentItem = currentItem + 1
        }

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                sliderHandler.removeCallbacks(sliderRunnable)
                sliderHandler.postDelayed(sliderRunnable, 3000) // Đổi ảnh sau 3s
            }
        })
    }

    override fun onResume() {
        super.onResume()
        // Khi mở Detail -> Ẩn Nav
        // Thay "R.id.bottomNavigationView" bằng ID thật trong MainActivity của bạn
        requireActivity().findViewById<View>(R.id.bottomNavigation)?.visibility = View.GONE

        // Tiếp tục chạy slider ảnh nếu có
        if (::sliderRunnable.isInitialized) sliderHandler.postDelayed(sliderRunnable, 3000)
    }

    override fun onPause() {
        super.onPause()
        // Khi rời khỏi Detail (Back lại Home) -> Hiện Nav
        requireActivity().findViewById<View>(R.id.bottomNavigation)?.visibility = View.VISIBLE

        // Dừng slider ảnh
        if (::sliderRunnable.isInitialized) sliderHandler.removeCallbacks(sliderRunnable)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Dọn dẹp ViewBinding để chống tràn bộ nhớ
        sliderHandler.removeCallbacks(sliderRunnable)
    }


}