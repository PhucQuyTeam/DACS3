package com.example.test1603

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.example.test1603.databinding.FragmentSv2Binding

class SecondFragment : Fragment() {

    private lateinit var binding: FragmentSv2Binding
    private var student: Student? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            student = it.getParcelable("student")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSv2Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.tvResult.text = """
            Mã SV: ${student?.masv}
            Tên SV: ${student?.tensv}
            Giới tính: ${student?.gioiTinh}
            Sở thích: ${student?.soThich}
        """.trimIndent()
    }

    companion object {
        fun newInstance(student: Student): SecondFragment {
            val fragment = SecondFragment()
            val bundle = Bundle()
            bundle.putParcelable("student", student)
            fragment.arguments = bundle
            return fragment
        }
    }
}