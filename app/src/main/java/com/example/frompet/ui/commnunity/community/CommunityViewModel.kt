package com.example.frompet.ui.commnunity.community


import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.frompet.data.model.CommunityData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class CommunityViewModel : ViewModel() {

    // 데이터 가져오기
    private val communitydb = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // Firebase 현재 사용자 가져오기
    private val currentUserId = auth.currentUser?.uid
    private val currentUser = FirebaseAuth.getInstance().currentUser

    private val _communityList = MutableLiveData<List<CommunityData>>()
    val communityList: LiveData<List<CommunityData>> = _communityList


    // 데이터 로드
    fun loadCommunityListData(filter: String) {
        val currentUserId = auth.currentUser?.uid
        val chipQuery = communitydb.collection("Community")

        if (filter.isNotEmpty()) {
            chipQuery.orderBy("timestamp", Query.Direction.DESCENDING)
        } else {
            chipQuery.whereEqualTo("tag", filter)
        }
//        for (filter in filters) {
//            chipQuery.whereEqualTo("tag", filter)

        communitydb
            .collection("Community")
            .whereEqualTo("tag", filter)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()

            .addOnSuccessListener { querySnapshot ->
                val communityListData = mutableListOf<CommunityData>()

                if (querySnapshot.isEmpty.not()) {
                    for (document in querySnapshot.documents) {
                        val data = document.toObject(CommunityData::class.java)
                        data?.let {
                            communityListData.add(it)
                        }
                    }
                    _communityList.value = communityListData
                    Log.d("sooj", "${communityListData}")
                }
            }
            .addOnFailureListener { exception ->
                Log.e("sooj", "456 데이터 로딩 실패", exception)
            }
    }
}
