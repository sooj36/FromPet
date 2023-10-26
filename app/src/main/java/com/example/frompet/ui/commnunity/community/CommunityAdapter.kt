package com.example.frompet.ui.commnunity.community

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.frompet.data.model.CommunityData
import com.example.frompet.data.model.User
import com.example.frompet.databinding.ItemCommunityBinding
import com.example.frompet.ui.chat.adapter.ChatMessageAdapter
import com.example.frompet.ui.setting.FriendsListAdapter

class CommunityAdapter(private val ListClick: (CommunityData) -> Unit) :
    ListAdapter<CommunityData, CommunityAdapter.CommunityViewHolder>(DiffCallback()) {


    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): CommunityAdapter.CommunityViewHolder {
        return CommunityViewHolder(
            ItemCommunityBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            ), ListClick
        )
    }

    override fun onBindViewHolder(holder: CommunityAdapter.CommunityViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }


    inner class CommunityViewHolder(
        private val binding: ItemCommunityBinding,
        private val ListClick: (CommunityData) -> Unit
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(CommunityData: CommunityData) {
            binding.tvTitleComm.text = CommunityData.title
//                    binding.chipTag.isChecked = CommunityData.tag.
            binding.tvContentsComm.text = CommunityData.contents

            binding.linearlayoutAdd.setOnClickListener {
                ListClick(CommunityData)
            }

        }
    }

    class DiffCallback : DiffUtil.ItemCallback<CommunityData>() {
        override fun areItemsTheSame(oldItem: CommunityData, newItem: CommunityData): Boolean {
            return oldItem.title == newItem.title
        }

        override fun areContentsTheSame(oldItem: CommunityData, newItem: CommunityData): Boolean {
            return oldItem == newItem
        }
    }
}