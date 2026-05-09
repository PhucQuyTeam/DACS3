package com.example.dacs3.repository

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.dacs3.Token.TokenManager
import com.example.dacs3.api.ApiService
import com.example.dacs3.model.ChatDTO
import com.google.gson.Gson
import ua.naiksoftware.stomp.Stomp
import ua.naiksoftware.stomp.StompClient
import ua.naiksoftware.stomp.dto.StompHeader

class ChatRepository(private val apiService: ApiService,private val tokenManager: TokenManager)  {

    private var stompClient: StompClient? = null
    val newMessageLiveData = MutableLiveData<ChatDTO>()

    suspend fun getHistory(adminId: Int) = apiService.getChatHistory(adminId)

    suspend fun sendMessage(adminId: Int, message: String,type: String = "text"): retrofit2.Response<ChatDTO>{
        val payload = HashMap<String, Any>()
        payload["adminId"] = adminId
        payload["message"] = message
        payload["messageType"] = type

        return apiService.sendMessage(payload)

    }

    @SuppressLint("CheckResult")
    fun connectWebSocket(myUserId: Int, adminId: Int) {
        val wsUrl = "ws://10.0.2.2:8081/ws-chat/websocket"
        stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, wsUrl)

        val headers = arrayListOf<StompHeader>()

        // GIẢI PHÁP TỐI ƯU: Lấy Token MỚI NHẤT ngay tại giây phút kết nối
        val freshToken = tokenManager.getAccessToken()
        if (freshToken != null) {
            headers.add(StompHeader("Authorization", "Bearer $freshToken"))
        }

        stompClient?.connect(headers)

        stompClient?.lifecycle()?.subscribe { lifecycleEvent ->
            when (lifecycleEvent.type) {
                ua.naiksoftware.stomp.dto.LifecycleEvent.Type.OPENED -> {
                    Log.d("STOMP", "Đã kết nối WebSocket thành công với vé mới!")
                }
                ua.naiksoftware.stomp.dto.LifecycleEvent.Type.ERROR -> {
                    Log.e("STOMP", "Lỗi WebSocket: ${lifecycleEvent.exception}")
                    // Tùy chọn: Nếu lỗi do Token, có thể xử lý kết nối lại ở đây
                }
                ua.naiksoftware.stomp.dto.LifecycleEvent.Type.CLOSED -> {
                    Log.d("STOMP", "WebSocket đã đóng.")
                }
                else -> {}
            }
        }

        // 4. LẮNG NGHE KÊNH CHAT RIÊNG
        val topic = "/topic/chat/${myUserId}_${adminId}"
        stompClient?.topic(topic)?.subscribe({ topicMessage ->
            val newMsg = Gson().fromJson(topicMessage.payload, ChatDTO::class.java)
            newMessageLiveData.postValue(newMsg)
        }, { error ->
            Log.e("STOMP", "Lỗi khi subscribe topic: ${error.message}")
        })
    }

    fun disconnectWebSocket() {
        stompClient?.disconnect()
    }

    suspend fun markAsRead(adminId: Int) = apiService.markChatAsRead(adminId)

    // Hàm gọi API Upload ảnh
    suspend fun uploadChatImage(imagePart: okhttp3.MultipartBody.Part): retrofit2.Response<com.example.dacs3.model.BaseResponse> {
        return apiService.uploadChatImage(imagePart)
    }

}