package com.example.test1603

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.test1603.databinding.FragmentPrime1Binding

class Prime1Fragment : Fragment() {
    private lateinit var binding: FragmentPrime1Binding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPrime1Binding.inflate(inflater, container, false)
        return  binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.btnTinh.setOnClickListener {
            val start = binding.edtStart.text.toString().toIntOrNull()
            val end = binding.edtEnd.text.toString().toIntOrNull()

            if (start == null || end == null) {
                binding.edtStart.error = "Nhập số hợp lệ"
                return@setOnClickListener
            }

            val primes = mutableListOf<Int>()

            for (i in start..end) {
                if (isPrime(i)) {
                    primes.add(i)
                }
            }

            val average = if (primes.isNotEmpty())
                primes.average()
            else 0.0

            val result = """
                Từ $start đến $end
                Số nguyên tố: $primes
                Trung bình: $average
            """.trimIndent()
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContentView, Prime2Fragment.newInstance(result))
                .addToBackStack(null).commit()
        }
    }
        private fun isPrime(n: Int): Boolean {
            if (n < 2) return false
            for (i in 2..Math.sqrt(n.toDouble()).toInt()) {
                if (n % i == 0) return false
            }
            return true
        }
    }
