package com.example.frompet.data.model

import com.google.firebase.database.Exclude


data class CommunityHomeData(
    val petLogo: Int,
    val petType: String,
    var uid: String=""
)
