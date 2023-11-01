package com.example.frompet.data.model

import com.google.firebase.database.Exclude


data class CommunityHomeData(
    val pet_logo: String,
    val pet_name: String,
    var uid: String=""
){
    constructor():this("","")

    @Exclude
    fun toMap():Map<String,Any>{
        return mapOf(
            "pet_logo" to pet_logo,
            "pet_name" to pet_name,
            "uid" to uid
        )
    }
}
