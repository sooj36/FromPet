package com.pet.frompet.data.repository.category

import com.pet.frompet.data.model.CommunityData
import com.pet.frompet.data.model.CommunityHomeData

interface CategoryRepository {
    suspend fun getCategory(): List<CommunityHomeData>

    suspend fun getCommunityData(petType:String): List<CommunityData>

}