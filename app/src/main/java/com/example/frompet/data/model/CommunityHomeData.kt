package com.example.frompet.data.model

import com.google.firebase.database.Exclude


data class CommunityHomeData(
    val pet_logo: Int,
    val pet_name: String,
    var uid: String="" //var 은 지향 copy 사용 --> 무결성이 사라짐.
){
}
