package com.pet.frompet.ui.chat.adapter

import android.content.Context
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.icu.util.TimeZone
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.pet.frompet.R
import com.pet.frompet.ui.chat.activity.ChatClickUserDetailActivity
import com.pet.frompet.databinding.ItemChathomeBinding
import com.pet.frompet.data.model.User
import com.pet.frompet.ui.chat.viewmodel.ChatViewModel
import com.google.firebase.auth.FirebaseAuth
import java.util.Date
import java.util.Locale

class ChatHomeAdapter(
    var context: Context,
    private val chatViewModel: ChatViewModel,
    private val lifecycleOwner: LifecycleOwner,
) : ListAdapter<User, ChatHomeAdapter.ChatHomeViewHolder>(DiffCallback()) {

    var onChatItemClick: ((User) -> Unit)? = null
    private val sdf = SimpleDateFormat("a HH:mm", Locale.KOREA).apply {
        timeZone = TimeZone.getTimeZone("Asia/Seoul")
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatHomeViewHolder {
        val binding = ItemChathomeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChatHomeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChatHomeViewHolder, position: Int) {
        val user = getItem(position)
        holder.bindItems(user)
    }

    inner class ChatHomeViewHolder(private val binding: ItemChathomeBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindItems(user: User) = with(binding) {
            tvChatTitle.text = user.petName
            user.petProfile.let {
                ivPetProfile.load(it) {
                    error(R.drawable.kakaotalk_20230825_222509794_01)
                }
            }
            ivPetProfile.setOnClickListener {
                userDetail(user)
            }
            val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return
            val chatRoomId = chatViewModel.chatRoom(currentUserId, user.uid)

            chatViewModel.loadLastChats(currentUserId, user.uid)
            chatViewModel.lastChatLiveData(chatRoomId).observe(lifecycleOwner) { lastMessage ->
                if (lastMessage?.imageUrl != null) {
                    tvLastmessage.text = "(사진)"
                } else {
                    tvLastmessage.text = lastMessage?.message ?: "메시지가 없습니다."
                }
                lastMessage?.let {
                    val formatTime = formatTimeStamp(it.timestamp)
                    tvLastTime.text = formatTime
                } ?: run {
                    tvLastTime.text = ""
                }
            }

            chatViewModel.newChats.observe(lifecycleOwner) { newMessages ->
                val hasNewMessage = newMessages[chatRoomId] ?: false
                if (hasNewMessage) {
                    tvNewMessage.visibility = View.VISIBLE
                } else {
                    tvNewMessage.visibility = View.GONE
                }
            }

            root.setOnClickListener {
                onChatItemClick?.invoke(user)
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.uid == newItem.uid
        }

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem == newItem
        }
    }

    private fun userDetail(user: User) {
        val intent = Intent(context, ChatClickUserDetailActivity::class.java)
        intent.putExtra(ChatClickUserDetailActivity.USER, user)
        context.startActivity(intent)
    }

    private fun formatTimeStamp(timestamp: Long): String {
        return sdf.format(Date(timestamp))
    }
}