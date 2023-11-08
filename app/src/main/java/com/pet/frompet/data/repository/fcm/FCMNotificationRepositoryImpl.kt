package com.pet.frompet.data.repository.fcm

import android.util.Log
import com.pet.frompet.data.model.NotificationData
import com.pet.frompet.data.model.NotificationRequest
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
    fun sendNotification(uid: String, title: String, message: String) {
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

    fun sendChatNotification(chatRoomUid: String, senderUid: String, receiverUid: String, title: String, message: String) {
        database.child("chatRooms").child(chatRoomUid).child("users").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                //서로 채팅방 상태가져옴 true인지 false인지
                val senderStatus = snapshot.child(senderUid).child("status").value as? Boolean ?: false
                val receiverStatus = snapshot.child(receiverUid).child("status").value as? Boolean ?: false
                //부정연산자로 상태를 체크한다음에 실제 true로 들어와있어도 서로 false바꿔주기때문에 둘다 false면  || 실행안되서 메시지안감 나머지는 한개라도 true면 ||이게 true로 되기때문에 메시지감
                if (!senderStatus || !receiverStatus) {
                    // 상대방의 알림 설정 확인
                    database.child("usersToken").child(receiverUid).child("chatNotificationsEnabled").addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val chatNotificationsEnabled = snapshot.value as? Boolean ?: true
                            // 알림이 활성화되어 있으면 알림을 보냄
                            if (chatNotificationsEnabled) {
                                sendNotification(receiverUid, title, message)
                            }
                        }
                        override fun onCancelled(error: DatabaseError) {}
                    })
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