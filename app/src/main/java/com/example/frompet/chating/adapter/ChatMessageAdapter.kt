package com.example.frompet.chating.adapter

import android.icu.text.SimpleDateFormat
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.frompet.R
import com.example.frompet.databinding.ItemChatMessageBinding
import com.example.frompet.login.data.ChatMessage
import java.util.Locale

class ChatMessageAdapter(private val currentUserId: String) : ListAdapter<ChatMessage, ChatMessageAdapter.ChatMessageViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatMessageAdapter.ChatMessageViewHolder {
        val binding = ItemChatMessageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChatMessageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChatMessageViewHolder, position: Int) {
        val chatMessage = getItem(position)
        holder.bind(chatMessage)
    }

    inner class ChatMessageViewHolder(private val binding: ItemChatMessageBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(chatMessage: ChatMessage) {
            binding.apply {
                if (chatMessage.senderId == currentUserId) {

                    tvName.text = "나"
                    tvMessage.text = chatMessage.message
                    tvMessage.setBackgroundResource(R.drawable.rightbubble)
                    tvTime.text = SimpleDateFormat("MM월dd일 hh:mm", Locale.getDefault()).format(chatMessage.timestamp)
                    binding.messageItemLinearlayoutMain.gravity = Gravity.RIGHT
                } else {

                    tvName.text = chatMessage.senderPetName
                    tvMessage.text = chatMessage.message
                    tvMessage.setBackgroundResource(R.drawable.leftbubble)
                    tvTime.text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(chatMessage.timestamp)
                    binding.messageItemLinearlayoutMain.gravity = Gravity.LEFT
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
