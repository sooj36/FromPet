package com.example.frompet.login.viewmodel

import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import java.lang.IllegalArgumentException

class LoginViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val _userId = MutableLiveData<FirebaseUser?>()
    val user: LiveData<FirebaseUser?> = _userId

    private val _loginResult = MutableLiveData<Boolean>()
    val loginResult: LiveData<Boolean> = _loginResult

    init {
        // ViewModel이 초기화될 때 현재 사용자 정보를 가져와서 _user LiveData에 설정합니다.
        _userId.value = auth.currentUser
    }


    fun singIn(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _userId.value = auth.currentUser
                    _loginResult.value = true
                } else {
                    Log.e("zzzzzzzz", "로그인 실패: ${task.exception}")
                    _userId.value = null
                    _loginResult.value = false
                }
            }
    }

    fun signUp(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    _userId.value = user
                    _loginResult.value = true

                } else {
                    Log.e("zzzzzzzz", "회원가입 실패: ${task.exception}")
                    _userId.value = null
                    _loginResult.value = false
                }
            }
    }
    fun loadUserPetProfile(callback: (String?, String?, String?) -> Unit) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val firestore = FirebaseFirestore.getInstance()
            val usersCollection = firestore.collection("User")
            val userId = currentUser.uid

            usersCollection.document(userId).get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        val petProfile = documentSnapshot.getString("petProfile")
                        val petName = documentSnapshot.getString("petName")
                        val petType = documentSnapshot.getString("petType")
                        callback(petProfile, petName, petType)
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("Firestore", "Error fetching Firestore data", exception)
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
