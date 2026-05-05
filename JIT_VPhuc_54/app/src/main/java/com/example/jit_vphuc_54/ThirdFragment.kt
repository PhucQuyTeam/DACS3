package com.example.jit_vphuc_54


import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.jit_vphuc_54.databinding.FragmentThirdBinding

class ThirdFragment : Fragment() {

    lateinit var binding: FragmentThirdBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentThirdBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.tvInfo3.text = "Settings Screen"
        binding.btnBack.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("data", "Hello from Settings")

            findNavController().navigate(R.id.detailFragment, bundle)
        }
    }
}