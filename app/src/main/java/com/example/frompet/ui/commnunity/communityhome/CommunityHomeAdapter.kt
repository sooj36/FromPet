package com.example.frompet.ui.commnunity.communityhome

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.frompet.data.model.CommunityHomeData
import com.example.frompet.databinding.ItemCoummunicationBinding
import com.example.frompet.ui.commnunity.community.CommunityActivity

class CommunityHomeAdapter(private val onClicked: (CommunityHomeData,Int) -> Unit) :
    ListAdapter<CommunityHomeData, CommunityHomeAdapter.CommunicationViewHolder>(
        object : DiffUtil.ItemCallback<CommunityHomeData>() {
            override fun areItemsTheSame(
                oldItem: CommunityHomeData,
                newItem: CommunityHomeData
            ): Boolean {
                return oldItem.petName == newItem.petName
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
                LayoutInflater.from(parent.context), parent, false,
            ), onClicked
        )
    }

    override fun onBindViewHolder(holder: CommunicationViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    class CommunicationViewHolder(
        private val binding: ItemCoummunicationBinding,
        private val onClicked: (CommunityHomeData,Int) -> Unit
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CommunityHomeData) = with(binding) {
            ivPetNameComm.load(item.petLogo)
            tvPetNameComm.text = item.petName
            binding.communicationId.setOnClickListener {
                onClicked(item, adapterPosition)
                Log.e("zzzzzzz", "Clicked item: ${item.petName}, Index: $adapterPosition")
                val intent: Intent = Intent(it.context, CommunityActivity::class.java)
                it.context.startActivity(intent)
            }
        }
    }
}