package com.example.dacs3.ui.order

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dacs3.databinding.FragmentOrderListBinding
import com.example.dacs3.network.RetrofitClient
import kotlinx.coroutines.launch

class OrderListFragment : Fragment() {

    private var _binding: FragmentOrderListBinding? = null
    private val binding get() = _binding!!
    private var tabStatus: Int = 0

    companion object {
        fun newInstance(status: Int): OrderListFragment {
            val fragment = OrderListFragment()
            val args = Bundle()
            args.putInt("status", status)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { tabStatus = it.getInt("status") }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOrderListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvOrders.layoutManager = LinearLayoutManager(requireContext())
        fetchOrders()
    }

    private fun fetchOrders() {
        binding.progressBarOrders.visibility = View.VISIBLE
        binding.rvOrders.visibility = View.GONE
        binding.tvEmptyOrders.visibility = View.GONE

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                // Chú ý: Đổi getMyOrders thành tên hàm trong ApiService của bạn
                val response = RetrofitClient.getInstance(requireContext()).getMyOrders(tabStatus)

                if (response.isSuccessful && response.body() != null) {
                    val orderList = response.body()!!
                    if (orderList.isEmpty()) {
                        binding.tvEmptyOrders.visibility = View.VISIBLE
                    } else {
                        val adapter = OrderHistoryAdapter(orderList)
                        binding.rvOrders.adapter = adapter
                        binding.rvOrders.visibility = View.VISIBLE
                    }
                } else {
                    Toast.makeText(requireContext(), "Lỗi tải dữ liệu", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                binding.tvEmptyOrders.text = "Lỗi kết nối mạng"
                binding.tvEmptyOrders.visibility = View.VISIBLE
            } finally {
                binding.progressBarOrders.visibility = View.GONE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}