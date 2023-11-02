package com.example.frompet.data.model

import android.os.Parcelable
import com.google.firebase.firestore.DocumentReference
import kotlinx.android.parcel.Parcelize


@Parcelize
data class CommunityData(
    var title: String = "",
    var tag: String = "",
    val timestamp: String = "",
    var contents: String = "",
    var uid: String = "",
    var docsId: String? = null,
    val petType: String? = ""
): Parcelable

