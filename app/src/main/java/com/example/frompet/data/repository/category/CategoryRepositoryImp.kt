package com.example.frompet.data.repository.category

import com.example.frompet.R
import com.example.frompet.data.model.CommunityHomeData
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class CategoryRepositoryImp(
    private val firestore: FirebaseFirestore,
): CategoryRepository{
    override suspend fun getCategory(): List<CommunityHomeData> {
        return mutableListOf(
            CommunityHomeData("dog", "강아지"),
            CommunityHomeData("cat", "고양이"),
            CommunityHomeData("raccoon", "라쿤"),
            CommunityHomeData("fox", "여우"),
            CommunityHomeData("chick", "새"),
            CommunityHomeData("pig", "돼지"),
            CommunityHomeData("snake", "파충류"),
            CommunityHomeData("fish", "물고기"),
        )
    }

    override suspend fun getAnimalCategory(): List<CommunityHomeData> {
        val animalCategory = mutableListOf<CommunityHomeData>()
        val querySnapshot = firestore.collection("Animal").get().await()
        for (document in querySnapshot.documents){
            val animalName = document.getString("name") ?: ""
            val animalImage = getAnimalImage(animalName)
            animalCategory.add(CommunityHomeData(animalImage.toString(),animalName))

        }
        return animalCategory
    }

    private fun getAnimalImage(animalName: String):Int{
        return when(animalName){
            "강아지" -> R.drawable.dog
            "고양이" -> R.drawable.cat
            "라쿤" -> R.drawable.raccoon
            "여우" -> R.drawable.fox
            "새" -> R.drawable.chick
            "돼지" -> R.drawable.pig
            "파충류" -> R.drawable.snake
            "물고기" -> R.drawable.fish
            else -> R.drawable.frog
        }

    }

}
