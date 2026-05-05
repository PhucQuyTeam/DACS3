package com.example.test1603

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.test1603.databinding.FragmentPrime2Binding

class Prime2Fragment : Fragment() {
    private lateinit var binding: FragmentPrime2Binding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPrime2Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val result = arguments?.getString("result")
        binding.tvKetQua.text = result
    }
    companion object{
        fun newInstance(result: String): Prime2Fragment {
            val fragment = Prime2Fragment()
            val bundle= Bundle()
            bundle.putString("result", result)
            fragment.arguments = bundle
            return fragment
        }

    }

}