package com.example.frompet.ui.login

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.frompet.data.repository.user.UserRepositoryImp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginupViewModel @Inject constructor(
    private val userRepository: UserRepositoryImp,
    private val auth: FirebaseAuth
):ViewModel() {

    private val _firebaseUser = MutableLiveData<FirebaseUser?>()
    val currentUser get() = _firebaseUser

    private val _loginResult = MutableLiveData<Boolean>()
    val loginResult: LiveData<Boolean> get() = _loginResult

    private val eventsChannel = Channel<AllEvents>()
    val allEventsFlow = eventsChannel.receiveAsFlow()

    init {
        // ViewModel이 초기화될 때 현재 사용자 정보를 가져와서 _user LiveData에 설정합니다.
        _firebaseUser.value = auth.currentUser
    }

    fun signInUser(email: String, password: String) = viewModelScope.launch {
        when {
            email.isEmpty() -> {
                eventsChannel.send(AllEvents.ErrorCode(1))
            }
            password.isEmpty() -> {
                eventsChannel.send(AllEvents.ErrorCode(2))
            }
            else -> {
                actualSignInUser(email , password)
            }
        }
    }
    private fun actualSignInUser(email:String, password: String) = viewModelScope.launch {
        try {
            val user = userRepository.signInWithEmailPassword(email, password)
            user?.let {
                _firebaseUser.postValue(it)
                eventsChannel.send(AllEvents.Message("로그인 성공"))
            }
        }catch(e:Exception){
            val error = e.toString().split(":").toTypedArray()
            Log.d(TAG, "signInUser: ${error[1]}")
            eventsChannel.send(AllEvents.Error(error[1]))
        }
    }
    fun signUpUser(email: String, password: String, confirmPass: String) = viewModelScope.launch {
        when {
            email.isEmpty() -> {
                eventsChannel.send(AllEvents.ErrorCode(1))
            }

            password.isEmpty() -> {
                eventsChannel.send(AllEvents.ErrorCode(2))
            }


            password != confirmPass -> {
                eventsChannel.send(AllEvents.ErrorCode(3))
            }

            else -> {
                actualSignUpUser(email, password)
                }
            }
        }

    private fun actualSignUpUser(email:String , password: String) = viewModelScope.launch {
        try {
            val user = userRepository.signUpWithEmailPassword(email, password)
            user?.let {
                _firebaseUser.postValue(it)
                eventsChannel.send(AllEvents.Message("회원가입 성공"))
            }
        }catch(e:Exception){
            val error = e.toString().split(":").toTypedArray()
            Log.d(TAG, "signInUser: ${error[1]}")
            eventsChannel.send(AllEvents.Error(error[1]))
        }
    }

    fun signOut() = viewModelScope.launch {
        try {
            val user = userRepository.signOut()
            user?.let {
                eventsChannel.send(AllEvents.Message("로그아웃 실패"))
            } ?: eventsChannel.send(AllEvents.Message("로그아웃 성공"))
            getCurrentUser()
        } catch (e: Exception) {
            val error = e.toString().split(":").toTypedArray()
            eventsChannel.send(AllEvents.Error(error[1]))
        }
    }

    private fun getCurrentUser() = viewModelScope.launch {
        val user = userRepository.getCurrentUser()
        _firebaseUser.postValue(user)
    }

    fun verifySendPasswordReset(email: String) {
        if (email.isEmpty()) {
            viewModelScope.launch {
                eventsChannel.send(AllEvents.ErrorCode(1))
            }
        } else {
            sendPasswordResetEmail(email)
        }
    }

    private fun sendPasswordResetEmail(email: String) = viewModelScope.launch {
        try {
            val result = userRepository.sendResetPassword(email)
            if (result) {
                eventsChannel.send(AllEvents.Message("이메일로 전송 되었습니다"))
            } else {
                eventsChannel.send(AllEvents.Error("일치하는 이메일이 없습니다."))
            }
        } catch (e: Exception) {
            val error = e.toString().split(":").toTypedArray()
            eventsChannel.send(AllEvents.Error(error[1]))
        }

    }


    sealed class AllEvents {
        data class Message(val message: String) : AllEvents()
        data class ErrorCode(val code: Int) : AllEvents()
        data class Error(val error: String) : AllEvents()
    }
}
