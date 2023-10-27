package com.example.frompet.ui.chat.adapter

import android.content.Context
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.icu.util.TimeZone
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.frompet.R
import com.example.frompet.data.model.ChatItem
import com.example.frompet.ui.chat.activity.ChatClickUserDetailActivity
import com.example.frompet.ui.chat.activity.ChatPullScreenActivity

import com.example.frompet.databinding.ItemMyMessageBinding
import com.example.frompet.databinding.ItemOtherMessageBinding
import com.example.frompet.data.model.ChatMessage
import com.example.frompet.data.model.User
import com.example.frompet.databinding.ItemDateHeaderBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Date
import java.util.Locale


class ChatMessageAdapter(var context: Context) :
    ListAdapter<ChatItem, RecyclerView.ViewHolder>(DiffCallback()) {

    private val firestore = FirebaseFirestore.getInstance()

    companion object {
        const val IMAGE_URL = "image_url"
        const val VIEW_TYPE_DATE_HEADER = 0
        const val VIEW_TYPE_MY_MESSAGE = 1
        const val VIEW_TYPE_OTHER_MESSAGE = 2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_DATE_HEADER -> {
                val binding = ItemDateHeaderBinding.inflate(inflater, parent, false)
                DateHeaderViewHolder(binding)
            }
            VIEW_TYPE_MY_MESSAGE -> {
                val binding = ItemMyMessageBinding.inflate(inflater, parent, false)
                MyMessageViewHolder(binding)
            }
            VIEW_TYPE_OTHER_MESSAGE -> {
                val binding = ItemOtherMessageBinding.inflate(inflater, parent, false)
                OtherMessageViewHolder(binding)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is ChatItem.DateHeader -> (holder as DateHeaderViewHolder).bind(item.date)
            is ChatItem.MessageItem -> {
                val chatMessage = item.chatMessage
                when (holder) {
                    is MyMessageViewHolder -> holder.bind(chatMessage)
                    is OtherMessageViewHolder -> holder.bind(chatMessage)
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is ChatItem.DateHeader -> VIEW_TYPE_DATE_HEADER
            is ChatItem.MessageItem -> {
                val chatMessage = (getItem(position) as ChatItem.MessageItem).chatMessage
                val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
                if (chatMessage.senderId == currentUserId) {
                    VIEW_TYPE_MY_MESSAGE
                } else {
                    VIEW_TYPE_OTHER_MESSAGE
                }
            }
        }
    }

    inner class DateHeaderViewHolder(private val binding: ItemDateHeaderBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(date: String) {
            binding.tvDateHeader.text = date
        }
    }


    inner class MyMessageViewHolder(private val binding: ItemMyMessageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(chatMessage: ChatMessage) = with(binding) {
            if (chatMessage.imageUrl.isNullOrEmpty().not()) {
                ivMessageImage.isVisible = true
                tvMessage.isVisible = false
                tvTime.text = formatTimeStamp(chatMessage.timestamp)
                ivMessageImage.load(chatMessage.imageUrl) {
                    error(R.drawable.kakaotalk_20230825_222509794_01)
                }
                clickListener(ivMessageImage, chatMessage.imageUrl)
            } else {
                ivMessageImage.isVisible = false
                tvMessage.isVisible = true
                tvMessage.setBackgroundResource(R.drawable.chat2)
                tvMessage.text = chatMessage.message
                tvTime.text =
                    formatTimeStamp(chatMessage.timestamp)
            }
        }
    }

    inner class OtherMessageViewHolder(private val binding: ItemOtherMessageBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(chatMessage: ChatMessage) = with(binding) {
            if (chatMessage.imageUrl.isNullOrEmpty().not()) {
                ivMessageImage.isVisible = true
                tvMessage.isVisible = false
                tvName.text =chatMessage.senderPetName
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
                tvTime.text =
                    formatTimeStamp(chatMessage.timestamp)
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
    }



    class DiffCallback : DiffUtil.ItemCallback<ChatItem>() {
        override fun areItemsTheSame(oldItem: ChatItem, newItem: ChatItem): Boolean {
            return when {
                oldItem is ChatItem.DateHeader && newItem is ChatItem.DateHeader -> oldItem.date == newItem.date
                oldItem is ChatItem.MessageItem && newItem is ChatItem.MessageItem -> oldItem.chatMessage.timestamp == newItem.chatMessage.timestamp
                else -> false
            }
        }

        override fun areContentsTheSame(oldItem: ChatItem, newItem: ChatItem): Boolean {
            return oldItem == newItem
        }
    }


    private fun clickListener(imageView: View, imageUrl: String?) {
        imageView.setOnClickListener {
            val intent = Intent(it.context, ChatPullScreenActivity::class.java)
            intent.putExtra(IMAGE_URL, imageUrl)
            it.context.startActivity(intent)
        }
    }

    private fun userDetail(user: User) {
        val intent = Intent(context, ChatClickUserDetailActivity::class.java)
        intent.putExtra(ChatClickUserDetailActivity.USER, user)
        context.startActivity(intent)
    }
    private fun formatTimeStamp(timestamp:Long):String{
        val sdf = SimpleDateFormat("a HH:mm",Locale.KOREA)
        sdf.timeZone = TimeZone.getTimeZone("Asia/Seoul")
        return sdf.format(Date(timestamp))
    }
}