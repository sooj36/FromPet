package com.example.frompet.ui.commnunity.community

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CommunityViewModel : ViewModel() {
    private val currentUser = FirebaseAuth.getInstance().currentUser
    private val firestore = FirebaseFirestore.getInstance()

    private val _title = MutableLiveData<String?>()
    val title : LiveData<String?> = _title

    private val _contents = MutableLiveData<String>()
    val contents : LiveData<String> = _contents

    private val _timestamp = MutableLiveData<String>()
    val timestamp : LiveData<String> = _timestamp

    private val _tag = MutableLiveData<String>()
    val tag : LiveData<String> = _tag

    fun loadCommunity() {
        val userId = currentUser?.uid

        if (userId != null) {
            firestore.collection("Community").document(userId).get()
                .addOnSuccessListener { documentSnapshop ->
                    if (documentSnapshop.exists()) {
                        _title.value = documentSnapshop.getString("title")
                        _contents.value = documentSnapshop.getString("contents")
                    }
                }
        }
    }


}