package com.example.frompet.data.repository.category

import com.example.frompet.R
import com.example.frompet.data.model.CommunityHomeData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class CategoryRepositoryImp(
    private val firestore: FirebaseFirestore
): CategoryRepository{
    override suspend fun getCategory(): List<CommunityHomeData> {
        val communityHomeData = mutableListOf(
            CommunityHomeData(R.drawable.dog, "강아지"),
            CommunityHomeData(R.drawable.cat, "고양이"),
            CommunityHomeData(R.drawable.raccoon, "라쿤"),
            CommunityHomeData(R.drawable.fox, "여우"),
            CommunityHomeData(R.drawable.chick, "새"),
            CommunityHomeData(R.drawable.pig, "돼지"),
            CommunityHomeData(R.drawable.snake, "파충류"),
            CommunityHomeData(R.drawable.fish, "물고기")
        )
        return communityHomeData
    }
}