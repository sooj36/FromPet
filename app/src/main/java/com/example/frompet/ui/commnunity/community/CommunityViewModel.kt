package com.example.frompet.ui.commnunity.community


import android.media.metrics.Event
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.frompet.SingleLiveEvent
import com.example.frompet.data.model.CommunityData
import com.example.frompet.ui.commnunity.communityhome.CategoryClick
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class CommunityViewModel(
) : ViewModel() {

    // 데이터 가져오기
    private val communitydb = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // Firebase 현재 사용자 가져오기
    private val currentUserId = auth.currentUser?.uid
    private val currentUser = FirebaseAuth.getInstance().currentUser

    private val _communityList = MutableLiveData<List<CommunityData>?>()
    val communityList: MutableLiveData<List<CommunityData>?> = _communityList




    private val _event: SingleLiveEvent<CategoryClick> = SingleLiveEvent()
    val event: LiveData<CategoryClick> get() = _event
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()


    // 데이터 로드
//    fun loadCommunityListData(filter: String) {
//
//        val communityListData = mutableListOf<CommunityData>()
//        if (filter == "전체") {
//            communitydb
//                .collection("Community")
//                .orderBy("timestamp", Query.Direction.DESCENDING)
//                .get()
//
//                .addOnSuccessListener { querySnapshot ->
//                    Log.d("sooj", "tagadd ${querySnapshot.size()}")
//
//
//                    if (querySnapshot.isEmpty.not()) {
//                        for (document in querySnapshot.documents) {
//                            val data = document.toObject(CommunityData::class.java)
//                            data?.let {
//                                communityListData.add(it)
//                            }
//                        }
//
//
//                        Log.d("sooj", "test ${communityListData}")
//                    }
//                    _communityList.value = communityListData
//                }
//                .addOnFailureListener { exception ->
//                    Log.e("sooj", "456 데이터 로딩 실패", exception)
//                }
//        } else {
//            communitydb
//                .collection("Community")
//                .whereEqualTo("tag", filter)
//                .orderBy("timestamp", Query.Direction.DESCENDING)
//                .get()
//
//                .addOnSuccessListener { querySnapshot ->
//                    Log.d("sooj", "tagadd ${querySnapshot.size()}")
//
//
//                    if (querySnapshot.isEmpty.not()) {
//                        for (document in querySnapshot.documents) {
//                            val data = document.toObject(CommunityData::class.java)
//                            data?.let {
//                                communityListData.add(it)
//                            }
//                        }
//                        Log.d("sooj", "test ${communityListData}")
//                    }
//                    _communityList.value = communityListData
//                }
//                .addOnFailureListener { exception ->
//                    Log.e("sooj", "456 데이터 로딩 실패", exception)
//                }
//
//        }
//    }
    fun getCommunityData(petType: String): LiveData<List<CommunityData>> {
        val liveData = MutableLiveData<List<CommunityData>>()
        firestore.collection("Community")
            .whereEqualTo("petType", petType)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { documents ->
                Log.d("zzzzzz", "Documents: $documents")

                val communityDataList = documents.mapNotNull { document ->
                    document.toObject(CommunityData::class.java)
                }
                liveData.value = communityDataList
            }
            .addOnFailureListener { exception ->
            }
        return liveData
    }

    fun getFilterCommunityData(petType: String, filter: String): LiveData<List<CommunityData>> {
        val liveData = MutableLiveData<List<CommunityData>>()

        var query: Query = firestore.collection("Community").whereEqualTo("petType", petType)

        if (filter != "전체") {
            query = query.whereEqualTo("tag", filter)
        }
        query.orderBy("timestamp", Query.Direction.DESCENDING).get()
            .addOnSuccessListener { docs ->
                val communityDataList = docs.mapNotNull { document ->
                    document.toObject(CommunityData::class.java)
                }
                liveData.value = communityDataList
            }
            .addOnFailureListener {}
        return liveData
    }
    fun deleteCommunityData(docsId: String) {
        firestore.collection("Community").document(docsId).delete()
            .addOnSuccessListener {
                val updatedList = _communityList.value?.filterNot { it.docsId == docsId }
                _communityList.postValue(updatedList)
            }
            .addOnFailureListener { e ->
            }
    }
}


