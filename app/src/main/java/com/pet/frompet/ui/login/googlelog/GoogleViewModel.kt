package com.pet.frompet.ui.login.googlelog

import android.content.Intent
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pet.frompet.data.repository.user.UserRepositoryImp
import com.pet.frompet.ui.intro.IntroActivity.Companion.TAG
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.gun0912.tedpermission.provider.TedPermissionProvider.context
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GoogleViewModel @Inject constructor(
    private val userRepository: UserRepositoryImp,
    var mGoogleSignInClient: GoogleSignInClient,
    private val auth: FirebaseAuth
): ViewModel() {
    private val _firebaseUser = MutableLiveData<FirebaseUser?>()
    val currentUser get() = _firebaseUser

    private val eventsChannel = Channel<AllEvents>()
    val allEventsFlow = eventsChannel.receiveAsFlow()

    fun signInGoogle(idToken: String) = viewModelScope.launch{
        if (idToken.isNotEmpty()) {
            try {
                Log.e(TAG,"google log st")
                val user = userRepository.signInGoogle(idToken)
                user?.let {
                    val uid = it.uid
                    Log.e(TAG, "User UID: $uid")
                    _firebaseUser.postValue(it)
                    eventsChannel.send(AllEvents.Message("Google로 로그인 되었습니다."))
                    uid?.let {
                        saveGoogle(uid)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error during Google login", e)
                eventsChannel.send(AllEvents.Error("Google 로그인 중 오류가 발생"))
            }
        } else {
            // idToken이 비어있는 경우에 대한 처리
            eventsChannel.send(AllEvents.Error("Google 로그인 중 오류가 발생"))
        }
    }

    private fun saveGoogle(uid: String){
        val userDocRef = FirebaseFirestore.getInstance().collection("User").document(uid)

        // 사용자 정보를 가져올 GoogleSignInAccount
        val acct = GoogleSignIn.getLastSignedInAccount(context)

        if (acct != null) {
            val userData = hashMapOf(
                "username" to acct.displayName,
                "email" to acct.email

            )

            userDocRef.set(userData)
                .addOnSuccessListener {
                    Log.e(TAG, "Success")
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Fail: $e")
                }
        } else {
            Log.e(TAG, "Google SignInAccount is null")
        }
    }

    sealed class AllEvents {
        data class Message(val message: String) : AllEvents()
        data class ErrorCode(val code: Int) : AllEvents()
        data class Error(val error: String) : AllEvents() {
            fun getKoreanMessage(): String {
                if (error == "INVALID_LOGIN_CREDENTIALS") {
                    return "계정 정보가 잘못되었습니다."
                } else if (error == "NO_SUCH_USER") {
                    return "해당 계정이 존재하지 않습니다."
                } else {
                    return "이메일 혹은 비밀번호를 확인하세요"
                }
            }
        }
        data class GoogleSignIn(val error: Intent): AllEvents()
    }

}
