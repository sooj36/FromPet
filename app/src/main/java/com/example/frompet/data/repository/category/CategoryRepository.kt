package com.example.frompet.data.repository.category

import com.example.frompet.data.model.CommunityData
import com.example.frompet.data.model.CommunityHomeData

interface CategoryRepository {
    suspend fun getCategory(): List<CommunityHomeData>

    suspend fun getCommunityData(petType:String): List<CommunityData>

}