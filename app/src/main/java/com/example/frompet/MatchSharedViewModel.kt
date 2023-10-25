package com.example.frompet

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.frompet.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore


class MatchSharedViewModel : ViewModel() {

    private val _likeList :MutableLiveData<List<User>?> = MutableLiveData()
    val likeList : MutableLiveData<List<User>?> get() = _likeList
//    private val _disLikeList : MutableLiveData<List<UserModel>> = MutableLiveData()
//    val disLikeList : MutableLiveData<List<UserModel>>  get() =  _disLikeList
    private val _matchedList : MutableLiveData<List<User>> = MutableLiveData()
    val matchedList : MutableLiveData<List<User>> get() = _matchedList

    private val database = FirebaseDatabase.getInstance().getReference("likeUsers")
    private val disLikeDb = FirebaseDatabase.getInstance().getReference("dislikeList")

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    fun like(targetUserId: String) {
        val currentUserId = auth.currentUser?.uid ?: return
        database.child(targetUserId).child("likedBy").child(currentUserId).setValue(true)
    }

    fun dislike(targetUserId: String) {
        val currentUserId = auth.currentUser?.uid ?: return
        database.child(currentUserId).child("likedBy").child(targetUserId).removeValue()
        disLikeDb.child(currentUserId).child(targetUserId).setValue(true)
    }

    fun loadlike() {
        val currentUserId = auth.currentUser?.uid ?: return

        database.child(currentUserId).child("likedBy").addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val likedUserIds = snapshot.children.mapNotNull { it.key }
                val likedUsers = mutableListOf<User>()

                likedUserIds.forEach { userId ->
                    database.child(currentUserId).child("matched").child(userId)
                        .addValueEventListener(object : ValueEventListener {

                            override fun onDataChange(matchedSnapshot: DataSnapshot) {
                                if (!matchedSnapshot.exists()) { // 매치된 사용자가 아닌 경우에만 추가
                                    firestore.collection("User").document(userId)
                                        .get()
                                        .addOnSuccessListener { document ->
                                            val user = document.toObject(User::class.java)
                                            user?.let {
                                                // 중복 체크: 이미 likedUsers에 있는 사용자는 추가하지 않음
                                                if (likedUsers.none { existingUser -> existingUser.uid == user.uid }) {
                                                    likedUsers.add(it)
                                                    _likeList.value = likedUsers.toList()
                                                    Log.d("jun", "매치되기전라이크리스트${_likeList.value}")
                                                }
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

    fun loadAlreadyActionUsers(load: (List<String>) -> Unit) {
        val currentUserId = auth.currentUser?.uid ?: return
        val exceptIds = mutableListOf<String>()

        disLikeDb.child(currentUserId).addListenerForSingleValueEvent(object : ValueEventListener {  //디스라이크유저 불러오기
            override fun onDataChange(snapshot: DataSnapshot) {

                snapshot.children.forEach { childSnapshot ->
                    val userId = childSnapshot.key
                    userId?.let { exceptIds.add(it) }
                }
                database.addListenerForSingleValueEvent(object : ValueEventListener { //likeby노드에 내유아디가 들어가서 전체순회
                    override fun onDataChange(snapshot: DataSnapshot) {
                        snapshot.children.forEach { userSnapshot ->
                            val userId = userSnapshot.key
                            if (userSnapshot.child("likedBy").hasChild(currentUserId)) {
                                userId?.let { exceptIds.add(it) }

                            }
                        }
                        database.child(currentUserId).child("matched").addListenerForSingleValueEvent(object : ValueEventListener { //매치된유저불러오기
                            override fun onDataChange(snapshot: DataSnapshot) {
                                snapshot.children.forEach { childSnapshot ->
                                    val userId = childSnapshot.key
                                    userId?.let { exceptIds.add(it) }
                                }
                                load(exceptIds)
                            }

                            override fun onCancelled(error: DatabaseError) {}
                        })
                    }

                    override fun onCancelled(error: DatabaseError) {}
                })
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }


    fun getExceptDislikeAndMe(onSuccess: (List<User>) -> Unit, onFailure: (Exception) -> Unit) {
        val allUsersData = mutableListOf<User>()
        val currentUserId = auth.currentUser?.uid

        loadAlreadyActionUsers { exceptionUsers ->

            firestore.collection("User")
                .get()
                .addOnSuccessListener { querySnapshot ->
                    if (querySnapshot.isEmpty.not()) {
                        for (document in querySnapshot.documents) {
                            val user = document.toObject(User::class.java)
                            user?.let {
                                if (it.uid != currentUserId && it.uid !in exceptionUsers) {
                                    allUsersData.add(it)
                                }
                            }
                        }
                        onSuccess(allUsersData)
                    }
                }
                .addOnFailureListener { e ->
                    onFailure(e)
                }
        }
    }

    fun matchUser(otherUserUid: String) {
        val currentUserId = auth.currentUser?.uid ?: return
        // 서로 like한 경우
        Log.d("jun", "매치 유저 uid: $otherUserUid")
        database.child(currentUserId).child("matched").child(otherUserUid).setValue(true)
        database.child(otherUserUid).child("matched").child(currentUserId).setValue(true)
        database.child(currentUserId).child("likedBy").child(otherUserUid).removeValue()
        database.child(otherUserUid).child("likedBy").child(currentUserId).removeValue()

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
                val matchedUsers = mutableListOf<User>()
                if (matchedUserIds.isEmpty()) {
                    _matchedList.value = listOf()
                    return //이 부분 추가해서 매치리스트가 빈null일때도 ui업뎃하게함..
                }

                matchedUserIds.forEach { userId ->
                    firestore.collection("User").document(userId)
                        .get()
                        .addOnSuccessListener { document ->
                            val user = document.toObject(User::class.java)
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
    fun removeMatchedUser(targetUserId: String) {
        val currentUserId = auth.currentUser?.uid ?: return
        database.child(currentUserId).child("matched").child(targetUserId).removeValue()
        database.child(targetUserId).child("matched").child(currentUserId).removeValue()
        database.child(targetUserId).child("likeBy").child(currentUserId).removeValue()
        database.child(currentUserId).child("likeBy").child(targetUserId).removeValue()
        loadMatchedUsers()
        loadlike()

        Log.d("jun", "삭제한 후 매치리스트  : ${_matchedList.value}")
    }


}














