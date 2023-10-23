package com.example.frompet.ui.chat.adapter

import android.content.Context
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.frompet.R
import com.example.frompet.ui.chat.ChatClickUserDetailActivity
import com.example.frompet.ui.chat.ChatPullScreenActivity

import com.example.frompet.databinding.ItemMyMessageBinding
import com.example.frompet.databinding.ItemOtherMessageBinding
import com.example.frompet.data.model.ChatMessage
import com.example.frompet.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Date
import java.util.Locale


class ChatMessageAdapter(var context: Context) :
    ListAdapter<ChatMessage, RecyclerView.ViewHolder>(DiffCallback()) {

    private val firestore = FirebaseFirestore.getInstance()

    companion object {
        const val IMAGE_URL = "image_url"
        const val VIEW_TYPE_MY_MESSAGE = 1
        const val VIEW_TYPE_OTHER_MESSAGE = 2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_MY_MESSAGE -> {
                val binding = ItemMyMessageBinding.inflate(inflater, parent, false)
                MyMessageViewHolder(binding)
            }

            VIEW_TYPE_OTHER_MESSAGE -> {
                val binding = ItemOtherMessageBinding.inflate(inflater, parent, false)
                OtherMessageViewHolder(binding)
            }

            else -> throw IllegalArgumentException("")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val chatMessage = getItem(position)
        when (holder) {
            is MyMessageViewHolder -> holder.bind(chatMessage)
            is OtherMessageViewHolder -> holder.bind(chatMessage)
        }
    }

    override fun getItemViewType(position: Int): Int {
        val chatMessage = getItem(position)
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
        return if (chatMessage.senderId == currentUserId) {
            VIEW_TYPE_MY_MESSAGE
        } else {
            VIEW_TYPE_OTHER_MESSAGE
        }
    }

    inner class MyMessageViewHolder(private val binding: ItemMyMessageBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(chatMessage: ChatMessage) = with(binding) {
            tvMessage.setBackgroundResource(R.drawable.chat2)

//            ivMessageImage.setBackgroundResource(R.drawable.chat2)

            tvMessage.text = chatMessage.message
            tvTime.text =
                SimpleDateFormat("a HH:mm", Locale.KOREA).format(Date(chatMessage.timestamp))

            if (chatMessage.imageUrl.isNullOrEmpty().not()) {
                ivMessageImage.isVisible = true
                tvMessage.isVisible = false
                ivMessageImage.load(chatMessage.imageUrl) {
                    error(R.drawable.kakaotalk_20230825_222509794_01)
                }
                clickListener(ivMessageImage, chatMessage.imageUrl)
            } else {
                ivMessageImage.isVisible = false
            }
        }
    }
    inner class OtherMessageViewHolder(private val binding: ItemOtherMessageBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(chatMessage: ChatMessage) = with(binding) {
            tvName.text = chatMessage.senderPetName
            tvMessage.setBackgroundResource(R.drawable.chat1)

//            ivMessageImage.setBackgroundResource(R.drawable.chat1)


            tvMessage.text = chatMessage.message
            tvTime.text = SimpleDateFormat("a HH:mm", Locale.KOREA).format(Date(chatMessage.timestamp))

            if (chatMessage.imageUrl.isNullOrEmpty().not()) {
                ivMessageImage.isVisible = true
                tvMessage.isVisible = false
                ivMessageImage.load(chatMessage.imageUrl) {
                    error(R.drawable.kakaotalk_20230825_222509794_01)
                }
                clickListener(ivMessageImage, chatMessage.imageUrl)
            } else {
                ivMessageImage.isVisible = false
            }
            //보내는사람=현재 uid
            firestore.collection("User").document(chatMessage.senderId ).get().addOnSuccessListener { document ->
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

    class DiffCallback : DiffUtil.ItemCallback<ChatMessage>() {
        override fun areItemsTheSame(oldItem: ChatMessage, newItem: ChatMessage): Boolean {
            return oldItem.senderPetName == newItem.senderPetName
        }

        override fun areContentsTheSame(oldItem: ChatMessage, newItem: ChatMessage): Boolean {
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
}