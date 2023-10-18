package com.example.frompet.chating

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
import com.google.firebase.firestore.auth.User

class ChatViewModel : ViewModel() {
    private val _chatMessages = MutableLiveData<List<ChatMessage>>()
    val chatMessages: LiveData<List<ChatMessage>> get() = _chatMessages
    private val _isTyping = MutableLiveData<Boolean>()
    val isTyping: LiveData<Boolean> get() = _isTyping
    private val _lastMessages = HashMap<String, MutableLiveData<ChatMessage?>>()
    private val _newMessages = MutableLiveData<HashMap<String, Boolean>>()
    val newMessages: LiveData<HashMap<String, Boolean>> get() = _newMessages


    private val database = FirebaseDatabase.getInstance().reference
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    fun chatRoom(uid1: String, uid2: String): String {
        return if (uid1 > uid2) "$uid1+$uid2" else "$uid2+$uid1" //두 사람 채팅에는 항상 합친 동일한 구분자로 생성함
    }

    fun getLastMessageLiveData(chatRoomId: String): LiveData<ChatMessage?> {
        return _lastMessages.getOrPut(chatRoomId) { MutableLiveData<ChatMessage?>() }
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
                database.child("lastMessages").child(chatRoomId).setValue(chatMessage)
                database.child("newMessages").child(chatRoomId).child(receiverId).setValue(true)
                    .addOnSuccessListener {
                        loadPreviousMessages(chatRoomId)
                    }.addOnFailureListener { exception ->
                        Log.d("jun", "메시지전송실패: ${exception.message}")
                    }
            }.addOnFailureListener { exception ->
                Log.d("jun", "연결실패: ${exception.message}")
            }
    }
    fun sendImage(chatMessage: ChatMessage) {
        val chatRoomId = chatRoom(chatMessage.senderId, chatMessage.receiverId)

        database.child("chatMessages").child(chatRoomId).push().setValue(chatMessage)
        database.child("lastMessages").child(chatRoomId).setValue(chatMessage)
        database.child("newMessages").child(chatRoomId).child(chatMessage.receiverId).setValue(true)
    }


    fun loadPreviousMessages(chatRoomId: String) {
        database.child("chatMessages").child(chatRoomId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val messages =
                        snapshot.children.mapNotNull { it.getValue(ChatMessage::class.java) }
                    _chatMessages.value = messages.sortedBy { it.timestamp }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.d("jun", "불러오기실패: ${databaseError.message}")
                }
            })
    }

    fun loadLastMessage(currentUserId: String, otherUserId: String) { //  리얼타임 베이스의 구조를 최적화하여 필요한 데이터만 읽으려고 라스트메시지 노드 따로 추가함
        val chatRoomId = chatRoom(currentUserId, otherUserId)

        database.child("lastMessages").child(chatRoomId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val message = snapshot.getValue(ChatMessage::class.java)
                    _lastMessages[chatRoomId]?.value = message
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })
    }

    fun goneNewMessages(chatRoomId: String) {
        val currentUserId = auth.currentUser?.uid ?: return
        database.child("newMessages").child(chatRoomId).child(currentUserId).setValue(false)
    }

    fun loadNewMessages() {
        val currentUserId = auth.currentUser?.uid ?: return
        database.child("newMessages")
            .orderByChild(currentUserId)
            .equalTo(true)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val newMessageRooms = snapshot.children.mapNotNull {
                        it.key?.let { key ->
                            key to (it.child(currentUserId).getValue(Boolean::class.java) ?: false)
                        }
                    }.toMap()
                    _newMessages.value = HashMap(newMessageRooms)
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })
    }

    fun getlastTimeSorted(user: List<UserModel>, onUpdate: (List<UserModel>) -> Unit) {
        val currentUserId = auth.currentUser?.uid ?: return
        val chatRoomIds = user.map { user -> chatRoom(currentUserId, user.uid) }

        database.child("lastMessages").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val lastMessagesMap = mutableMapOf<String, Long>()

                chatRoomIds.forEach { chatRoomId ->
                    val message = snapshot.child(chatRoomId).getValue(ChatMessage::class.java)
                    lastMessagesMap[chatRoomId] = message?.timestamp ?: 0
                }

                val sortedUsers = user.sortedByDescending { user ->
                    val roomId = chatRoom(currentUserId, user.uid)
                    lastMessagesMap[roomId] ?: 0
                }
                onUpdate(sortedUsers)
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }


    fun checkTypingStatus(receiverId: String) {
        database.child("typingStatus").child(receiverId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val status = snapshot.getValue(Boolean::class.java) ?: false
                    _isTyping.value = status
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })
    }

    fun setTypingStatus(isTyping: Boolean) {
        val currentId = auth.currentUser?.uid ?: return
        database.child("typingStatus").child(currentId).setValue(isTyping)
    }
}
