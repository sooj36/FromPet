package com.example.frompet.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize

data class Filter(
    val petType: String? = "",
    val petGender: String? = "",
    val petNeuter: String? = "",
    val distanceFrom: Float = 0f,
    val distanceTo: Float = 600.0f
): Parcelable
