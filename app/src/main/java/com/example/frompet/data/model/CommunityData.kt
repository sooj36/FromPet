package com.example.frompet.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.sql.Timestamp


@Parcelize
data class CommunityData(
    val title : String = "",
    val tag : String = "",
    val timestamp : String = "",
    val contents : String = "",
    var uid: String = ""
): Parcelable
