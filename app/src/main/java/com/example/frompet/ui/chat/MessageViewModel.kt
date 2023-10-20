package com.example.frompet.ui.chat

import android.net.Uri
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
            val currentUserId = repository.getCurrentUserId()?:return@launch
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
       fun uploadImage(uri: Uri, user: User) {
        viewModelScope.launch {
            val imageUrl = repository.uploadImage(uri)
            imageUrl?.let {
                val currentUserId = repository.getCurrentUserId()
                val currentUser = repository.getUserProfile(currentUserId!!)
                val message = ChatMessage(
                    senderId = currentUserId,
                    receiverId = user.uid,
                    senderPetName = currentUser?.petName ?: return@let,
                    message = "",
                    imageUrl = imageUrl,
                    timestamp = System.currentTimeMillis()
                )
                repository.sendImage(message)
            }
        }
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
    fun getCurrentUserId(): LiveData<String?> {
        val userId = MutableLiveData<String?>()
        viewModelScope.launch {
            userId.value = repository.getCurrentUserId()
        }
        return userId
    }

    fun getUserProfile(userId: String): LiveData<User?> {
        val userProfile = MutableLiveData<User?>()
        viewModelScope.launch {
            userProfile.value = repository.getUserProfile(userId)
        }
        return userProfile
    }


}
