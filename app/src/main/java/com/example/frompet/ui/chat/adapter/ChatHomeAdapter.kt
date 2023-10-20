package com.example.frompet.ui.chat.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.frompet.R
import com.example.frompet.ui.chat.ChatClickUserDetailActivity
import com.example.frompet.databinding.ItemChathomeBinding
import com.example.frompet.data.model.User
import com.example.frompet.ui.chat.ChatViewModel
import com.google.firebase.auth.FirebaseAuth

class ChatHomeAdapter(var context: Context, private val chatViewModel: ChatViewModel, private val lifecycleOwner: LifecycleOwner,) :
    ListAdapter<User, ChatHomeAdapter.ChatHomeViewHolder>(DiffCallback()) {

    var onChatItemClick: ((User) -> Unit)? = null

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
        fun bindItems(user: User) {
            binding.apply {
                tvChatTitle.text = user.petName
                user.petProfile.let {
                    ivPetProfile.load(it){
                        error(R.drawable.kakaotalk_20230825_222509794_01)
                    }
                }
                ivPetProfile.setOnClickListener {
                    userDetail(user)
                }
                val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return
                val chatRoomId = chatViewModel.chatRoom(currentUserId, user.uid)
                chatViewModel.loadLastChats(currentUserId, user.uid)

                val liveData = chatViewModel.lastChatLiveData(chatRoomId)
                liveData.observe(lifecycleOwner) { lastMessage ->
                    binding.tvLastmessage.text = lastMessage?.message ?: ""
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
    }

    class DiffCallback : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem == newItem
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
}
