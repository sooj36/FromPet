package com.example.frompet.ui.chat.adapter

import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.icu.util.TimeZone
import android.view.View
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.frompet.R
import com.example.frompet.data.model.ChatMessage
import com.example.frompet.databinding.ItemMyMessageBinding
import coil.load
import com.example.frompet.data.model.User
import com.example.frompet.ui.chat.activity.ChatPullScreenActivity
import com.example.frompet.ui.chat.viewmodel.MessageViewModel
import com.example.frompet.ui.setting.fcm.FCMNotificationViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Date
import java.util.Locale

class MyMessageViewHolder(private val binding: ItemMyMessageBinding,
                          private val fcmViewModel: FCMNotificationViewModel,
                          private val messageViewModel: MessageViewModel,
                          private val user: User
)
    : RecyclerView.ViewHolder(binding.root) {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val chatRoomId = messageViewModel.chatRoom(auth.currentUser?.uid?:"", user.uid)



    fun bind(chatMessage: ChatMessage) = with(binding) {
        if (chatMessage.imageUrl.isNullOrEmpty().not()) {
            ivMessageImage.isVisible = true
            tvMessage.isVisible = false
            tvTime.text = formatTimeStamp(chatMessage.timestamp)
            ivMessageImage.load(chatMessage.imageUrl) {
                error(R.drawable.kakaotalk_20230825_222509794_01)
            }
            clickListener(ivMessageImage, chatMessage.imageUrl)
            firestore.collection("User").document(auth.currentUser?.uid?:"").get()
                .addOnSuccessListener { docs->
                    val currentUserName = docs.getString("petName")?:"알 수 없음"
                    val title = "${currentUserName}님에게 새로운 메시지"
                    fcmViewModel.sendFCMChatNotification(
                        chatRoomId,
                        auth.currentUser?.uid ?: "",
                        chatMessage.receiverId,
                        title,
                        "(사진)"
                    )
                }

        } else {
            ivMessageImage.isVisible = false
            tvMessage.isVisible = true
            tvMessage.setBackgroundResource(R.drawable.chat2)
            tvMessage.text = chatMessage.message
            tvTime.text = formatTimeStamp(chatMessage.timestamp)
        }
    }

    private fun clickListener(imageView: View, imageUrl: String?) {
        imageView.setOnClickListener {
            val intent = Intent(it.context, ChatPullScreenActivity::class.java)
            intent.putExtra(ChatMessageAdapter.IMAGE_URL, imageUrl)
            it.context.startActivity(intent)
        }
    }

    private fun formatTimeStamp(timestamp:Long):String{
        val sdf = SimpleDateFormat("a HH:mm", Locale.KOREA)
        sdf.timeZone = TimeZone.getTimeZone("Asia/Seoul")
        return sdf.format(Date(timestamp))
    }
}
