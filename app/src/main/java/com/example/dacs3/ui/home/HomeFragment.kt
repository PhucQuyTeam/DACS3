package com.example.dacs3.ui.home

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
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
import com.example.dacs3.databinding.FragmentHomeBinding
import com.example.dacs3.network.RetrofitClient
import com.example.dacs3.repository.ProductRepository
import com.example.dacs3.viewmodel.HomeViewModel
import com.example.dacs3.viewmodel.HomeViewModelFactory
import kotlin.math.abs

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val sliderHandler = Handler(Looper.getMainLooper())
    private lateinit var sliderRunnable: Runnable


    private lateinit var productAdapter: ProductHomeAdapter
    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupBanner()
        setupRecyclerView()
        setupViewModel()
        setupFilter()
        setupSearch()
        setupFabAIChat()
        binding.ivCart.setOnClickListener {
            findNavController().navigate(R.id.action_nav_home_to_cartActivity)
        }
    }

    private fun setupBanner() {
        val images = listOf(
            R.drawable.hinhthuysinh3,
            R.drawable.hinhthuysinh1,
            R.drawable.hinhthuysinh2,
        )

        val adapter = BannerAdapter(images)
        binding.viewPagerBanner.adapter = adapter

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

        sliderRunnable = Runnable {
            val currentItem = binding.viewPagerBanner.currentItem
            val totalItems = adapter.itemCount

            if (currentItem == totalItems - 1) {
                binding.viewPagerBanner.currentItem = 0
            } else {
                binding.viewPagerBanner.currentItem = currentItem + 1
            }
        }

        binding.viewPagerBanner.registerOnPageChangeCallback(object : androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                sliderHandler.removeCallbacks(sliderRunnable)
                sliderHandler.postDelayed(sliderRunnable, 4000)
            }
        })
    }

    override fun onPause() {
        super.onPause()
        sliderHandler.removeCallbacks(sliderRunnable)
    }

    override fun onResume() {
        super.onResume()
        sliderHandler.postDelayed(sliderRunnable, 4000)
    }

    override fun onDestroyView() {
        super.onDestroyView()
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
    }

    private fun setupViewModel() {
        val apiService = RetrofitClient.getInstance(requireContext())
        val repository = ProductRepository(apiService)
        val factory = HomeViewModelFactory(repository)

        homeViewModel = ViewModelProvider(this, factory)[HomeViewModel::class.java]

        homeViewModel.products.observe(viewLifecycleOwner) { productList ->
            productAdapter.submitList(productList)
        }

        homeViewModel.errorMessage.observe(viewLifecycleOwner) { errorMsg ->
            Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_SHORT).show()
        }

        homeViewModel.fetchProducts()
        homeViewModel.fetchUnreadCount()

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

    private fun setupSearch() {
        binding.ivSearch.setOnClickListener {
            binding.layoutSearchBar.visibility = View.VISIBLE
            binding.etSearch.requestFocus()
            showKeyboard(binding.etSearch)
        }


        binding.ivCloseSearch.setOnClickListener {
            binding.layoutSearchBar.visibility = View.GONE
            binding.etSearch.text.clear()
            hideKeyboard(binding.etSearch)
        }

        binding.etSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val query = binding.etSearch.text.toString().trim()

                if (query.isNotEmpty()) {
                    hideKeyboard(binding.etSearch)

                    binding.layoutSearchBar.visibility = View.GONE
                    binding.etSearch.text.clear()


                    val bundle = Bundle().apply {
                        putString("SEARCH_QUERY", query)
                    }
                    findNavController().navigate(R.id.action_nav_home_to_searchFragment, bundle)
                }
                true
            } else {
                false
            }
        }
    }

    private fun showKeyboard(view: View) {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun hideKeyboard(view: View) {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupFabAIChat() {
        var dX = 0f
        var dY = 0f
        var initialTouchX = 0f
        var initialTouchY = 0f

        binding.fabAIChat.setOnTouchListener { view, event ->
            when (event.actionMasked) {

                MotionEvent.ACTION_DOWN -> {
                    dX = view.x - event.rawX
                    dY = view.y - event.rawY
                    initialTouchX = event.rawX
                    initialTouchY = event.rawY
                    true
                }

                MotionEvent.ACTION_MOVE -> {
                    val newX = event.rawX + dX
                    val newY = event.rawY + dY

                    val parent = view.parent as View
                    val maxX = parent.width - view.width.toFloat()
                    val maxY = parent.height - view.height.toFloat()


                    view.animate()
                        .x(newX.coerceIn(0f, maxX))
                        .y(newY.coerceIn(0f, maxY))
                        .setDuration(0)
                        .start()
                    true
                }


                MotionEvent.ACTION_UP -> {
                    val diffX = Math.abs(event.rawX - initialTouchX)
                    val diffY = Math.abs(event.rawY - initialTouchY)


                    if (diffX < 10 && diffY < 10) {
                        findNavController().navigate(R.id.action_nav_home_to_AIChatFragment)
                    }
                    true
                }
                else -> false
            }
        }
    }

}