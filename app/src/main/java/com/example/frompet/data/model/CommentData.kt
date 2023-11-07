package com.example.frompet.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class CommentData(
    val commentId: String,
    val content: String = "",
    val authorUid: String = "",
    val postDocumentId: String = "",
    val timestamp: Long = System.currentTimeMillis()
): Parcelable{
    constructor() : this("", "", "", "", 0) // 기본 생성자 추가
}


