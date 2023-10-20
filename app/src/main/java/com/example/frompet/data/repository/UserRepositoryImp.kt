package com.example.frompet.data.repository

import com.example.frompet.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class UserRepositoryImp : UserRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override suspend fun saveUser(user: User, callback: (Boolean) -> Unit): List<User>? {
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

    override fun getUser(userId: String, callback: (User?) -> Unit) {
        val usersCollection = firestore.collection("User")
        usersCollection.document(userId).get()
            .addOnSuccessListener { documentSnapshot ->
                val user = documentSnapshot.toObject(User::class.java)
                callback(user)
            }
            .addOnFailureListener {
                callback(null)
            }
    }
}

