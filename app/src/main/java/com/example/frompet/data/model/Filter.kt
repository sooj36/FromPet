package com.example.frompet.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize

data class Filter(
    val petType: String? = "",
    val petGender: String? = "",
    val petNeuter: String? = "",
    var distanceFrom: Float = 10.0f,
    var distanceTo: Float = 360.0f
): Parcelable
