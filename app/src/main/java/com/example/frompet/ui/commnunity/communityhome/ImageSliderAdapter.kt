package com.example.frompet.ui.commnunity.communityhome

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.frompet.R
import com.example.frompet.data.model.User
import com.example.frompet.databinding.ItemSliderImageBinding

class ImageSliderAdapter : ListAdapter<User, ImageSliderAdapter.ViewHolder>(UserDiffCallback()) {

    inner class ViewHolder(val binding: ItemSliderImageBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSliderImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = getItem(position)

        holder.binding.ivTopProfile.load(user.petProfile)
        holder.binding.ivBadge1.visibility = View.GONE
        holder.binding.ivBadge2.visibility = View.GONE
        holder.binding.ivBadge3.visibility = View.GONE
        holder.binding.ivBadge4.visibility = View.GONE
        holder.binding.ivBadge5.visibility = View.GONE

        when (position) {
            0 -> holder.binding.ivBadge1.visibility = View.VISIBLE
            1 -> holder.binding.ivBadge2.visibility = View.VISIBLE
            2 -> holder.binding.ivBadge3.visibility = View.VISIBLE
            3 -> holder.binding.ivBadge4.visibility = View.VISIBLE
            4 -> holder.binding.ivBadge5.visibility = View.VISIBLE
        }
    }

    class UserDiffCallback : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.uid == newItem.uid
        }

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem == newItem
        }
    }
}
