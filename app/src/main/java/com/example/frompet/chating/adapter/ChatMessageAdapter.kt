package com.example.frompet.chating.adapter

import android.icu.text.SimpleDateFormat
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.frompet.R
import com.example.frompet.databinding.ItemChatMessageBinding
import com.example.frompet.login.data.ChatMessage
import com.example.frompet.login.data.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Locale

class ChatMessageAdapter() :
    ListAdapter<ChatMessage, ChatMessageAdapter.ChatMessageViewHolder>(DiffCallback()) {
    private val firestore = FirebaseFirestore.getInstance()

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
        fun bind(chatMessage: ChatMessage) {
            binding.apply {
                val  currentUserId = FirebaseAuth.getInstance().currentUser?.uid?:return
                if (chatMessage.senderId == currentUserId) {

                    tvName.text = "나"
                    tvMessage.text = chatMessage.message
                    tvMessage.setBackgroundResource(R.drawable.rightbubble)
                    tvTime.text =
                        SimpleDateFormat("HH:mm", Locale.getDefault()).format(chatMessage.timestamp)
                    binding.apply {  messageItemLinearlayoutMain.gravity = Gravity.RIGHT
                    ivProfile.visibility= View.GONE}
                } else {

                    tvName.text = chatMessage.senderPetName
                    tvMessage.text = chatMessage.message
                    tvMessage.setBackgroundResource(R.drawable.leftbubble)
                    tvTime.text =
                        SimpleDateFormat("HH:mm", Locale.getDefault()).format(chatMessage.timestamp)
                    binding.messageItemLinearlayoutMain.gravity = Gravity.LEFT
                    firestore.collection("User").document(chatMessage.senderId)
                        .get()
                        .addOnSuccessListener { document ->
                            val user = document.toObject(UserModel::class.java)
                            user?.petProfile.let {
                                binding.ivProfile.load(it) {
                                    error(R.drawable.kakaotalk_20230825_222509794_01)
                                }
                            }
                        }
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
