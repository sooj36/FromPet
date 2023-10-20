package com.example.frompet.data.model

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

