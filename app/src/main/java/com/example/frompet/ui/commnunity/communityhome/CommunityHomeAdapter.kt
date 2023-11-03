package com.example.frompet.ui.commnunity.communityhome

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.frompet.data.model.CommunityHomeData
import com.example.frompet.data.model.toCommunityData
import com.example.frompet.databinding.ItemCoummunicationBinding
import com.example.frompet.ui.commnunity.community.CommunityActivity

class CommunityHomeAdapter(
    var onClicked: (CommunityHomeData,Int) -> Unit) :
    ListAdapter<CommunityHomeData, CommunityHomeAdapter.CommunicationViewHolder>(
        object : DiffUtil.ItemCallback<CommunityHomeData>() {
            override fun areItemsTheSame(
                oldItem: CommunityHomeData,
                newItem: CommunityHomeData
            ): Boolean {
                return oldItem == newItem
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
        holder.bind(item,position)
    }

    class CommunicationViewHolder(
        private val binding: ItemCoummunicationBinding,
        private val onItemClick: (CommunityHomeData, Int) -> Unit
    ) :
        RecyclerView.ViewHolder(binding.root) {
        private val context = binding.root.context
        fun bind(item: CommunityHomeData,position: Int) = with(binding) {
            ivPetNameComm.load(item.petLogo)
            tvPetNameComm.text = item.petType
            /*binding.communicationId.setOnClickListener {
                val intent = Intent(it.context, CommunityActivity::class.java)
                it.context.startActivity(intent)
            }*/
            itemView.setOnClickListener {
                onItemClick(item,adapterPosition)
                val item = item.toCommunityData()
                Intent(context, CommunityActivity::class.java).apply {
                    putExtra(CommunityActivity.EXTRA_DATA, item)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }.run { context.startActivity(this) }
                Log.e("zzzzzzz", "Clicked item: ${item.petType}, Index: $adapterPosition")
            }
        }
    }
}