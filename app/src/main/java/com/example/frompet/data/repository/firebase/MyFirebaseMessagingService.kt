package com.example.frompet.data.repository.firebase

import FCMTokenManagerViewModel
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.frompet.MainActivity
import com.example.frompet.R
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {
    private val TAG = "FirebaseService"

    private val fcmTokenManagerViewModel = FCMTokenManagerViewModel() // FCMTokenManagerViewModel 인스턴스 생성

    override fun onNewToken(token: String) {
        Log.d(TAG, "new Token: $token")

        // 토큰 값을 따로 저장
        val pref = this.getSharedPreferences("token", Context.MODE_PRIVATE)
        val editor = pref.edit()
        editor.putString("token", token).apply()
        editor.commit()
        Log.i(TAG, "성공적으로 토큰을 저장함")

        // FCM 토큰을 ViewModel을 통해 저장
        fcmTokenManagerViewModel.saveFCMToken(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(TAG, "From: " + remoteMessage.from)

        Log.d(TAG, "Message data : ${remoteMessage.data}")
        Log.d(TAG, "Message noti : ${remoteMessage.notification}")

        if (remoteMessage.data.isNotEmpty()) {
            // 알림 생성
            sendNotification(remoteMessage)
        } else {
            Log.e(TAG, "data가 비어있습니다. 메시지를 수신하지 못했습니다.")
        }
    }

    private fun sendNotification(remoteMessage: RemoteMessage) {
        val channelId = "channelId -- 앱 마다 설정"
        val channelName = "channelName -- 앱 마다 설정"
        val channelDescription = "channelDescription -- 앱 마다 설정"
        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelId, channelName, importance).apply {
                description = channelDescription
            }
            notificationManager.createNotificationChannel(channel)
        }

        val uniId: Int = (System.currentTimeMillis() / 7).toInt()

        val intent = Intent(this, MainActivity::class.java)
        for(key in remoteMessage.data.keys){
            intent.putExtra(key, remoteMessage.data.getValue(key))
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(this, uniId, intent, PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_MUTABLE)


        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(remoteMessage.data["title"].toString())
            .setContentText(remoteMessage.data["body"].toString())
            .setAutoCancel(true)
            .setSound(soundUri)
            .setContentIntent(pendingIntent)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Notice", NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        // 추가 코드: 알림을 받았을 때 앱이 실행 중이면 알림을 처리하고, 백그라운드에 있으면 알림을 표시
        if (isAppInForeground()) {
            // 앱이 포그라운드에 있을 때 알림 처리
            handleNotificationInForeground(remoteMessage)
        } else {
            // 앱이 백그라운드에 있을 때 알림 표시
            notificationManager.notify(uniId, notificationBuilder.build())
        }
    }

    private fun handleNotificationInForeground(remoteMessage: RemoteMessage) {
        // 앱이 포그라운드에 있을 때 알림 처리 로직
        // 예를 들어, 알림을 UI에 표시하는 등의 작업을 수행할 수 있습니다.
    }

    private fun isAppInForeground(): Boolean {
        // 앱이 현재 포그라운드에 있는지 여부를 확인하는 로직
        // 예를 들어, 현재 화면이 액티비티인 경우를 포그라운드로 간주할 수 있습니다.
        // 필요한 로직에 따라 구현하세요.
        return true
    }

    fun getFirebaseToken() {
        // 비동기 방식 (추천)
        FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
            Log.d(TAG, "token=$token")
            // 토큰을 사용하여 원하는 작업을 수행
        }
    }
}