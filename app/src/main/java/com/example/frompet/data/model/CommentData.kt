package com.example.frompet.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class CommentData(
    val content: String = "",
    val authorUid: String = "",
    val authorName: String = "",
    val authorProfile: String?= null,
    val postDocumentId: String = "",
    val timestamp: Long = System.currentTimeMillis()
): Parcelable
