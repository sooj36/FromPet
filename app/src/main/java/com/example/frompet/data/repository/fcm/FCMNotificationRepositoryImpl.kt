package com.example.frompet.data.repository.fcm

import android.util.Log
import com.example.frompet.data.model.NotificationData
import com.example.frompet.data.model.NotificationRequest
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class FCMNotificationRepositoryImpl {
    private val database = FirebaseDatabase.getInstance().getReference()
    //특정사용자에게 알림을 보내는 함수
    fun sendNotificationToUser(uid: String, title: String, message: String) {
        //해당 사용자의 정보를 가져옵니다.
        database.child("usersToken").child(uid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val token = snapshot.child("fcmToken").value as? String
                val notificationsEnabled = snapshot.child("notificationsEnabled").value as? Boolean ?: true
                // 알림이 활성화되어 있고, 토큰이 null이 아닌 경우에만 FCM 알림을 보내는 로직
                if (notificationsEnabled && token != null) {
                    CoroutineScope(Dispatchers.IO).launch {
                        sendFCM(token, title, message)
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    //FCM서버에 알림을 보내는 함수
    private suspend fun sendFCM(token: String, title: String, message: String) {

        //retrofit을 사용하여 FCm 서버에 HTTP요청을 보내기 위한 서비스 객체생성
        val notificationService = Retrofit.Builder()
            .baseUrl("https://fcm.googleapis.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NotificationService::class.java)

        //알림데이터생성
        val notificationData = NotificationData(title, message)

        //알림 요청 본문생성(데이터페이로드와,알림페이로드 모두포함)
        val body = NotificationRequest(token, notificationData,notificationData)

        //FCM서버에 요청보내기
        val response = notificationService.sendNotification(body)
        if (response.isSuccessful) {
            Log.d("FCM","sent successfully")
        } else {
            Log.d("FCM", "Failed send : ${response.errorBody()?.string()}")
        }
    }

}