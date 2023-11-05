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
import com.google.firebase.database.MutableData
import com.google.firebase.database.Transaction
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
class HomeFilterViewModel: ViewModel() {
    private val _filteredUsers = MutableLiveData<List<User>>()
    val filteredUsers: MutableLiveData<List<User>> get() = _filteredUsers

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading


    private val store: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val currentUser = FirebaseAuth.getInstance().currentUser?.uid?:""
    private val swipedUsersRef = database.getReference("swipedUsers").child(currentUser)
    private val filteredUsersRef = database.getReference("filteredUsers").child(currentUser)
    private var lastFilter: Filter? = null



    fun filterUsers(filter: Filter) {
//        if (filter == lastFilter) return
        lastFilter = filter
        var query: Query = store.collection("User")

        if (filter.petGender != "all") {
            filter.petGender?.let {
                val genderValue = when (it) {
                    "남" -> "남"
                    "여" -> "여"
                    else -> it
                }
                query = query.whereEqualTo("petGender", genderValue)
            }
        }
        if (filter.petNeuter != "상관없음") {
            filter.petNeuter?.let {
                val genderValue = when (it) {
                    "중성화" -> "중성화"
                    "중성화 안함" -> "중성화 안함"
                    else -> it
                }
                query = query.whereEqualTo("petNeuter", genderValue)
            }
        }
        if (filter.petType != "전체") {
            filter.petType?.let { query = query.whereEqualTo("petType", it) }
        }
        query.get().addOnSuccessListener { documents ->
            val users = documents.map { it.toObject(User::class.java) }
                .filter { it.uid != currentUser }
            // 스와이프한 사용자를 제외하는로직입니다
            excludeSwipedUsers(users) { filteredUsers ->
                _filteredUsers.value = filteredUsers
                updateFilteredUsers(filteredUsers)
                loadFilteredUsers()
            }
        }
    }

    fun loadFilteredUsers() {
        _isLoading.value = true
        filteredUsersRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val users = snapshot.children.mapNotNull { it.getValue(User::class.java) }
                _filteredUsers.value = users
                _isLoading.value = false
            }

            override fun onCancelled(error: DatabaseError) {
                _isLoading.value = false
            }
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
        filteredUsersRef.child(userId).removeValue()
    }

    // 필터링된 사용자 목록을 업데이트하는 함수입니당
    private fun updateFilteredUsers(users: List<User>) {
    //이 트랜잭션을 사용하는 이유: 여러 사용자가 동시에 같은 데이터를 수정할 때 발생할 수 있는 충돌을 방지하기 위해(현재상태기반으로 데이터를 읽고 쓰는 방식)
        filteredUsersRef.runTransaction(object : Transaction.Handler {
            override fun doTransaction(mutableData: MutableData): Transaction.Result {
                mutableData.value = users.associateBy { it.uid }
                return Transaction.success(mutableData)
            }

            override fun onComplete(
                databaseError: DatabaseError?,
                b: Boolean,
                dataSnapshot: DataSnapshot?
            ) {
                if (databaseError != null) {
                    Log.e("ffff", "Failed users", databaseError.toException())
                } else {
                    Log.d("ffff", "Updated ${users.size} users")
                }
            }
        })
    }
}




