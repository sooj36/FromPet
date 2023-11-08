package com.pet.frompet.ui.chat.adapter

import android.content.Context
import android.content.Intent
import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.pet.frompet.R
import com.pet.frompet.data.model.ChatMessage
import com.pet.frompet.data.model.User
import com.pet.frompet.databinding.ItemOtherMessageBinding
import com.pet.frompet.ui.chat.activity.ChatClickUserDetailActivity
import com.pet.frompet.ui.chat.activity.ChatPullScreenActivity
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class OtherMessageViewHolder(
    private val binding: ItemOtherMessageBinding,
    private val context: Context
) : RecyclerView.ViewHolder(binding.root) {

    private val firestore = FirebaseFirestore.getInstance()

    fun bind(chatMessage: ChatMessage) = with(binding) {
        if (chatMessage.imageUrl.isNullOrEmpty().not()) {
            ivMessageImage.isVisible = true
            tvMessage.isVisible = false
            tvName.text = chatMessage.senderPetName
            tvTime.text = formatTimeStamp(chatMessage.timestamp)
            ivMessageImage.load(chatMessage.imageUrl) {
                error(R.drawable.kakaotalk_20230825_222509794_01)
            }
            clickListener(ivMessageImage, chatMessage.imageUrl)
        } else {
            ivMessageImage.isVisible = false
            tvMessage.isVisible = true
            tvName.text = chatMessage.senderPetName
            tvMessage.setBackgroundResource(R.drawable.chat1)
            tvMessage.text = chatMessage.message
            tvTime.text = formatTimeStamp(chatMessage.timestamp)
        }

        firestore.collection("User").document(chatMessage.senderId).get().addOnSuccessListener { document ->
            val user = document.toObject(User::class.java)
            user?.petProfile?.let {
                ivProfile.load(it) {
                    error(R.drawable.kakaotalk_20230825_222509794_01)
                }
                ivProfile.setOnClickListener { userDetail(user) }
            }
        }
    }

    private fun userDetail(user: User) {
        val intent = Intent(context, ChatClickUserDetailActivity::class.java)
        intent.putExtra(ChatClickUserDetailActivity.USER, user)
        context.startActivity(intent)
    }

    private fun formatTimeStamp(timestamp: Long): String {
        val sdf = SimpleDateFormat("a HH:mm", Locale.KOREA)
        sdf.timeZone = TimeZone.getTimeZone("Asia/Seoul")
        return sdf.format(Date(timestamp))
    }
    private fun clickListener(imageView: View, imageUrl: String?) {
        imageView.setOnClickListener {
            val intent = Intent(it.context, ChatPullScreenActivity::class.java)
            intent.putExtra(ChatMessageAdapter.IMAGE_URL, imageUrl)
            it.context.startActivity(intent)
        }
    }
}
