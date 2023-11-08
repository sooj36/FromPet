package com.pet.frompet.ui.setting.fcm

import android.util.Log
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.database.FirebaseDatabase
//FCM 토큰을 관리하는 클래스
class FCMTokenManager {

    private val database = FirebaseDatabase.getInstance().getReference()
    //현재 앱의 인스턴스의 FCM토큰을 가져오는 함수
    fun fetchAndBaseFCMToken(uid: String) {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.d("FCM", "token failed", task.exception)
                return@addOnCompleteListener
            }
            val token = task.result
            Log.d("FCM", "FCM Token: $token")

            //리얼타임 베이스에에 토큰 저장
            tokenToDatabase(uid, token)
        }
    }
    //사용자의 uid와 Fcm토큰을 리얼타임에 저장하는 함수,상태도 저장함(알림을 받을 수 있는 상태)
    fun tokenToDatabase(uid: String, token: String?) {
        if (token != null) {
            database.child("usersToken").child(uid).child("fcmToken").setValue(token)
        }
    }
    fun removeFCMToken(userId: String) {
        database.child(userId).removeValue()
            .addOnCompleteListener(OnCompleteListener<Void> { task ->
                if (task.isSuccessful) {
                    println("FCM 토큰이 성공적으로 제거되었습니다.")
                } else {
                    println("FCM 토큰 제거 중 오류 발생: ${task.exception}")
                }
            })
    }
}