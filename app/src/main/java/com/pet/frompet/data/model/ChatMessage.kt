package com.pet.frompet.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ChatMessage(
    val senderId: String = "",
    val senderPetName: String = "",
    val receiverId: String = "",
    val message: String = "",
    val imageUrl: String? = null,
    val timestamp: Long = System.currentTimeMillis()
): Parcelable

//chatItem의 인스턴스를 처리할 때 각 유형에 대한 로직을 명확하게 구분가능
sealed class ChatItem {
    data class MessageItem(val chatMessage: ChatMessage) : ChatItem()
    data class DateHeader(val date: String) : ChatItem()
}


