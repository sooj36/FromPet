package com.example.frompet.data.repository.map

import com.example.frompet.data.model.UserLocation

interface MapRepository {

//      suspend fun getCommunityData(petType:String): List<CommunityData>
      suspend fun getUserLocation() : List<UserLocation>



}
