package com.example.frompet.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CommunityHomeData(
    val pet_logo: Int,
    val pet_name: String
): Parcelable
