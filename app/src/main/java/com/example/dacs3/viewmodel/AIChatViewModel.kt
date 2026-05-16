package com.example.dacs3.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.dacs3.model.AIMessageModel
import com.example.dacs3.repository.AIChatRepository
import kotlinx.coroutines.launch

class AIChatViewModel(private val repository: AIChatRepository) : ViewModel() {

    private val _messages = MutableLiveData<List<AIMessageModel>>(emptyList())
    val messages: LiveData<List<AIMessageModel>> get() = _messages

    init {
        val welcomeMsg = AIMessageModel(text = "Xin chào! Mình là trợ lý AI của PQ Aquarium Shop. Mình có thể giúp gì cho hồ cá của bạn hôm nay?", isUser = false)
        _messages.value = listOf(welcomeMsg)
    }

    fun sendMessage(userText: String) {
        val currentList = _messages.value?.toMutableList() ?: mutableListOf()

        // 1. Thêm tin nhắn của User lên màn hình
        currentList.add(AIMessageModel(text = userText, isUser = true))

        // 2. Thêm một cái bong bóng "AI đang gõ..."
        val typingMsg = AIMessageModel(text = "Đang suy nghĩ...", isUser = false, isTyping = true)
        currentList.add(typingMsg)
        _messages.value = currentList

        // 3. Gọi API hỏi Google Gemini
        viewModelScope.launch {
            try {
                val response = repository.askAI(userText)

                // Xóa bong bóng "Đang suy nghĩ..." đi
                val updatedList = _messages.value?.toMutableList() ?: mutableListOf()
                updatedList.removeAll { it.isTyping }

                if (response.isSuccessful && response.body() != null) {
                    // Thêm câu trả lời của AI vào
                    val aiReply = response.body()!!.reply
                    updatedList.add(AIMessageModel(text = aiReply, isUser = false))
                } else {
                    updatedList.add(AIMessageModel(text = "Xin lỗi, đường truyền đến não AI đang bị kẹt :(", isUser = false))
                }
                _messages.postValue(updatedList)

            } catch (e: Exception) {
                val updatedList = _messages.value?.toMutableList() ?: mutableListOf()
                updatedList.removeAll { it.isTyping }
                updatedList.add(AIMessageModel(text = "Lỗi mạng: Không thể kết nối tới AI.", isUser = false))
                _messages.postValue(updatedList)
            }
        }
    }
}

class AIChatViewModelFactory(private val repository: AIChatRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AIChatViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AIChatViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}