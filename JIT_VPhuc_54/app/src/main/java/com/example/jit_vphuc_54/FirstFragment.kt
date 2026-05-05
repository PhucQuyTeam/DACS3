package com.example.jit_vphuc_54

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.jit_vphuc_54.databinding.FragmentFirstBinding
import com.example.jit_vphuc_54.model.Product

class FirstFragment : Fragment() {

    lateinit var binding: FragmentFirstBinding
    lateinit var adapter: AdapterProduct
    private val list = mutableListOf<Product>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.tvInfo1.text = "Home Screen"
        binding.btncheckSHH.setOnClickListener {
            val input = binding.edtSHH.text.toString()

            if (input.isEmpty()) {
                android.widget.Toast.makeText(context, "Nhập số đi!", android.widget.Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val n = input.toInt()

            if (isPerfectNumber(n)) {
                android.widget.Toast.makeText(context, "$n là số hoàn hảo", android.widget.Toast.LENGTH_LONG).show()
            } else {
                android.widget.Toast.makeText(context, "$n không phải số hoàn hảo", android.widget.Toast.LENGTH_LONG).show()
            }
        }
        binding.btnNext.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("data", "Hello from Home")

            findNavController().navigate(R.id.homeDetailFragment, bundle)
        }
    }
    private fun isPerfectNumber(n: Int): Boolean {
        if (n < 2) return false

        var sum = 1
        for (i in 2 until n) {
            if (n % i == 0) {
                sum += i
            }
        }
        return sum == n
    }
}