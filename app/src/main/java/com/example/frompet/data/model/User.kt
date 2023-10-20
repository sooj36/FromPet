package com.example.frompet.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(
    val petAge: Int = 0,
    val petDescription: String = "",
    val petGender: String = "",
    val petIntroduction: String = "",
    val petName: String = "",
    val petProfile: String?= null,
    val petType: String = "",
    var uid:String = ""
): Parcelable
