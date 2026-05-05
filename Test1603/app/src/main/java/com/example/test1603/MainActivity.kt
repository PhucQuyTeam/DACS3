package com.example.test1603

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.test1603.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContentView, Prime1Fragment())
            .commit()
    }
}