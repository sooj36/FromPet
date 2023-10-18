package com.example.frompet.chating.adapter

import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.frompet.R
import com.example.frompet.chating.ChatPullScreenActivity
import com.example.frompet.databinding.ItemChatMessageBinding
import com.example.frompet.login.data.ChatMessage
import com.example.frompet.login.data.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Locale

class ChatMessageAdapter() :
    ListAdapter<ChatMessage, ChatMessageAdapter.ChatMessageViewHolder>(DiffCallback()) {
    private val firestore = FirebaseFirestore.getInstance()
    companion object{
        const val IMAGE_URL = "image_url"
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ChatMessageAdapter.ChatMessageViewHolder {
        val binding =
            ItemChatMessageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChatMessageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChatMessageViewHolder, position: Int) {
        val chatMessage = getItem(position)
        holder.bind(chatMessage)
    }

    inner class ChatMessageViewHolder(private val binding: ItemChatMessageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(chatMessage: ChatMessage) {//뷰타입을 나누자,시간대같은거는 말풍선 하나에
            binding.apply {
                val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return
                if (chatMessage.senderId == currentUserId) {
                    tvName.text = "나"
                    tvMessage.setBackgroundResource(R.drawable.rightbubble)
                    ivMessageImage.setBackgroundResource(R.drawable.rightbubble)
                    tvTime.text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(chatMessage.timestamp)
                    messageItemLinearlayoutMain.gravity = Gravity.END
                    ivProfile.visibility = View.GONE
                } else {
                    tvName.text = chatMessage.senderPetName
                    tvMessage.setBackgroundResource(R.drawable.leftbubble)
                    ivMessageImage.setBackgroundResource(R.drawable.leftbubble)
                    tvTime.text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(chatMessage.timestamp)
//                    System.currentTimeMillis() 이걸로 보내자 Long타입으로 사용자에게 보여줄때는 사용자의 현재시간으로
                    messageItemLinearlayoutMain.gravity = Gravity.START
                    ivProfile.visibility = View.VISIBLE

                    firestore.collection("User").document(chatMessage.senderId)//프로필이미지를 어딘가에 저장해놨다가 if있으면 갖다쓰고 else없으면 다시불러오고
                        .get()
                        .addOnSuccessListener { document ->
                            val user = document.toObject(UserModel::class.java)
                            user?.petProfile?.let {
                                binding.ivProfile.load(it) {
                                    error(R.drawable.kakaotalk_20230825_222509794_01)
                                }
                            }
                        }
                }
                if (!chatMessage.imageUrl.isNullOrEmpty()) {
                    ivMessageImage.visibility = View.VISIBLE
                    tvMessage.visibility = View.GONE
                    ivMessageImage.load(chatMessage.imageUrl) {
                        error(R.drawable.kakaotalk_20230825_222509794_01)
                    }


                    ivMessageImage.setOnClickListener {
                        val intent = Intent(it.context, ChatPullScreenActivity::class.java)
                        intent.putExtra(IMAGE_URL, chatMessage.imageUrl)
                        it.context.startActivity(intent)
                    }

                } else {
                    ivMessageImage.visibility = View.GONE
                    tvMessage.visibility = View.VISIBLE
                    tvMessage.text = chatMessage.message

                }
            }
        }
    }



    class DiffCallback : DiffUtil.ItemCallback<ChatMessage>() {
        override fun areItemsTheSame(oldItem: ChatMessage, newItem: ChatMessage): Boolean {
            return oldItem.timestamp == newItem.timestamp
        }

        override fun areContentsTheSame(oldItem: ChatMessage, newItem: ChatMessage): Boolean {
            return oldItem == newItem
        }
    }
}
