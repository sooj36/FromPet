package com.pet.frompet.ui.setting
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.pet.frompet.R
import com.pet.frompet.data.model.User
import com.pet.frompet.databinding.ItemFriendsBinding
import com.pet.frompet.ui.chat.adapter.ChatListAdapter
import com.pet.frompet.util.RandomColor

class FriendsListAdapter(private val context: Context, private val onUserClick: (User,ImageView) -> Unit) :
    ListAdapter<User, FriendsListAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemFriendsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = getItem(position)
        holder.bindItems(user)
    }


    inner class ViewHolder(private val binding: ItemFriendsBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindItems(user: User) {
            binding.apply {
                tvUserName3.text = user.petName
                tvUserType3.text = user.petType
                Glide.with(root.context).load(user.petProfile).into(binding.ivPetProfile)

                val startColor = RandomColor.getRandomColor(255, 0.5f, 0.7f)
                val endColor = Color.argb(0, Color.red(startColor), Color.green(startColor), Color.blue(startColor))
                val gradientDrawable = binding.view6.background as GradientDrawable
                gradientDrawable.colors = intArrayOf(startColor, endColor)
                root.setOnClickListener {
                    onUserClick(user,ivPetProfile)
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
