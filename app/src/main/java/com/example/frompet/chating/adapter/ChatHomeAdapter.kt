package com.example.frompet.chating.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.frompet.chating.ChatMessageActivity
import com.example.frompet.chating.ChatUserDetailActivity
import com.example.frompet.databinding.ItemChathomeBinding
import com.example.frompet.login.data.UserModel

class ChatHomeAdapter(var context: Context) :
    ListAdapter<UserModel, ChatHomeAdapter.ChatHomeViewHolder>(DiffCallback()) {

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

        fun bindItems(user: UserModel) {
            binding.apply {
                tvChatTitle.text = user.petName
                root.setOnClickListener {
                    val intent = Intent(context, ChatMessageActivity::class.java)
                    intent.putExtra("user", user)
                    context.startActivity(intent)
                }
            }

        }
    }

    class DiffCallback : DiffUtil.ItemCallback<UserModel>() {
        override fun areItemsTheSame(oldItem: UserModel, newItem: UserModel): Boolean {
            return oldItem == newItem
        }
        override fun areContentsTheSame(oldItem: UserModel, newItem: UserModel): Boolean {
            return oldItem == newItem
        }
    }
}
