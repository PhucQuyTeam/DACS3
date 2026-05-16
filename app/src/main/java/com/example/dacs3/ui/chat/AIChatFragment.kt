package com.example.dacs3.ui.chat // Đổi lại package cho đúng thư mục của bạn

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

    private var _binding: FragmentAIChatBinding? = null // Đã đổi thành FragmentAiChatBinding
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

        // ÉP POPUP MỞ CAO LÊN 90% (Giống hệt Messenger)
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
                // THỦ THUẬT: Set chiều cao lấp ló (peekHeight) bằng luôn chiều cao 90%
                behavior.peekHeight = height
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                behavior.skipCollapsed = true

                // Bắt buộc set background transparent ở đây để hiện được góc bo tròn trong XML
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
        // Nút X để tắt Popup Chat
        binding.btnClose.setOnClickListener {
            dismiss()
        }

        // Nút Gửi tin nhắn
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