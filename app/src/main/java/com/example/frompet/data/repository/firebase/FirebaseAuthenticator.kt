package com.example.frompet.data.repository.firebase

import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import java.lang.Exception

class FirebaseAuthenticator : BaseAuthenticator {
    override suspend fun signUpWithEmailPassword(email: String, password: String): FirebaseUser? {
        Firebase.auth.createUserWithEmailAndPassword(email,password).await()
        return Firebase.auth.currentUser
    }

    override suspend fun signInWithEmailPassword(email: String, password: String): FirebaseUser? {
        Firebase.auth.signInWithEmailAndPassword(email , password).await()
        return Firebase.auth.currentUser
    }

    override suspend fun signInGoogle(idToken: String): FirebaseUser? {
        val credential: AuthCredential = GoogleAuthProvider.getCredential(idToken, null)
        return try {
            val authResult = Firebase.auth.signInWithCredential(credential).await()
            authResult.user
        }catch (e: Exception){
            null
        }
    }

    override suspend fun signInWithCredential(credential: AuthCredential): FirebaseUser? {
        return try {
            val authResult = Firebase.auth.signInWithCredential(credential).await()
            authResult.user
        } catch (e: Exception) {
            // 실패 시 예외 처리
            null
        }
    }

    override fun signOut(): FirebaseUser? {
        Firebase.auth.signOut()
        return Firebase.auth.currentUser
    }

    override fun getUser(): FirebaseUser? {
        return Firebase.auth.currentUser
    }

    override suspend fun sendPasswordReset(email: String) {
        Firebase.auth.sendPasswordResetEmail(email).await()
    }

}