package com.example.frompet.data.repository.category

import android.content.Context
import android.util.Log
import com.example.frompet.R
import com.example.frompet.data.model.CommunityData
import com.example.frompet.data.model.CommunityHomeData
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class CategoryRepositoryImp(
    private val context: Context
) : CategoryRepository {
    override suspend fun getCategory(): List<CommunityHomeData> {
        val categories = listOf(
            "category_dog", "category_cat", "category_raccoon", "category_fox",
            "category_chick", "category_pig", "category_snake", "category_fish"
        )
        Log.e("zzzzz", "getCategory called with categories: $categories")
        return categories.map { categoryString ->
            CommunityHomeData(
                getAnimalImage(categoryString), // 이미지 리소스 ID 가져오기
                context.getString(
                    context.resources.getIdentifier(
                        categoryString,
                        "string",
                        context.packageName
                    )
                )
            )

        }
    }

    override suspend fun getCommunityData(petType:String): List<CommunityData> {
        val firestore = FirebaseFirestore.getInstance()
        val communityDataList = mutableListOf<CommunityData>()
        try {
            val querySnapshot = firestore.collection("Community")
                .whereEqualTo("petType",petType).get().await()
            Log.e("aaaaaa","$petType")
            for(document in querySnapshot.documents){
                val petType = document.getString("petType") ?: ""
                val docsId = document.id

                val communityData = CommunityData(petType = petType, docsId = docsId)
                communityDataList.add(communityData)
            }
            Log.e("aaaaa","$communityDataList")

        }catch (e:Exception){
            Log.e("aaaaaa","error",e)
        }
        return communityDataList
    }

    private fun getAnimalImage(categoryString: String): Int {
        val resourceName = categoryString.split("_").last()
        val resourceIdName =
            context.resources.getIdentifier(resourceName, "drawable", context.packageName)
        return if (resourceIdName != 0) resourceIdName else R.drawable.frog
    }

}
