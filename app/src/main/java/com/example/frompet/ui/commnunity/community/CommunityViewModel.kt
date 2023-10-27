package com.example.frompet.ui.commnunity.community

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.airbnb.lottie.L
import com.example.frompet.data.model.CommunityData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CommunityViewModel : ViewModel() {

    // 데이터 가져오기
    private val communitydb = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // Firebase 현재 사용자 가져오기
    private val currentUserId = auth.currentUser?.uid
    private val currentUser = FirebaseAuth.getInstance().currentUser

    private val _title = MutableLiveData<String?>()
    val title: LiveData<String?> = _title

    private val _contents = MutableLiveData<String>()
    val contents: LiveData<String> = _contents

    private val _timestamp = MutableLiveData<String>()
    val timestamp: LiveData<String> = _timestamp

    private val _tag = MutableLiveData<String>()
    val tag: LiveData<String> = _tag

    private val _communityList = MutableLiveData<List<CommunityData>>()
    val communityList: LiveData<List<CommunityData>> = _communityList


    // firebase에서 커뮤니티 정보 로드하고 뷰모델에 할당
    fun loadCommunityList() {
        val userId = currentUser?.uid

        if (userId != null) {
            communitydb.collection("Community").document(userId).get()
                .addOnSuccessListener { documentSnapshop ->
                    if (documentSnapshop.exists()) {
                        _title.value = documentSnapshop.getString("title")
                        _contents.value = documentSnapshop.getString("contents")
                        _tag.value = documentSnapshop.getString("tag")
                        _timestamp.value = documentSnapshop.getString("timestamp")
                    }
                }
        }

    }

    fun loadCommunityData() {
        val currentUserId = auth.currentUser?.uid
        communitydb.collection("Community")
            .get()
            .addOnSuccessListener { querySnapshot ->
                val communityList = mutableListOf<CommunityData>()

                if (querySnapshot.isEmpty.not()) {
                    for (document in querySnapshot.documents) {
                        val data = document.toObject(CommunityData::class.java)
                        data?.let {
                            communityList.add(it)
                        }
                    }
                    _communityList.value = communityList
                }
            }
            .addOnFailureListener { exception ->
                Log.e("sooj", "데이터 로딩 실패", exception)
            }
    }
}