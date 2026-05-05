package com.example.test1603

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.example.test1603.databinding.FragmentSv1Binding


class FirstFragment : Fragment() {

    private lateinit var binding: FragmentSv1Binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSv1Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.btnNhap.setOnClickListener {

            val masv = binding.edtMaSv.text.toString()
            val tensv = binding.edtTenSv.text.toString()

            val gender = when {
                binding.rbNam.isChecked -> "Nam"
                binding.rbNu.isChecked -> "Nữ"
                else -> "Chưa chọn"
            }

            val hobbiesList = mutableListOf<String>()
            if (binding.cbGame.isChecked) hobbiesList.add("Game")
            if (binding.cbMusic.isChecked) hobbiesList.add("Music")
            if (binding.cbSport.isChecked) hobbiesList.add("Sport")

            val hobbies = hobbiesList.joinToString(", ")

            val student = Student(masv, tensv, gender, hobbies)

            val secondFragment = SecondFragment.newInstance(student)

            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContentView, secondFragment)
                .addToBackStack(null)
                .commit()
        }
    }
}