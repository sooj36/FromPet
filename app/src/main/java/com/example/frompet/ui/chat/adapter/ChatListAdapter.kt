package com.example.frompet.ui.chat.adapter

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.frompet.R

import com.example.frompet.databinding.ItemChatlistBinding
import com.example.frompet.data.model.User
import com.example.frompet.util.RandomColor
import com.example.frompet.util.RandomColor.Companion.getRandomColor
import java.util.Random

class ChatListAdapter(private val context: Context, private val onUserClick: (User, ImageView) -> Unit) :
    ListAdapter<User, ChatListAdapter.ChatListViewHoler>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatListViewHoler {
        val binding = ItemChatlistBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChatListViewHoler(binding)
    }

    override fun onBindViewHolder(holder: ChatListViewHoler, position: Int) {
        val user = getItem(position)
        holder.bindItems(user)
    }

    inner class ChatListViewHoler(private val binding: ItemChatlistBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindItems(user: User) {
            binding.apply {
                tvUserName.text = user.petName
                tvUserType.text = user.petType
                tvUserAge.text = "${user.petAge.toString()}세"

                user.petProfile?.let {
                    ivPetProfile2.load(it){
                        error(R.drawable.kakaotalk_20230825_222509794_01)
                    }
                }
                val startColor = getRandomColor(255, 0.5f, 0.7f)
                val endColor = Color.argb(0, Color.red(startColor), Color.green(startColor), Color.blue(startColor))
                val gradientDrawable = binding.view6.background as GradientDrawable
                gradientDrawable.colors = intArrayOf(startColor, endColor)
                root.setOnClickListener {
                    onUserClick(user,ivPetProfile2)
                }
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
}




