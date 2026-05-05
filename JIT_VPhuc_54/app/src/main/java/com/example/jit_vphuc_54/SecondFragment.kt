package com.example.jit_vphuc_54


import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.jit_vphuc_54.databinding.FragmentSecondBinding
class SecondFragment : Fragment() {

    lateinit var binding: FragmentSecondBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentSecondBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.tvInfo2.text = "Profile Screen"

        binding.btnNext.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("data", "Hello from Profile")

            findNavController().navigate(R.id.detailFragment, bundle)
        }
    }
}