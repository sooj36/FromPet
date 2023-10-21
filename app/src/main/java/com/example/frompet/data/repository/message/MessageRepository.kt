package com.example.frompet.data.repository.message

import android.net.Uri
import com.example.frompet.data.model.ChatMessage
import com.example.frompet.data.model.User

interface MessageRepository {
    suspend fun sendMessage(chatRoomId: String, chatMessage: ChatMessage)

    suspend fun sendImage(chatMessage: ChatMessage)

    suspend fun goneNewMessages(chatRoomId: String)

    suspend fun loadPreviousMessages(chatRoomId: String): List<ChatMessage>

    suspend fun checkTypingStatus(receiverId: String): Boolean

    suspend fun setTypingStatus(isTyping: Boolean)
    suspend fun getCurrentUserId(): String?

    suspend fun getUserProfile(userId: String): User?

    suspend fun uploadImage(uri: Uri): String?


    fun addChatMessagesListener(chatRoomId: String, onMessagesUpdated: (List<ChatMessage>) -> Unit)

    fun addTypingStatusListener(receiverId: String, onTypingStatusUpdated: (Boolean) -> Unit)

    fun addUserProfileListener(userId: String, onProfileUpdated: (User) -> Unit)



}
