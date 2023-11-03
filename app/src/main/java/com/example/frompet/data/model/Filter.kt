package com.example.frompet.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize

data class Filter(
    val petType: String? = null,
    val petGender: String? = null,
    val petNeuter: String? = null,
    val location: String? = null
): Parcelable
