package com.pet.frompet.ui.commnunity.communityhome

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.pet.frompet.data.model.CommunityHomeData
import com.pet.frompet.data.model.toCommunityData
import com.pet.frompet.databinding.ItemCoummunicationBinding
import com.pet.frompet.ui.commnunity.community.CommunityActivity

class CommunityHomeAdapter(
    private val onClicked: (CommunityHomeData) -> Unit
) : ListAdapter<CommunityHomeData, CommunityHomeAdapter.CommunicationViewHolder>(
    object : DiffUtil.ItemCallback<CommunityHomeData>() {
        override fun areItemsTheSame(
            oldItem: CommunityHomeData,
            newItem: CommunityHomeData
        ): Boolean {
            return oldItem.petType == newItem.petType
        }

        override fun areContentsTheSame(
            oldItem: CommunityHomeData,
            newItem: CommunityHomeData
        ): Boolean {
            return oldItem == newItem
        }
    }
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommunicationViewHolder {
        return CommunicationViewHolder(
            ItemCoummunicationBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            ), onClicked
        )
    }

    override fun onBindViewHolder(holder: CommunicationViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }


    class CommunicationViewHolder(
        private val binding: ItemCoummunicationBinding,
        private val onItemClicked: (CommunityHomeData) -> Unit
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CommunityHomeData) = with(binding) {
            ivPetNameComm.load(item.petLogo)
            tvPetNameComm.text = item.petType
            itemView.setOnClickListener {
                onItemClicked(item) // 아이템 클릭 시 onClicked 클로저를 호출하여 클릭 이벤트 처리
            }
        }
    }
}
