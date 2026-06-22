package com.example.dacs3.ui.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dacs3.Token.TokenManager
import com.example.dacs3.adapter.ChatAdapter
import com.example.dacs3.databinding.FragmentChatBinding
import com.example.dacs3.network.RetrofitClient
import com.example.dacs3.repository.ChatRepository
import com.example.dacs3.viewmodel.ChatViewModel
import com.example.dacs3.viewmodel.ChatViewModelFactory
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream


class chatFragment : Fragment() {

    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: ChatViewModel
    private lateinit var chatAdapter: ChatAdapter

    private val ADMIN_ID = 42
    private var myUserId = -1

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()
        setupRecyclerView()
        setupActions()
        observeViewModel()

        val tokenManager = TokenManager(requireContext())
        myUserId = tokenManager.getUserId()


        viewModel.loadHistory(ADMIN_ID)
        viewModel.startRealtimeChat(myUserId, ADMIN_ID)

        viewModel.markMessagesAsRead(ADMIN_ID)
    }

    private fun setupViewModel() {

        val tokenManager = TokenManager(requireContext())
        val apiService = RetrofitClient.getInstance(requireContext())

        val repository = ChatRepository(apiService, tokenManager)

        viewModel = ViewModelProvider(this, ChatViewModelFactory(repository))[ChatViewModel::class.java]
    }

    private fun setupRecyclerView() {
        chatAdapter = ChatAdapter(ADMIN_ID)
        binding.rvChatMessages.apply {
            adapter = chatAdapter
            layoutManager = LinearLayoutManager(requireContext()).apply {
                stackFromEnd = true
            }
        }
    }

    private fun setupActions() {
        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.btnSend.setOnClickListener {
            val msg = binding.etMessageInput.text.toString().trim()
            if (msg.isNotEmpty()) {
                viewModel.sendTextMessage(ADMIN_ID, msg)
                binding.etMessageInput.text.clear()
            }
        }

        binding.btnAddMedia.setOnClickListener {
            binding.btnAddMedia.setOnClickListener {
                pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }
        }
    }

    private fun observeViewModel() {
        // 1. Nhận lịch sử chat (Giữ nguyên)
        viewModel.chatHistory.observe(viewLifecycleOwner) { history ->
            chatAdapter.submitList(history.toMutableList())
            // Cuộn xuống dòng cuối cùng
            if (history.isNotEmpty()) {
                binding.rvChatMessages.scrollToPosition(history.size - 1)
            }
        }

        // 2. NGAY KHI BẠN GỬI TIN NHẮN THÀNH CÔNG -> CẬP NHẬT MÀN HÌNH LUÔN
        viewModel.sentMessageResult.observe(viewLifecycleOwner) { newlySentMsg ->
            val currentList = chatAdapter.currentList.toMutableList()
            // Tránh thêm trùng nếu WebSocket đã nhanh tay thêm rồi
            if (!currentList.any { it.id == newlySentMsg.id }) {
                currentList.add(newlySentMsg)
                chatAdapter.submitList(currentList) {
                    binding.rvChatMessages.scrollToPosition(currentList.size - 1)
                }
            }
        }

        // 3. Nhận tin nhắn Real-time từ Shop gửi đến qua WebSocket (Giữ nguyên)
        viewModel.newMessage.observe(viewLifecycleOwner) { newMsg ->
            if (!chatAdapter.currentList.any { it.id == newMsg.id }) {
                val currentList = chatAdapter.currentList.toMutableList()
                currentList.add(newMsg)
                chatAdapter.submitList(currentList) {
                    binding.rvChatMessages.scrollToPosition(currentList.size - 1)
                }
            }
        }
    }

    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            val contentResolver = requireContext().contentResolver
            val inputStream = contentResolver.openInputStream(uri)

            val tempFile = File(requireContext().cacheDir, "temp_chat_image.jpg")
            val outputStream = FileOutputStream(tempFile)
            inputStream?.copyTo(outputStream)

            val requestFile = tempFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("image", tempFile.name, requestFile)

            viewModel.uploadAndSendImage(ADMIN_ID, body)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}