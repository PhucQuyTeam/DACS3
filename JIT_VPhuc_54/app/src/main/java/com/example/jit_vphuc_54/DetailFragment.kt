package com.example.jit_vphuc_54

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.jit_vphuc_54.databinding.FragmentDetailBinding
class DetailFragment : Fragment() {

    lateinit var binding: FragmentDetailBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val data = arguments?.getString("data") ?: "No Data"

        binding.tvDetail.text = data

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }
}