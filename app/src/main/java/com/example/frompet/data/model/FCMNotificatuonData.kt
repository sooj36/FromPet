package com.example.frompet.data.model
//모델 클래스로 알림의 제목과 본문
data class NotificationData(
    val title: String,      //알림의 제목
    val body: String)      //알림의 본문내용

//FCM 메시지를 전송하기 위한 요청모델

data class NotificationRequest(
    val to: String,                //메시지를 전송할 대상의 FCM토큰
    val data: NotificationData,        //데이터페이로드,
    val notification: NotificationData)        //알림 페이로드 시스템에서 자동으로 알림표시, 즉 이게 있어야지 알림에 대한 정보가 표시댐 UI에 자동으로