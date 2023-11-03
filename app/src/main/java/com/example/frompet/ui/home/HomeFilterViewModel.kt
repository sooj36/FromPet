package com.example.frompet.ui.home

import android.util.Log
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.frompet.MatchSharedViewModel
import com.example.frompet.data.model.Filter
import com.example.frompet.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
class HomeFilterViewModel: ViewModel() {
    private val _filteredUsers = MutableLiveData<List<User>>()
    val filteredUsers: MutableLiveData<List<User>> get() = _filteredUsers

    private val store: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val currentUser = FirebaseAuth.getInstance().currentUser?.uid?:""
    private val swipedUsersRef = database.getReference("swipedUsers").child(currentUser)


    fun filterUsers(filter: Filter) {
        var query: Query = store.collection("User")

        if (filter.petGender != "all") {
            filter.petGender?.let {
                val genderValue = when (it) {
                    "male" -> "남"
                    "female" -> "여"
                    else -> it
                }
                query = query.whereEqualTo("petGender", genderValue)
            }
        }
        if (filter.petType != "전체") {
            filter.petType?.let { query = query.whereEqualTo("petType", it) }
        }
        query.get().addOnSuccessListener { documents ->
            val users = documents.map { it.toObject(User::class.java) }
                .filter { it.uid != currentUser }

            // 스와이프한 사용자를 제외합니다
            excludeSwipedUsers(users) { filteredUsers ->
                _filteredUsers.value = filteredUsers
                updateFilteredUsers(filteredUsers)
            }
        }
    }

    fun loadFilteredUsers() {
        val filteredUsersRef = database.getReference("filteredUsers").child(currentUser)
        filteredUsersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val users = dataSnapshot.children.mapNotNull { it.getValue(User::class.java) }
                // 스와이프한 사용자를 제외하고 나서 ui를 업데이트하기 위한 콜백을 전달합니당
                excludeSwipedUsers(users) { filteredUsers ->
                    _filteredUsers.value = filteredUsers
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun excludeSwipedUsers(users: List<User>, callback: (List<User>) -> Unit) {
        swipedUsersRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val swipedUserIds = snapshot.children.map { it.key ?: "" }.toSet()
                val filteredList = users.filter { !swipedUserIds.contains(it.uid) }
                callback(filteredList)
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    // 사용자가 카드를 스와이프할 때 호출할 함수입니다:박세준
    fun userSwiped(userId: String) {
        // 스와이프한 사용자를 스와이프 노드에 추가합니다
        swipedUsersRef.child(userId).setValue(true)

        // 필터링된 사용자 목록에서 스와이프한 사용자를 제거합니당
        _filteredUsers.value = _filteredUsers.value?.filterNot { it.uid == userId }

        // 필터유저 노드에서 스와이프한 사용자를 제거합니다
        val filteredUsersRef = database.getReference("filteredUsers").child(currentUser)
        filteredUsersRef.child(userId).removeValue()
    }

    // 필터링된 사용자 목록을 업데이트하는 함수입니당
    private fun updateFilteredUsers(users: List<User>) {
        val filteredUsersRef = database.getReference("filteredUsers").child(currentUser)
        filteredUsersRef.removeValue().addOnCompleteListener {
            for (user in users) {
                filteredUsersRef.child(user.uid).setValue(user)
            }
        }
    }
}




