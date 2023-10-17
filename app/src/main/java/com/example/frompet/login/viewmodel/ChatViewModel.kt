package com.example.frompet.login.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.frompet.login.data.ChatMessage
import com.example.frompet.login.data.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import org.checkerframework.checker.builder.qual.ReturnsReceiver

class ChatViewModel : ViewModel() {
    private val _chatMessages = MutableLiveData<List<ChatMessage>>()
    val chatMessages: LiveData<List<ChatMessage>> get() = _chatMessages
    private val _isTyping =MutableLiveData<Boolean>()
    val isTyping :LiveData<Boolean> get() = _isTyping
    private val _lastMessage = MutableLiveData<ChatMessage?>()
    val lastMessage: LiveData<ChatMessage?> get() = _lastMessage
    private val database = FirebaseDatabase.getInstance().reference
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    fun chatRoom(uid1: String, uid2: String): String {
        return if (uid1 > uid2) "$uid1+$uid2" else "$uid2+$uid1" //두 사람 채팅에는 항상 합친 동일한 구분자로 생성함
    }

    fun sendMessage(receiverId: String, message: String) {
        val currentUserId = auth.currentUser?.uid ?: return
        val chatRoomId = chatRoom(currentUserId, receiverId)

        firestore.collection("User").document(currentUserId).get()
            .addOnSuccessListener { document ->
                val currentUser = document.toObject(UserModel::class.java)
                val senderPetName = currentUser?.petName ?: "Unknown"

                val chatMessage = ChatMessage(
                    senderId = currentUserId,
                    senderPetName = senderPetName,
                    receiverId = receiverId,
                    message = message,
                    timestamp = System.currentTimeMillis()
                )

                database.child("chatMessages").child(chatRoomId).push().setValue(chatMessage)
                    .addOnSuccessListener {
                        loadPreviousMessages(chatRoomId)
                    }
                    .addOnFailureListener { exception ->
                        Log.d("jun", "메시지전송실패: ${exception.message}")
                    }
            }
            .addOnFailureListener { exception ->
                Log.d("jun", "연결실패: ${exception.message}")
            }
    }


    fun loadPreviousMessages(chatRoomId: String) {
        database.child("chatMessages").child(chatRoomId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val messages = dataSnapshot.children.mapNotNull { it.getValue(ChatMessage::class.java) }
                    _chatMessages.value = messages.sortedBy { it.timestamp }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.d("jun", "불러오기실패: ${databaseError.message}")
                }
            })
    }
    fun checkTypingStatus(receiverId: String) {
        database.child("typingStatus").child(receiverId).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val status = dataSnapshot.getValue(Boolean::class.java)?:false
                _isTyping.value = status
            }
            override fun onCancelled(databaseError: DatabaseError){}
        })
    }
    fun setTypingStatus(isTyping:Boolean){
        val currentId = auth.currentUser?.uid ?: return
        database.child("typingStatus").child(currentId).setValue(isTyping)
    }
    fun loadLastMessage(userId: String) {
        database.child("chatMessages")
            .orderByChild("senderId")
            .equalTo(userId)
            .limitToLast(1)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        val message = dataSnapshot.children.first().getValue(ChatMessage::class.java)
                        _lastMessage.value = message
                    } else {
                        _lastMessage.value = null
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })
    }
}
