package com.example.dacs3.ui.home

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.dacs3.R
import com.example.dacs3.databinding.ActivityHomeBinding
import com.nafis.bottomnavigation.NafisBottomNavigation

class HomeActivity : AppCompatActivity() {
    lateinit var binding: ActivityHomeBinding
    lateinit var navController: NavController
    lateinit var navHostFragment: NavHostFragment


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            // Chỉ đẩy padding top bằng với chiều cao thanh trạng thái
            // Trái, phải, dưới giữ nguyên (0) để không làm móp méo giao diện
            v.setPadding(0, systemBars.top, 0, 0)
            insets
        }

        navHostFragment = supportFragmentManager.findFragmentById(binding.fragmentContainerView.id) as NavHostFragment

        navController = navHostFragment.navController

//        binding.bottomNavigationView.setupWithNavController(navController)

        // 2. CẤU HÌNH THANH ĐIỀU HƯỚNG NAFIS
        // ==========================================
        val bottomNav = binding.bottomNavigation

        // Thêm các nút bấm (Tab) vào thanh bottom
        // Số 1, 2, 3, 4 là ID của nút. Kế bên là Icon svg tương ứng của bạn.
        bottomNav.add(NafisBottomNavigation.Model(1, R.drawable.ic_home))
        bottomNav.add(NafisBottomNavigation.Model(2, R.drawable.ic_blog))
        bottomNav.add(NafisBottomNavigation.Model(3, R.drawable.ic_notifications))
        bottomNav.add(NafisBottomNavigation.Model(4, R.drawable.ic_profile))

        // Mặc định khi vừa mở app lên sẽ chọn sẵn Tab Home (ID số 1)
        bottomNav.show(1)

        // Xử lý hiệu ứng: Bấm vào nút nào thì gọi lệnh chuyển tới Fragment đó
        bottomNav.setOnClickMenuListener { model ->
            when (model.id) {
                1 -> navController.navigate(R.id.nav_home)
                2 -> navController.navigate(R.id.nav_blog)
                3 -> navController.navigate(R.id.nav_notifications)
                4 -> navController.navigate(R.id.nav_profile)
            }
        }

        // (Tùy chọn) Chặn sự kiện click đúp: Nếu đang ở tab Home mà bấm Home thêm lần nữa thì không làm gì cả
        bottomNav.setOnReselectListener {
            // Để trống để tránh app load lại trang hiện tại
        }
    }

}