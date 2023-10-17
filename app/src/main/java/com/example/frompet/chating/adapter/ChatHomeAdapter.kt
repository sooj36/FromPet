package com.example.frompet.chating.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.example.frompet.R
import com.example.frompet.chating.ChatMessageActivity
import com.example.frompet.chating.ChatUserDetailActivity
import com.example.frompet.databinding.ItemChathomeBinding
import com.example.frompet.login.data.ChatMessage
import com.example.frompet.login.data.UserModel
import com.example.frompet.login.viewmodel.ChatViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class ChatHomeAdapter(var context: Context) :
    ListAdapter<UserModel, ChatHomeAdapter.ChatHomeViewHolder>(DiffCallback()) {

    val database = FirebaseDatabase.getInstance().getReference("chatMessages")

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
                user.petProfile.let {
                    ivPetProfile.load(it){
                        error(R.drawable.kakaotalk_20230825_222509794_01)
                    }
                }

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
