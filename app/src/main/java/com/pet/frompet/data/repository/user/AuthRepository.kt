package com.pet.frompet.data.repository.user

import com.pet.frompet.data.repository.firebase.BaseAuthenticator
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val authenticator: BaseAuthenticator,
    private val firestore: FirebaseFirestore

): BaseAuthRepository {
    override suspend fun signInWithEmailPassword(email: String, password: String): FirebaseUser? {
        return authenticator.signInWithEmailPassword(email , password)
    }

    override suspend fun signUpWithEmailPassword(email: String, password: String): FirebaseUser? {
        return authenticator.signUpWithEmailPassword(email , password)
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

    override suspend fun isAlreadyLoggedIn(): Boolean {
        val user = authenticator.getUser()
        return user != null
    }

    override suspend fun signInGoogle(idToken: String): FirebaseUser? {
        return authenticator.signInGoogle(idToken)
    }

    override suspend fun signInWithCredential(credential: AuthCredential): FirebaseUser? {
        return authenticator.signInWithCredential(credential)
    }

}