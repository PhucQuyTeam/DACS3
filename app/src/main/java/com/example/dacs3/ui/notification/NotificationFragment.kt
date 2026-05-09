package com.example.dacs3.ui.notification

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dacs3.R
import com.example.dacs3.adapter.NotificationAdapter
import com.example.dacs3.network.RetrofitClient
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class NotificationFragment : Fragment() {

    private lateinit var rvNotifications: RecyclerView
    private lateinit var adapter: NotificationAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_notification, container, false)
        rvNotifications = view.findViewById(R.id.rvNotifications)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Trở về như cũ, cực kỳ đơn giản
        adapter = NotificationAdapter(emptyList())
        rvNotifications.layoutManager = LinearLayoutManager(requireContext())
        rvNotifications.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            while (isActive) {
                fetchNotificationsFromDatabase()
                delay(5000)
            }
        }
    }

    private suspend fun fetchNotificationsFromDatabase() {
        try {
            val sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
            val token = sharedPreferences.getString("TOKEN", "") ?: ""
            val authHeader = "Bearer $token"

            val response = RetrofitClient.getInstance(requireContext()).getMyNotifications(authHeader)

            if (response.isSuccessful) {
                val notiList = response.body() ?: emptyList()
                val isNewData = notiList.size > adapter.itemCount

                adapter.updateData(notiList)

                if (isNewData) {
                    rvNotifications.smoothScrollToPosition(0)
                }
            }
        } catch (e: Exception) {
            Log.e("API_TEST", "Lỗi: ${e.message}")
        }
    }
}