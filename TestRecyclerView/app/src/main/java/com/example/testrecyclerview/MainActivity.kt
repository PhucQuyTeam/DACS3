package com.example.testrecyclerview

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.testrecyclerview.Adapter.ProductAdapter
import com.example.testrecyclerview.model.Product

class MainActivity : AppCompatActivity() {

    private lateinit var adapter: ProductAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)

        // dữ liệu giả
        val products = listOf(
            Product(1, "Áo thun nam", 120000.0, R.drawable.ao),
            Product(2, "Giày sneaker", 350000.0, R.drawable.giay),
            Product(3, "Balo thời trang", 220000.0, R.drawable.balo),
            Product(4, "Tai nghe Bluetooth", 500000.0, R.drawable.tainghe),

            Product(3, "Balo thời trang", 220000.0, R.drawable.balo),
            Product(4, "Tai nghe Bluetooth", 500000.0, R.drawable.tainghe),
            Product(1, "Áo thun nam", 120000.0, R.drawable.ao),
            Product(2, "Giày sneaker", 350000.0, R.drawable.giay),
            Product(3, "Balo thời trang", 220000.0, R.drawable.balo),
            Product(4, "Tai nghe Bluetooth", 500000.0, R.drawable.tainghe),

            Product(1, "Áo thun nam", 120000.0, R.drawable.ao),
            Product(2, "Giày sneaker", 350000.0, R.drawable.giay),
            Product(3, "Balo thời trang", 220000.0, R.drawable.balo),
            Product(4, "Tai nghe Bluetooth", 500000.0, R.drawable.tainghe)
        )

        adapter = ProductAdapter(products) { product ->
            Toast.makeText(this, "Click: ${product.name}", Toast.LENGTH_SHORT).show()
        }

        // dạng lưới giống Shopee
        recyclerView.layoutManager = GridLayoutManager(this, 2)

        recyclerView.adapter = adapter
    }
}