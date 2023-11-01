package com.example.frompet.data.model

import com.google.firebase.database.Exclude


data class CommunityHomeData(
    val pet_logo: Int,
    val pet_name: String,
    var uid: String=""
){
}
