package com.example.frompet.ui.chat.viewmodel

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
    private val repository: MessageRepository = MessageRepositoryImpl()//뷰모델안에서는 구현체가 누군지몰라도 된다 생성자를 뷰모델팩토리로 넘겨야한다(레파지토리를넣어야함)
    private val _chatMessages = MutableLiveData<List<ChatMessage>>()
    val chatMessages: LiveData<List<ChatMessage>> get() = _chatMessages

    private val _isTyping = MutableLiveData<Boolean>()
    val isTyping: LiveData<Boolean> get() = _isTyping

    private val _userProfile = MutableLiveData<User>()
    val userProfile: LiveData<User> get() = _userProfile


    fun chatRoom(uid1: String, uid2: String): String {
        return if (uid1 > uid2) "$uid1+$uid2" else "$uid2+$uid1"
    }

    fun sendMessage(receiverId: String, message: String) {
        viewModelScope.launch {
            repository.createAndSendMessage(receiverId, message)
        }
    }
    fun uploadImage(uri: Uri, user: User) {
        viewModelScope.launch {
            repository.createAndSendImage(uri, user)
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

    fun setTypingStatus(receiverId: String, isTyping: Boolean) = viewModelScope.launch {
        repository.setTypingStatus(receiverId, isTyping)
    }

    fun updateUserChatStatus(chatRoomUid: String, isInChat: Boolean) = viewModelScope.launch {
        repository.setUserChatStatus(chatRoomUid, isInChat)
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
