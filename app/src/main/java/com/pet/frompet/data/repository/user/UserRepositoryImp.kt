package com.pet.frompet.data.repository.user

import com.pet.frompet.data.model.User
import com.pet.frompet.data.repository.firebase.BaseAuthenticator
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
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

    override suspend fun deleteAccount(): Boolean {
        return authenticator.deleteAccount()
    }

    override suspend fun sendResetPassword(email: String): Boolean {
        authenticator.sendPasswordReset(email)
        return true
    }

    override suspend fun isAlreadyLoggedIn(): Boolean {
        val user = authenticator.getUser()
        return user != null
    }

    override suspend fun signInGoogle(idToken: String): FirebaseUser? {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            authenticator.signInWithCredential(credential)
        } catch (e: Exception) {
            // 실패 시 예외 처리
            null
        }
    }

    override suspend fun signInWithCredential(credential: AuthCredential): FirebaseUser? {
        return try {
            val authResult = FirebaseAuth.getInstance().signInWithCredential(credential).await()
            authResult.user
        } catch (e: Exception) {
            // 실패 시 예외 처리
            null
        }
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

