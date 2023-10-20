package com.example.frompet.data.repository.chat

import androidx.lifecycle.LiveData
import com.example.frompet.data.model.ChatMessage
import com.example.frompet.data.model.User

interface ChatRepository {
    fun chatRoom(uid1: String, uid2: String): String

    fun getLastChatLiveData(chatRoomId: String): LiveData<ChatMessage?>

    fun loadLastChats(currentUserId: String, otherUserId: String)

    fun goneNewMessages(chatRoomId: String)

    fun loadNewChats(): LiveData<HashMap<String, Boolean>>

    fun getLastTimeSorted(user: List<User>, onUpdate: (List<User>) -> Unit)

}
