package com.pet.frompet.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class CommentData(
    val commentId: String,
    val content: String = "",
    val authorUid: String = "",
    val postDocumentId: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val reportCount: Int = 0,
    val likeCount: Int = 0,
    val likeUsers: List<String> = listOf(),
    val reCommentCount: Int? = null, // 이 부분을 추가합니다.
): Parcelable{
    constructor() : this("", "", "", "", 0, 0, 0, listOf(), null)
}

