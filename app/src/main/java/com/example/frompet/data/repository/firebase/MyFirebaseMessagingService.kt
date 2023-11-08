//package com.example.frompet.data.repository.firebase
//
//import FCMTokenManagerViewModel
//import android.app.Notification
//import android.app.NotificationChannel
//import android.app.NotificationManager
//import android.app.PendingIntent
//import android.content.Context
//import android.content.Intent
//import android.media.RingtoneManager
//import android.os.Build
//import android.util.Log
//import androidx.core.app.NotificationCompat
//import com.example.frompet.MainActivity
//import com.example.frompet.R
//import com.google.firebase.messaging.FirebaseMessaging
//import com.google.firebase.messaging.FirebaseMessagingService
//import com.google.firebase.messaging.RemoteMessage
//
//class MyFirebaseMessagingService : FirebaseMessagingService() {
//    private val TAG = "FirebaseService"
//
//    override fun onMessageReceived(remoteMessage: RemoteMessage) {
//        Log.d(TAG, "From: " + remoteMessage.from)
//
//        Log.d(TAG, "Message data: ${remoteMessage.data}")
//        Log.d(TAG, "Message notification: ${remoteMessage.notification}")
//
//        if (remoteMessage.data.isNotEmpty()) {
//            // 알림 생성
//            sendNotification(remoteMessage.data)
//        } else {
//            Log.e(TAG, "데이터가 비어 있습니다. 메시지를 수신하지 못했습니다.")
//        }
//    }
//
//    private fun sendNotification(data: Map<String, String>) {
//        val channelId = "my_channel_id" // 채널 ID 설정
//        val channelName = "My Channel" // 채널 이름 설정
//        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
//        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val importance = NotificationManager.IMPORTANCE_HIGH
//            val channel = NotificationChannel(channelId, channelName, importance)
//            notificationManager.createNotificationChannel(channel)
//        }
//
//        val uniqueId: Int = (System.currentTimeMillis() / 7).toInt()
//
//        val intent = Intent(this, MainActivity::class.java)
//        data.forEach { (key, value) ->
//            intent.putExtra(key, value)
//        }
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//
//        val pendingIntent = PendingIntent.getActivity(this, uniqueId, intent, PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_MUTABLE)
//
//        val notificationBuilder = NotificationCompat.Builder(this, channelId)
//            .setPriority(NotificationCompat.PRIORITY_HIGH)
//            .setSmallIcon(R.mipmap.ic_launcher)
//            .setContentTitle(data["title"])
//            .setContentText(data["body"])
//            .setAutoCancel(true)
//            .setSound(soundUri)
//            .setContentIntent(pendingIntent)
//
//        // 앱이 포그라운드에 있는지 여부를 확인하여 처리
//        if (isAppInForeground()) {
//            // 앱이 포그라운드에 있을 때 알림 처리
//            handleNotificationInForeground(notificationBuilder.build())
//        } else {
//            // 앱이 백그라운드에 있을 때 알림 표시
//            notificationManager.notify(uniqueId, notificationBuilder.build())
//        }
//    }
//
//    private fun handleNotificationInForeground(notification: Notification) {
//        // 알림 처리 로직을 구현해야함
//    }
//
//    private fun isAppInForeground(): Boolean {
//        // 앱이 현재 포그라운드에 있는지 여부를 확인하는 로직을 구현해야함
//        return true
//    }
//}