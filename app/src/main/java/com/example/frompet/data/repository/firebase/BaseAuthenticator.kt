package com.example.frompet.data.repository.firebase

import com.google.firebase.auth.FirebaseUser

interface BaseAuthenticator {

    suspend fun signUpWithEmailPassword(email:String , password:String) : FirebaseUser?

    suspend fun signInWithEmailPassword(email: String , password: String):FirebaseUser?

    suspend fun sigInGoogle(idToken: String):FirebaseUser?

    fun signOut() : FirebaseUser?

    fun getUser() : FirebaseUser?

    suspend fun sendPasswordReset(email :String)

}