package com.pet.frompet.data.repository.firebase

import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseUser

interface BaseAuthenticator {

    suspend fun signUpWithEmailPassword(email:String , password:String) : FirebaseUser?

    suspend fun signInWithEmailPassword(email: String , password: String):FirebaseUser?

    suspend fun signInGoogle(idToken: String): FirebaseUser?

    suspend fun signInWithCredential(credential: AuthCredential): FirebaseUser?

    suspend fun deleteAccount(): Boolean
    fun signOut() : FirebaseUser?

    fun getUser() : FirebaseUser?

    suspend fun sendPasswordReset(email :String)

}