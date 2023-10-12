package com.example.frompet.login.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.frompet.login.data.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class LoginViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val _user = MutableLiveData<FirebaseUser?>()
    val user: LiveData<FirebaseUser?> = _user

    fun singIn(email:String,password:String){
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if(task.isSuccessful){
                    _user.value = auth.currentUser
                }else{
                    _user.value = null
                }
            }
    }

    fun signUp(){

    }
}