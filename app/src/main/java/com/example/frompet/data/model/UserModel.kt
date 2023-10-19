package com.example.frompet.data.model

import android.net.Uri
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserModel(
    val petAge: Int = 0,
    val petDescription: String = "",
    val petGender: String = "",
    val petIntroduction: String = "",
    val petName: String = "",
    val petProfile: String?= null,
    val petType: String = "",
    var uid:String = "",
    val latitude : Int = 0,
    val longtitude : Int = 0


): Parcelable{

}
@Parcelize
data class ChatMessage(
    val senderId: String = "",
    val senderPetName: String = "",
    val receiverId: String = "",
    val message: String = "",
    val imageUrl: String? = null,
    val timestamp: Long = System.currentTimeMillis()
): Parcelable

