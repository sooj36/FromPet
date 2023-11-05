package com.example.frompet.data.model

import java.io.Serializable


data class CommunityHomeData(
    val petLogo: Int,
    val petType: String,
    /* var uid: String="" //var 은 지향 copy 사용 --> 무결성이 사라짐.*/
):Serializable

fun CommunityHomeData.toCommunityData(): CommunityData {
    return CommunityData(
        petType = petType
    )

}
