package com.example.dacs3.ui.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dacs3.adapter.AIChatAdapter
import com.example.dacs3.databinding.FragmentAIChatBinding
import com.example.dacs3.network.RetrofitClient
import com.example.dacs3.repository.AIChatRepository
import com.example.dacs3.viewmodel.AIChatViewModel
import com.example.dacs3.viewmodel.AIChatViewModelFactory
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class AIChatFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentAIChatBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: AIChatViewModel
    private lateinit var aiChatAdapter: AIChatAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Đổi inflate cho chuẩn
        _binding = FragmentAIChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog?.setOnShowListener { dialogInterface ->
            val bottomSheetDialog = dialogInterface as BottomSheetDialog
            val bottomSheet = bottomSheetDialog.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)

            bottomSheet?.let {
                // Tính toán để chiều cao bằng 90% màn hình
                val displayMetrics = resources.displayMetrics
                val height = (displayMetrics.heightPixels * 0.9).toInt()

                // Cập nhật lại layout
                it.layoutParams.height = height
                it.requestLayout()

                val behavior = BottomSheetBehavior.from(it)
                behavior.peekHeight = height
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                behavior.skipCollapsed = true

                it.setBackgroundResource(android.R.color.transparent)
            }
        }

        setupViewModel()
        setupRecyclerView()
        setupActions()
    }

    private fun setupViewModel() {
        val apiService = RetrofitClient.getInstance(requireContext())
        val repository = AIChatRepository(apiService)
        viewModel = ViewModelProvider(this, AIChatViewModelFactory(repository))[AIChatViewModel::class.java]

        viewModel.messages.observe(viewLifecycleOwner) { messageList ->
            aiChatAdapter.submitList(messageList.toList()) {
                if (messageList.isNotEmpty()) {
                    binding.rvChatMessages.scrollToPosition(messageList.size - 1)
                }
            }
        }
    }

    private fun setupRecyclerView() {
        aiChatAdapter = AIChatAdapter()
        binding.rvChatMessages.apply {
            adapter = aiChatAdapter
            layoutManager = LinearLayoutManager(requireContext()).apply {
                stackFromEnd = true
            }
        }
    }

    private fun setupActions() {
        binding.btnClose.setOnClickListener {
            dismiss()
        }

        binding.btnSend.setOnClickListener {
            val msg = binding.etMessageInput.text.toString().trim()
            if (msg.isNotEmpty()) {
                viewModel.sendMessage(msg)
                binding.etMessageInput.text.clear()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}