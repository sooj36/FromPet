package com.example.frompet.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserLocation(
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
): Parcelable
