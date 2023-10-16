package com.example.frompet.login.repository

import com.example.frompet.login.data.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.suspendCancellableCoroutine

class UserRepositoryImp : UserRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override suspend fun saveUser(user: UserModel, callback: (Boolean) -> Unit): List<UserModel>? {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val usersCollection = firestore.collection("User")
            usersCollection.document(currentUser.uid).set(user)
                .addOnSuccessListener {
                    callback(true) // 성공
                }
                .addOnFailureListener {
                    callback(false) // 실패
                }
        } else {
            callback(false) // 현재 사용자가 로그인되어 있지 않음
        }
        return null // 무엇을 반환해도 상관 없음
    }

    override fun getUser(userId: String, callback: (UserModel?) -> Unit) {
        val usersCollection = firestore.collection("User")
        usersCollection.document(userId).get()
            .addOnSuccessListener { documentSnapshot ->
                val user = documentSnapshot.toObject(UserModel::class.java)
                callback(user)
            }
            .addOnFailureListener {
                callback(null)
            }
    }
}

