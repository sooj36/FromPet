package com.example.frompet.login.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserModel(
    val petAge: Int = 0,
    val petDescription: String = "",
    val petGender: String = "",
    val petIntroduction: String = "",
    val petName: String = "",
    val petProfile: String = "",
    val petType: String = "",
    var uid: String = "CmrOTtczqVMUzuCFpVgIp1zFkOH3" //CmrOTtczqVMUzuCFpVgIp1zFkOH3 zizi실험할땐 이걸로하기 Td4QjX4JQ2Y0EzUVmkY2JIYz8ML2 hihi하트보내고 매칭할때까진 C로 채팅은 T로


): Parcelable
@Parcelize
data class ChatMessage(
    val senderId: String = "",
    val senderPetName: String = "",
    val receiverId: String = "",
    val message: String = "",
    val timestamp: Long = System.currentTimeMillis()
): Parcelable

