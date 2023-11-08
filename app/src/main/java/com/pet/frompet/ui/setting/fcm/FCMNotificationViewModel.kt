package com.pet.frompet.ui.setting.fcm

import androidx.lifecycle.ViewModel
import com.pet.frompet.data.repository.fcm.FCMNotificationRepositoryImpl
//뷰모델을 확장하여 UI와 데이터 연산 사이의 중개자 역할
class FCMNotificationViewModel : ViewModel() {
    private val repository = FCMNotificationRepositoryImpl()

    //특정 사용자에게 FCM알림을 보내는 함수

    //UI에서 호출되어 사용자에게 알림을 보내는 작업(사용자 uid,타이틀,메시지 파라미터로받음)
    fun sendFCMNotification(uid: String, title: String, message: String) {
        repository.sendNotification(uid, title, message)
    }

    //상대방에게 채팅메시지 FCM알림을 보내는 함수
    fun sendFCMChatNotification(chatRoomUid: String, senderUid: String, receiverUid: String, title: String, message: String) {
        repository.sendChatNotification(chatRoomUid, senderUid, receiverUid, title, message)
    }

}


