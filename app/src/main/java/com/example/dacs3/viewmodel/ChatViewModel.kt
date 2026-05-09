package com.example.dacs3.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dacs3.model.ChatDTO
import com.example.dacs3.repository.ChatRepository
import kotlinx.coroutines.launch



class ChatViewModel(private val repository: ChatRepository): ViewModel() {
    private val _chatHistory = MutableLiveData<List<ChatDTO>>()
    val chatHistory: LiveData<List<ChatDTO>> = _chatHistory

    val newMessage: LiveData<ChatDTO> = repository.newMessageLiveData

    val sentMessageResult = MutableLiveData<ChatDTO>()

    fun loadHistory(adminId: Int){
          viewModelScope.launch {
              try {
                  val response = repository.getHistory(adminId)
                  if (response.isSuccessful) {
                      _chatHistory.postValue(response.body())
                  }
              } catch (e: Exception) {
                  e.printStackTrace()
              }
          }
    }

    fun sendTextMessage(adminId: Int, message: String) {
        viewModelScope.launch {
            try {
//                repository.sendMessage(adminId, message)
                val response = repository.sendMessage(adminId, message)
                if (response.isSuccessful && response.body() != null) {
                    // Khi server báo lưu thành công, đẩy tin nhắn đó ra cho Fragment
                    sentMessageResult.postValue(response.body())
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun markMessagesAsRead(adminId: Int) {
        viewModelScope.launch {
            try {
                repository.markAsRead(adminId)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun uploadAndSendImage(adminId: Int, imagePart: okhttp3.MultipartBody.Part) {
        viewModelScope.launch {
            try {
                // 1. Gọi API Upload ảnh lên server
                val response = repository.uploadChatImage(imagePart) // Bạn nhớ thêm hàm uploadChatImage vào ChatRepository nhé

                if (response.isSuccessful && response.body() != null) {
                    val baseResponse = response.body()!!
                    if (baseResponse.success) {
                        // 2. Upload thành công! Lấy tên file do Backend trả về
                        val fileName = baseResponse.message

                        // 3. Gọi hàm sendTextMessage nhưng với Type là "image"
                        val chatResponse = repository.sendMessage(adminId, fileName, "image")
                        if (chatResponse.isSuccessful && chatResponse.body() != null) {
                            sentMessageResult.postValue(chatResponse.body())
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun startRealtimeChat(myUserId: Int, adminId: Int) {
        repository.connectWebSocket(myUserId, adminId)
    }

    override fun onCleared() {
        super.onCleared()
        repository.disconnectWebSocket()
    }


}