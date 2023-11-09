package com.pet.frompet.data.model

import android.os.Parcelable
import com.google.firebase.firestore.DocumentReference
import kotlinx.android.parcel.Parcelize


@Parcelize
data class CommunityData(
    var title: String = "",
    var tag: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    var contents: String = "",
    var uid: String = "",
    var docsId: String? = null,
    val petType: String? = "",
    val petProfile: String?= null,
    val petName: String = "",
    var viewCount :Int? = 0,
    var imageUrl: String? = null,
    val userLocation: UserLocation = UserLocation()

    ): Parcelable{
    // 기본 toString() 메서드를 오버라이드하여 원하는 형식으로 출력
    override fun toString(): String {
        return "CommunityData(title=$title, tag=$tag, timestamp=$timestamp, " +
                "contents=$contents, uid=$uid, docsId=$docsId, petType=$petType, " +
                "petProfile=$petProfile, petName=$petName,imageUrl=$imageUrl)"
    }
}

