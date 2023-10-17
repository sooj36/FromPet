package com.example.frompet.home.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.frompet.databinding.ItemHomeBinding
import com.example.frompet.login.data.UserModel


class HomeAdapter : ListAdapter<UserModel, HomeAdapter.HomeViewHolder>(
    object : DiffUtil.ItemCallback<UserModel>() {
        override fun areItemsTheSame(oldItem: UserModel, newItem: UserModel): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: UserModel, newItem: UserModel): Boolean {
            return oldItem == newItem
        }
    }
) {
    init {
        Log.d("shshshsh", "Adapter initialized")
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val binding = ItemHomeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HomeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        try {
            val item = getItem(position)
            holder.bind(item)
        } catch (e: Exception) {
            Log.e("shshshs", "Error in onBindViewHolder: $e")
        }
    }

    class HomeViewHolder(
        private val binding: ItemHomeBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: UserModel) = with(binding){
            ivPetImage.load(item.petProfile)
            tvNamePet.text = item.petName
            tvTypePet.text = item.petType


        }
    }
}
