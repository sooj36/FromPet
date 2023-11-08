package com.pet.frompet.data.repository.message

import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.util.Log
import com.pet.frompet.data.model.ChatMessage
import com.pet.frompet.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.Date
import java.util.Locale

class MessageRepositoryImpl : MessageRepository { //22-25번쨰는 싱글톤코드같은것들은 생성자에 프로퍼티로 넣어야함
    private val database = FirebaseDatabase.getInstance().reference
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private fun chatRoom(uid1: String, uid2: String): String {
        return if (uid1 > uid2) "$uid1+$uid2" else "$uid2+$uid1"
    }

    override suspend fun sendMessage(chatRoomId: String, chatMessage: ChatMessage) {
        database.child("chatMessages").child(chatRoomId).push().setValue(chatMessage)
        database.child("lastMessages").child(chatRoomId).setValue(chatMessage)
        database.child("newMessages").child(chatRoomId).child(chatMessage.receiverId).setValue(true)
    }

    override suspend fun createAndSendMessage(receiverId: String, message: String) {
        val currentUserId = getCurrentUserId() ?: return
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
        sendMessage(chatRoomId, chatMessage)
    }

    override suspend fun sendImage(chatMessage: ChatMessage) {
        val chatRoomId = chatRoom(chatMessage.senderId, chatMessage.receiverId)
        database.child("chatMessages").child(chatRoomId).push().setValue(chatMessage)
        database.child("lastMessages").child(chatRoomId).setValue(chatMessage)
        database.child("newMessages").child(chatRoomId).child(chatMessage.receiverId).setValue(true)
    }
    override suspend fun createAndSendImage(uri: Uri, user: User) {
        val imageUrl = uploadImage(uri)
        imageUrl?.let {
            val currentUserId = getCurrentUserId()
            val currentUser = getUserProfile(currentUserId!!)
            val message = ChatMessage(
                senderId = currentUserId,
                receiverId = user.uid,
                senderPetName = currentUser?.petName ?: return@let,
                message = "",
                imageUrl = imageUrl,
                timestamp = System.currentTimeMillis()
            )
            sendImage(message)
        }
    }
    override suspend fun uploadImage(uri: Uri): String? {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "IMAGE_$timestamp.png"
        val storageRef = storage.reference.child("images").child(fileName)
        storageRef.putFile(uri).await()
        val imageUrl = storageRef.downloadUrl.await().toString()
        Log.d("jun", "Image URL: $imageUrl")
        return imageUrl
    }

    override suspend fun goneNewMessages(chatRoomId: String) {
        val currentUserId = auth.currentUser?.uid ?: return
        database.child("newMessages").child(chatRoomId).child(currentUserId).setValue(false)
    }

    override suspend fun loadPreviousMessages(chatRoomId: String): List<ChatMessage> {
        return withContext(Dispatchers.Main) {
            val snapshot = database.child("chatMessages").child(chatRoomId).get().await()
            snapshot.children.mapNotNull { it.getValue(ChatMessage::class.java) }
        }
    }

    override suspend fun setTypingStatus(receiverId: String, isTyping: Boolean) {
        val currentId = auth.currentUser?.uid ?: return
        database.child("typingStatus").child(currentId).child(receiverId).setValue(isTyping)
    }

    override suspend fun checkTypingStatus(receiverId: String): Boolean {
        val currentId = auth.currentUser?.uid ?: return false
        val snapshot =
            database.child("typingStatus").child(currentId).child(receiverId).get().await()
        return snapshot.getValue(Boolean::class.java) ?: false
    }

    override suspend fun setUserChatStatus(chatRoomUid: String, isInChat: Boolean) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            val userStatusRef = database.child("chatRooms").child(chatRoomUid).child("users").child(userId).child("status")
            userStatusRef.setValue(isInChat)
        }
    }


    override suspend fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }

    override suspend fun getUserProfile(userId: String): User? {
        val document = firestore.collection("User").document(userId).get().await()
        return document.toObject(User::class.java)
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

    override fun addTypingStatusListener(
        receiverId: String,
        onTypingStatusUpdated: (Boolean) -> Unit
    ) {
        val typingStatusRef =
            database.child("typingStatus").child(receiverId).child(auth.currentUser?.uid ?: return)
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