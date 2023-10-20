package com.example.frompet.data.repository.user

import com.example.frompet.data.model.User
import com.example.frompet.data.repository.firebase.BaseAuthenticator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestoreSettings
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserRepositoryImp @Inject constructor(
    private val authenticator: BaseAuthenticator,
    private val firestore: FirebaseFirestore
):BaseAuthRepository {
    override suspend fun signInWithEmailPassword(email: String, password: String): FirebaseUser? {
        return authenticator.signInWithEmailPassword(email, password)
    }

    override suspend fun signUpWithEmailPassword(email: String, password: String): FirebaseUser? {
        return authenticator.signUpWithEmailPassword(email, password)
    }

    override fun signOut(): FirebaseUser? {
        return authenticator.signOut()
    }

    override fun getCurrentUser(): FirebaseUser? {
        return authenticator.getUser()
    }
    override suspend fun sendResetPassword(email: String): Boolean {
        authenticator.sendPasswordReset(email)
        return true
    }

    suspend fun saveUserProfile(user:User){
        val userDocument = firestore.collection("User").document(user.uid)
        userDocument.set(user)
            .addOnSuccessListener {
            }
            .addOnFailureListener {

            }
    }

    suspend fun getUserProfile(uid: String): User?{
        val userDocument = firestore.collection("User").document(uid)
        val doucumentSnapshot = userDocument.get().await()
        return doucumentSnapshot.toObject(User::class.java)
    }

}

