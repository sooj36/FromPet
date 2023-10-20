package com.example.frompet.data.repository.message

import android.util.Log
import com.example.frompet.data.model.ChatMessage
import com.example.frompet.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class MessageRepositoryImpl : MessageRepository {
    private val database = FirebaseDatabase.getInstance().reference
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
 //챗메시지,라스트메시지,뉴메시지등등 나중에 상수값으로 뺼 예정
    override suspend fun sendMessage(chatRoomId: String, chatMessage: ChatMessage) {
        database.child("chatMessages").child(chatRoomId).push().setValue(chatMessage)
        database.child("lastMessages").child(chatRoomId).setValue(chatMessage)
        database.child("newMessages").child(chatRoomId).child(chatMessage.receiverId).setValue(true)
    }

    override suspend fun sendImage(chatMessage: ChatMessage) {
        val chatRoomId = chatRoom(chatMessage.senderId, chatMessage.receiverId)
        database.child("chatMessages").child(chatRoomId).push().setValue(chatMessage)
        database.child("lastMessages").child(chatRoomId).setValue(chatMessage)
        database.child("newMessages").child(chatRoomId).child(chatMessage.receiverId).setValue(true)
    }

    override suspend fun goneNewMessages(chatRoomId: String) {
        val currentUserId = auth.currentUser?.uid ?: return
        database.child("newMessages").child(chatRoomId).child(currentUserId).setValue(false)
    }

    override suspend fun loadPreviousMessages(chatRoomId: String): List<ChatMessage> {
        return withContext(Dispatchers.IO) {
            val snapshot = database.child("chatMessages").child(chatRoomId).get().await()
            snapshot.children.mapNotNull { it.getValue(ChatMessage::class.java) }
        }
    }

    override suspend fun checkTypingStatus(receiverId: String): Boolean {
        val snapshot = database.child("typingStatus").child(receiverId).get().await()
        return snapshot.getValue(Boolean::class.java) ?: false
    }

    override suspend fun setTypingStatus(isTyping: Boolean) {
        val currentId = auth.currentUser?.uid ?: return
        database.child("typingStatus").child(currentId).setValue(isTyping)
    }

    private fun chatRoom(uid1: String, uid2: String): String {
        return if (uid1 > uid2) "$uid1+$uid2" else "$uid2+$uid1"
    }

    override fun addChatMessagesListener( //파이어베이스의변화를 실시간으로 감지하고 ui에반영하기위해서 23년 권장방식이라고 봤던거같아서 ?
        chatRoomId: String,
        onMessagesUpdated: (List<ChatMessage>) -> Unit
    ) {
        val chatMessagesRef = database.child("chatMessages").child(chatRoomId)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val messages = snapshot.children.mapNotNull { it.getValue(ChatMessage::class.java) }
                onMessagesUpdated(messages)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("jun", "Error ${error.message}")
            }
        }
        chatMessagesRef.addValueEventListener(listener)
    }
    override fun addTypingStatusListener(receiverId: String, onTypingStatusUpdated: (Boolean) -> Unit) {//파이어베이스의변화를 실시간으로 감지하고 ui에반영하기위해서 23년 권장방식이라고 봤던거같아서 ?
        val typingStatusRef = database.child("typingStatus").child(receiverId)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val isTyping = snapshot.getValue(Boolean::class.java) ?: false
                onTypingStatusUpdated(isTyping)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("jun", "Error typing: ${error.message}")
            }
        }
        typingStatusRef.addValueEventListener(listener)
    }
    override fun addUserProfileListener(userId: String, onProfileUpdated: (User) -> Unit) {
        val userRef = firestore.collection("User").document(userId)
        userRef.addSnapshotListener { snapshot, error ->

            if (snapshot != null && snapshot.exists()) {
                val user = snapshot.toObject(User::class.java)
                user?.let { onProfileUpdated(it) }
            }
        }
    }


}