package com.example.frompet.data.repository.chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.frompet.data.model.ChatMessage
import com.example.frompet.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ChatRepositoryImpl : ChatRepository {
    private val database = FirebaseDatabase.getInstance().reference
    private val auth = FirebaseAuth.getInstance()
    private val _lastChats = HashMap<String, MutableLiveData<ChatMessage?>>()
    private val _newChats = MutableLiveData<HashMap<String, Boolean>>()
    private val listenersMap = HashMap<String, ValueEventListener>()

    override fun chatRoom(uid1: String, uid2: String): String {
        return if (uid1 > uid2) "$uid1+$uid2" else "$uid2+$uid1"
    }

    override fun getLastChatLiveData(chatRoomId: String): LiveData<ChatMessage?> {
        return _lastChats.getOrPut(chatRoomId) { MutableLiveData<ChatMessage?>() }
    }

    override fun loadLastChats(currentUserId: String, otherUserId: String) {
        val chatRoomId = chatRoom(currentUserId, otherUserId)

        listenersMap[chatRoomId]?.let {
            database.child("lastMessages").child(chatRoomId).removeEventListener(it)
        }

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val message = snapshot.getValue(ChatMessage::class.java)
                _lastChats[chatRoomId]?.value = message
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        }

        database.child("lastMessages").child(chatRoomId).addValueEventListener(listener)

        listenersMap[chatRoomId] = listener
    }

    override fun loadNewChats(): LiveData<HashMap<String, Boolean>> {
        val currentUserId = auth.currentUser?.uid ?: return _newChats
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
                    _newChats.value = HashMap(newMessageRooms)
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })
        return _newChats
    }

    override fun getLastTimeSorted(user: List<User>, onUpdate: (List<User>) -> Unit) {
        val currentUserId = auth.currentUser?.uid ?: return
        val chatRoomIds = user.map { user -> chatRoom(currentUserId, user.uid) }

        database.child("lastMessages").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val timeMap = mutableMapOf<String, Long>()

                chatRoomIds.forEach { chatRoomId ->
                    val message = snapshot.child(chatRoomId).getValue(ChatMessage::class.java)
                    if (message != null) {
                        timeMap[chatRoomId] = message.timestamp
                    } else {
                        database.child("matched").child(chatRoomId)
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    val matchedTime = snapshot.getValue(Long::class.java)
                                    matchedTime?.let {
                                        timeMap[chatRoomId] = it
                                    }
                                }
                                override fun onCancelled(error: DatabaseError) {}
                            })
                    }
                }

                val sortedUsers = user.sortedByDescending { user ->
                    val roomId = chatRoom(currentUserId, user.uid)
                    timeMap[roomId] ?: 0
                }
                onUpdate(sortedUsers)
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    override fun goneNewMessages(chatRoomId: String) {
        val currentUserId = auth.currentUser?.uid ?: return
        database.child("newMessages").child(chatRoomId).child(currentUserId).setValue(false)
    }

    override fun removeChatRoomData(chatRoomId: String) {
        database.child("chatMessages").child(chatRoomId).removeValue()
        database.child("lastMessages").child(chatRoomId).removeValue()
        database.child("newMessages").child(chatRoomId).removeValue()
        database.child("typingStatus").child(chatRoomId).removeValue()
    }
}
