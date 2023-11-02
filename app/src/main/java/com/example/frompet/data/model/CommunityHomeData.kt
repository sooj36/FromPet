package com.example.frompet.data.model


data class CommunityHomeData(
    val petLogo: Int,
    val petName: String,
   /* var uid: String="" //var 은 지향 copy 사용 --> 무결성이 사라짐.*/
){
}
