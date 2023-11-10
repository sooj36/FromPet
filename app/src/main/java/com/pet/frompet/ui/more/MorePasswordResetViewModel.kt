package com.pet.frompet.ui.more

import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import com.pet.frompet.data.repository.user.UserRepositoryImp
import com.pet.frompet.ui.login.LoginViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MorePasswordResetViewModel @Inject constructor(
    private val userRepository: UserRepositoryImp,
    var mGoogleSignInClient: GoogleSignInClient,
    private val auth: FirebaseAuth
): ViewModel() {
    companion object {
        private const val TAG = "랄라라"
    }

    private val eventsChannel = Channel<LoginViewModel.AllEvents>()
    val allEventsFlow = eventsChannel.receiveAsFlow()


    fun verifySendPasswordReset(email: String) {
        if (email.isEmpty()) {
            viewModelScope.launch {
                eventsChannel.send(LoginViewModel.AllEvents.ErrorCode(1))
            }
        } else {
            sendPasswordResetEmail(email)
        }
    }

    private fun sendPasswordResetEmail(email: String) = viewModelScope.launch {
        try {
            val result = userRepository.sendResetPassword(email)
            if (result) {
                eventsChannel.send(LoginViewModel.AllEvents.Message("이메일로 전송 되었습니다"))
            } else {
                eventsChannel.send(LoginViewModel.AllEvents.Error("일치하는 이메일이 없습니다."))
            }
        } catch (e: Exception) {
            val error = e.toString().split(":").toTypedArray()
            eventsChannel.send(LoginViewModel.AllEvents.Error(error[1]))
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
        data class GoogleSignIn(val error: Intent):AllEvents()
    }
}