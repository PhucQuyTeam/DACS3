package com.example.myappatschool.fragment

import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.myappatschool.R

class DetailFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_detail, container, false)

//        val txt = view.findViewById<TextView>(R.id.txtDetail)
//        txt.text = "$name\n${price} VND"


//        val data = arguments?.getString("data") ?: "No data"
//        txt.text = data
//
//        val name = arguments?.getString("name")
//        val price = arguments?.getDouble("price")

        val txtName = view.findViewById<TextView>(R.id.txtName)
        val txtPrice = view.findViewById<TextView>(R.id.txtPrice)

        val name = arguments?.getString("name")
        val price = arguments?.getDouble("price")

        txtName.text = name
        txtPrice.text = "$price VND"



        return view
    }
}