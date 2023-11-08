package com.pet.frompet.data.repository.map

import com.pet.frompet.data.model.UserLocation

interface MapRepository {

//      suspend fun getCommunityData(petType:String): List<CommunityData>
      suspend fun getUserLocation() : List<UserLocation>



}
