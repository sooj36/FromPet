package com.example.frompet.login.viewmodel

import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.frompet.login.data.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import java.lang.IllegalArgumentException

class LoginViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val _user = MutableLiveData<FirebaseUser?>()
    val user: LiveData<FirebaseUser?> = _user

    private val _loginResult = MutableLiveData<Boolean>()
    val loginResult: LiveData<Boolean> = _loginResult


    fun singIn(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _user.value = auth.currentUser
                    _loginResult.value = true

                } else {
                    Log.e("zzzzzzzz", "로그인 실패: ${task.exception}")
                    _user.value = null
                    _loginResult.value = false
                }
            }
    }

    fun signUp(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _user.value = auth.currentUser
                    _loginResult.value = true
                } else {
                    Log.e("zzzzzzzz", "회원가입 실패: ${task.exception}")
                    _user.value = null
                    _loginResult.value = false
                }
            }
    }
}

    class LoginViewModelFactory(

    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
                return LoginViewModel(
                ) as T
            } else {
                throw IllegalArgumentException("Not found ViewModel class")
            }
        }
    }
