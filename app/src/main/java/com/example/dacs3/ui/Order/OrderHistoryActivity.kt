package com.example.dacs3.ui.order

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.dacs3.MainActivity
import com.example.dacs3.adapter.OrderPagerAdapter
import com.example.dacs3.databinding.ActivityOrderHistoryBinding
import com.example.dacs3.ui.home.HomeActivity
import com.example.dacs3.ui.profile.ProfileFragment
import com.google.android.material.tabs.TabLayoutMediator
import kotlin.jvm.java

class OrderHistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOrderHistoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup Nút Back
        setSupportActionBar(binding.toolbarOrderHistory)
        binding.toolbarOrderHistory.setNavigationOnClickListener {
            // Gọi thẳng về HomeActivity
            val intent = Intent(this, HomeActivity::class.java) // Đổi HomeActivity thành tên Activity chính chứa Bottom Navigation của sếp nhé

            // Gửi mật lệnh bảo nó mở tab Profile
            intent.putExtra("OPEN_FRAGMENT", "PROFILE")

            // BÙA HỦY DIỆT: Xóa sạch toàn bộ lịch sử các màn hình trước đó (Checkout, Cart...)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

            startActivity(intent)
            finish()
        }

        // Nhận yêu cầu mở Tab nào từ màn hình trước (ví dụ: ProfileFragment)
        val targetTab = intent.getIntExtra("TARGET_TAB", 0)

        // Cài đặt ViewPager2 với Adapter
        val pagerAdapter = OrderPagerAdapter(this)
        binding.viewPagerOrders.adapter = pagerAdapter

        // Nối TabLayout và ViewPager2 (0: Chờ xác nhận, 1: Đang giao, 2: Hoàn thành)
        TabLayoutMediator(binding.tabLayoutOrders, binding.viewPagerOrders) { tab, position ->
            tab.text = when (position) {
                0 -> "Chờ xác nhận"
                1 -> "Đang giao"
                2 -> "Hoàn thành"
                else -> ""
            }
        }.attach()

        // Tự động cuộn sang đúng Tab cần xem
        binding.viewPagerOrders.setCurrentItem(targetTab, false)
    }
}