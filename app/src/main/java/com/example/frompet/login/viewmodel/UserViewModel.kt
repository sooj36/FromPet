package com.example.frompet.login.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.frompet.login.data.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore


class UserViewModel : ViewModel() {
    val likeList = MutableLiveData<List<UserModel>>()
    val disLikeList = MutableLiveData<List<UserModel>>()
    private val database = FirebaseDatabase.getInstance().getReference("likes")
    private val firestore = FirebaseFirestore.getInstance()

    fun like(targetUserId: String) {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        Log.d("jun", "$currentUserId")
        database.child(currentUserId).child("liked").child(targetUserId).setValue(true)
        database.child(targetUserId).child("likedBy").child(currentUserId).setValue(true)
    }

    fun dislike(targetUserId: String) {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        database.child(currentUserId).child("disliked").child(targetUserId).setValue(true)
        database.child(targetUserId).child("dislikedBy").child(currentUserId).setValue(true)
    }

    fun loadlikes() {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        database.child(currentUserId).child("likedBy").addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // 여기서 likedUserIds는 likedBy 항목의 사용자 id목록임 즉 나에게 좋아요보낸사람들
                val likedUserIds = snapshot.children.mapNotNull { it.key }
                val likedUsers = mutableListOf<UserModel>()

                likedUserIds.forEach { userId ->
                    // 여기서 파이어스토어에서 각 사용자의 정보를 로드해서 forEath로 순회하면서 맞는데이터 변환
                    firestore.collection("User").document(userId)
                        .get()
                        .addOnSuccessListener { document ->
                            val user = document.toObject(UserModel::class.java)
                            user?.let {
                                likedUsers.add(it)
                                likeList.value = likedUsers.toList()
                            }
                        }
                        .addOnFailureListener { exception ->
                            Log.d("jun", "Error  ", exception)
                        }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("jun", "db error: ${error.message}")
            }
        })
    }
}





