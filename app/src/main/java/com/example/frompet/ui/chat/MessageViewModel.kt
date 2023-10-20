package com.example.frompet.ui.chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.frompet.data.model.ChatMessage
import com.example.frompet.data.model.User
import com.example.frompet.data.repository.message.MessageRepository
import com.example.frompet.data.repository.message.MessageRepositoryImpl
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class MessageViewModel : ViewModel() {
    private val repository: MessageRepository = MessageRepositoryImpl()
    private val _chatMessages = MutableLiveData<List<ChatMessage>>()
    val chatMessages: LiveData<List<ChatMessage>> get() = _chatMessages

    private val _isTyping = MutableLiveData<Boolean>()
    val isTyping: LiveData<Boolean> get() = _isTyping

    private val _userProfile = MutableLiveData<User>()
    val userProfile: LiveData<User> get() = _userProfile

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    fun chatRoom(uid1: String, uid2: String): String {
        return if (uid1 > uid2) "$uid1+$uid2" else "$uid2+$uid1"
    }

    fun sendMessage(receiverId: String, message: String) {
        viewModelScope.launch {
            val currentUserId = auth.currentUser?.uid ?: return@launch
            val chatRoomId = chatRoom(currentUserId, receiverId)

            val document = firestore.collection("User").document(currentUserId).get().await()
            val currentUser = document.toObject(User::class.java)
            val senderPetName = currentUser?.petName ?: "오류"

            val chatMessage = ChatMessage(
                senderId = currentUserId,
                senderPetName = senderPetName,
                receiverId = receiverId,
                message = message,
                timestamp = System.currentTimeMillis()
            )
            repository.sendMessage(chatRoomId, chatMessage)
        }
    }

    fun sendImage(chatMessage: ChatMessage) = viewModelScope.launch {
        repository.sendImage(chatMessage)
    }

    fun goneNewMessages(chatRoomId: String) = viewModelScope.launch {
        repository.goneNewMessages(chatRoomId)
    }

    fun loadPreviousMessages(chatRoomId: String) {
        viewModelScope.launch {
            val messages = repository.loadPreviousMessages(chatRoomId)
            _chatMessages.postValue(messages)
        }
    }

    fun checkTypingStatus(receiverId: String) = viewModelScope.launch {
        val isTyping = repository.checkTypingStatus(receiverId)
        _isTyping.postValue(isTyping)
    }

    fun setTypingStatus(isTyping: Boolean) = viewModelScope.launch {
        repository.setTypingStatus(isTyping)
    }
    fun observeChatMessages(chatRoomId: String) {
        repository.addChatMessagesListener(chatRoomId) { messages ->
            _chatMessages.postValue(messages)
        }
    }
    fun observeTypingStatus(receiverId: String) {
        repository.addTypingStatusListener(receiverId) { isTyping ->
            _isTyping.postValue(isTyping)
        }
    }
    fun observeUserProfile(userId: String) {
        repository.addUserProfileListener(userId) { user ->
            _userProfile.postValue(user)
        }
    }

}
