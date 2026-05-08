package com.example.dacs3.ui.order

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.dacs3.adapter.OrderPagerAdapter
import com.example.dacs3.databinding.ActivityOrderHistoryBinding
import com.google.android.material.tabs.TabLayoutMediator

class OrderHistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOrderHistoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Nút Back
        setSupportActionBar(binding.toolbarOrderHistory)
        binding.toolbarOrderHistory.setNavigationOnClickListener {
            finish()
        }

        // Nhận yêu cầu mở Tab nào từ ProfileFragment
        val targetTab = intent.getIntExtra("TARGET_TAB", 0)

        // Cài đặt ViewPager2
        val pagerAdapter = OrderPagerAdapter(this)
        binding.viewPagerOrders.adapter = pagerAdapter

        // Nối TabLayout và ViewPager2
        TabLayoutMediator(binding.tabLayoutOrders, binding.viewPagerOrders) { tab, position ->
            tab.text = when (position) {
                0 -> "Chờ xác nhận"
                1 -> "Đang giao"
                2 -> "Hoàn thành"
                else -> ""
            }
        }.attach()

        // Mở đúng Tab
        binding.viewPagerOrders.currentItem = targetTab
    }
}