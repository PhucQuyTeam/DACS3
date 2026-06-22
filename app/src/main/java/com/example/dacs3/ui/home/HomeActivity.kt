package com.example.dacs3.ui.home

import android.os.Bundle
import android.view.View
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
            v.setPadding(0, systemBars.top, 0, 0)
            insets
        }

        navHostFragment = supportFragmentManager.findFragmentById(binding.fragmentContainerView.id) as NavHostFragment

        navController = navHostFragment.navController


        val bottomNav = binding.bottomNavigation

        bottomNav.add(NafisBottomNavigation.Model(1, R.drawable.ic_home))
        bottomNav.add(NafisBottomNavigation.Model(2, R.drawable.ic_blog))
        bottomNav.add(NafisBottomNavigation.Model(3, R.drawable.ic_notifications))
        bottomNav.add(NafisBottomNavigation.Model(4, R.drawable.ic_profile))

        bottomNav.show(1)

        bottomNav.setOnClickMenuListener { model ->
            when (model.id) {
                1 -> navController.navigate(R.id.nav_home)
                2 -> navController.navigate(R.id.nav_blog)
                3 -> navController.navigate(R.id.nav_notifications)
                4 -> navController.navigate(R.id.nav_profile)
            }
        }

        // ẩn khi vào trang chi tiết
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.productDetailFragment) {
                binding.bottomNavigation.visibility = View.GONE
            } else {
                binding.bottomNavigation.visibility = View.VISIBLE
            }
        }

        bottomNav.setOnReselectListener {
        }
    }

}