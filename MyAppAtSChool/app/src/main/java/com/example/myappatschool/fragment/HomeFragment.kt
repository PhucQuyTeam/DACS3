package com.example.myappatschool.fragment

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myappatschool.R
import com.example.myappatschool.adapter.MyAdapter
import com.example.myappatschool.adapter.ProductAdapter
import com.example.myappatschool.model.Product

//
//class HomeFragment : Fragment() {
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        val view = inflater.inflate(R.layout.fragment_home, container, false)
//
//        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
//
////        val list = listOf("Item 1", "Item 2", "Item 3", "Item 4")
//        val list = (1..10).toList()
//
//        // lọc điều kiện < 5
//        val filteredList = list.filter { it < 5 }
//        val displayList = filteredList.map { "Item đã được chọn là Item $it" }
//
//        recyclerView.layoutManager = LinearLayoutManager(context)
//        recyclerView.adapter = MyAdapter(displayList) {
//
//            val fragment = DetailFragment()
//            val bundle = Bundle()
//            bundle.putString("data", it)
//            fragment.arguments = bundle
//
//            parentFragmentManager.beginTransaction()
//                .replace(R.id.fragment_container, fragment)
//                .commit()
//        }
//
//        return view
//    }
//}

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)

        val list = listOf(
            Product("iPhone 15", 20000000.0),
            Product("Samsung S23", 15000000.0),
            Product("Xiaomi 13", 10000000.0),
            Product("Oppo Reno", 8000000.0)
        )

        recyclerView.layoutManager = LinearLayoutManager(context)

        recyclerView.adapter = ProductAdapter(list) {

            val fragment = DetailFragment()

            val bundle = Bundle()
            bundle.putString("name", it.name)
            bundle.putDouble("price", it.price)

            fragment.arguments = bundle

            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }

        return view
    }
}