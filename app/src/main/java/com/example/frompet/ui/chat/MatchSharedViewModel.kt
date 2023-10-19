package com.example.frompet.ui.chat

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.frompet.data.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore


class MatchSharedViewModel : ViewModel() {
    private val _likeList :MutableLiveData<List<UserModel>?> = MutableLiveData()
    val likeList : MutableLiveData<List<UserModel>?> get() = _likeList
    private val _disLikeList : MutableLiveData<List<UserModel>> = MutableLiveData()
    val disLikeList : MutableLiveData<List<UserModel>>  get() =  _disLikeList
    private val _matchedList : MutableLiveData<List<UserModel>> = MutableLiveData()
    val matchedList : MutableLiveData<List<UserModel>> get() = _matchedList

    private val _dislikedUserIds = mutableListOf<String>()
    val dislikedUserIds: List<String>
        get() = _dislikedUserIds


    private val database = FirebaseDatabase.getInstance().getReference("likeUsers")
    private val unlikedb = FirebaseDatabase.getInstance().getReference("dislike")
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
        database.child(targetUserId).child("matched").child(currentUserId).removeValue()
        database.child(currentUserId).child("matched").child(targetUserId).removeValue()
        unlikedb.child(targetUserId).child("dislike").child(currentUserId).setValue(true)
        _dislikedUserIds.add(targetUserId)
    }

    fun loadlike() {
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
                                                _likeList.value = likedUsers.toList()
                                                Log.d("jun", "매치되기전라이크리스트${_likeList.value}")
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
    fun loadunlike() {
        val currentUserId = auth.currentUser?.uid ?: return

        val likedUsers = mutableListOf<UserModel>()

        // 사용자가 dislike한 대상들을 가져와서 필터링합니다.
        unlikedb.child(currentUserId).child("dislike").addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val dislikedUserIds = snapshot.children.mapNotNull { it.key }

                // 이후, 데이터베이스에서 사용자들을 가져오고 dislike 대상을 필터링하여 likedUsers 목록에 추가합니다.
                database.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        dataSnapshot.children.forEach { userSnapshot ->
                            val user = userSnapshot.getValue(UserModel::class.java)
                            user?.let {
                                if (it.uid != currentUserId && it.uid !in dislikedUserIds) {
                                    likedUsers.add(it)
                                }
                            }
                        }

                        // 화면에 표시할 사용자 중 dislike한 사용자를 필터링합니다.
                        _likeList.value = likedUsers.toList()
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // 에러 처리 로직 추가
                    }
                })
            }

            override fun onCancelled(error: DatabaseError) {
                // 에러 처리 로직 추가
            }
        })

        // 나머지 코드는 변경하지 않습니다.
    }


    fun matchUser(otherUserUid: String) {
        val currentUserId = auth.currentUser?.uid ?: return
        // 서로 like한 경우
        Log.d("jun", "매치 유저 uid: $otherUserUid")
        database.child(currentUserId).child("matched").child(otherUserUid).setValue(true)
        database.child(otherUserUid).child("matched").child(currentUserId).setValue(true)
        database.child(currentUserId).child("likedBy").child(otherUserUid).removeValue()
        val currentLikes = _likeList.value?.toMutableList() ?: mutableListOf()
        currentLikes?.removeIf { it.uid == otherUserUid }
        _likeList.value = currentLikes
        Log.d("jun", "매치된후라이크리스트:${_likeList.value}")
        loadlike()

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
                                _matchedList.value = matchedUsers.toList()
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














