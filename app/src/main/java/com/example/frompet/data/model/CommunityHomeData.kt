package com.example.frompet.data.model

import java.io.Serializable


data class CommunityHomeData(
    val petLogo: Int,
    val petType: String,
    var title: String = "",
    var tag: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    var contents: String = "",
    var uid: String = "",
    var docsId: String? = null,
    val petProfile: String?= null,
    val petName: String = "",
    /* var uid: String="" //var 은 지향 copy 사용 --> 무결성이 사라짐.*/
):Serializable

fun CommunityHomeData.toCommunityData(): CommunityData {
    return CommunityData(
        title = title, // 클릭한 아이템에 해당하는 데이터의 실제 값 설정
        tag = tag,
        timestamp = System.currentTimeMillis(), // 필요한 경우 타임스탬프 설정
        contents =contents, // 필요한 경우 내용 설정
        uid = uid,
        docsId = docsId,
        petType = petType,
        petProfile = petProfile,
        petName = petName
    )

}
