package com.example.frompet.login.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.frompet.login.data.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore


class MatchViewModel : ViewModel() {
    val likeList = MutableLiveData<List<UserModel>?>()
    val disLikeList = MutableLiveData<List<UserModel>>()
    val matchedList = MutableLiveData<List<UserModel>>()
    private val database = FirebaseDatabase.getInstance().getReference("likeUsers")
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    fun like(targetUserId: String) {
        val currentUserId = auth.currentUser?.uid ?: return
        Log.d("jun", "현재유저 ID: $currentUserId, 타겟유저 ID: $targetUserId")
        database.child(targetUserId).child("likedBy").child(currentUserId).setValue(true)

    }

    fun dislike(targetUserId: String) {
        val currentUserId = auth.currentUser?.uid ?: return
        database.child(currentUserId).child("likedBy").child(targetUserId).removeValue()
        database.child(currentUserId).child("matched").child(targetUserId).removeValue()
        database.child(targetUserId).child("matched").child(currentUserId).removeValue()

    }
    fun loadlikes() {
        val currentUserId = auth.currentUser?.uid ?: return

        database.child(currentUserId).child("likedBy").addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val likedUserIds = snapshot.children.mapNotNull { it.key }
                val likedUsers = mutableListOf<UserModel>()

                likedUserIds.forEach { userId ->
                    database.child(currentUserId).child("matched").child(userId)
                        .addValueEventListener(object : ValueEventListener {
                            override fun onDataChange(matchedSnapshot: DataSnapshot) {
                                if (!matchedSnapshot.exists()) { // 매치된 사용자가 아닌 경우에만 추가
                                    firestore.collection("User").document(userId)
                                        .get()
                                        .addOnSuccessListener { document ->
                                            val user = document.toObject(UserModel::class.java)
                                            user?.let {
                                                likedUsers.add(it)
                                                likeList.value = likedUsers.toList()
                                                Log.d("jun", "매치되기전라이크리스트${likeList.value}")
                                            }
                                        }
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {}
                        })
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun matchWithUser(otherUserUid: String) {
        val currentUserId = auth.currentUser?.uid ?: return

        // 서로 like한 경우
        Log.d("jun", "매치 유저 uid: $otherUserUid")
        database.child(currentUserId).child("matched").child(otherUserUid).setValue(true)
        database.child(otherUserUid).child("matched").child(currentUserId).setValue(true)
        database.child(currentUserId).child("likedBy").child(otherUserUid).removeValue()
        val currentLikes = likeList.value?.toMutableList() ?: mutableListOf()
        currentLikes?.removeIf { it.uid == otherUserUid }
        likeList.value = currentLikes
        Log.d("jun", "매치된후라이크리스트:${likeList.value}")
        loadlikes()
    }


    fun loadMatchedUsers() {
        val currentUserId = auth.currentUser?.uid ?: return

        database.child(currentUserId).child("matched").addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val matchedUserIds = snapshot.children.mapNotNull { it.key }
                val matchedUsers = mutableListOf<UserModel>()

                matchedUserIds.forEach { userId ->
                    firestore.collection("User").document(userId)
                        .get()
                        .addOnSuccessListener { document ->
                            val user = document.toObject(UserModel::class.java)
                            user?.let {
                                matchedUsers.add(it)
                                matchedList.value = matchedUsers.toList()
                            }
                        }
                        .addOnFailureListener { exception ->
                            Log.d("jun", "Error  ", exception)
                        }
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
}







