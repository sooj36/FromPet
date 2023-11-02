package com.example.frompet.ui.commnunity.communityhome

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.frompet.data.model.CommunityHomeData
import com.example.frompet.databinding.ItemCoummunicationBinding
import com.example.frompet.ui.chat.activity.ChatPullScreenActivity
import com.example.frompet.ui.chat.adapter.ChatMessageAdapter
import com.example.frompet.ui.commnunity.community.CommunityActivity

class CommunityHomeAdapter(communicationFragment: List<CommunityHomeData>) :
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
        val binding = ItemCoummunicationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CommunicationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CommunicationViewHolder, position : Int) {
        // currentList : 해당 Adapter 에 " submitList()를 통해 삽입한 아이템 리스트
        holder.bind(currentList[position])
    }

    private fun clickListener(imageButton: ImageButton, imageUrl: String?) {
        imageButton.setOnClickListener {
            val intent = Intent(it.context, ChatPullScreenActivity::class.java)
            intent.putExtra(ChatMessageAdapter.IMAGE_URL, imageUrl)
            it.context.startActivity(intent)
        }
    }


    class CommunicationViewHolder(private val binding: ItemCoummunicationBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(communityHomeData: CommunityHomeData) {
            binding.ivPetNameComm.load(communityHomeData.pet_logo)
            binding.tvPetNameComm.text = communityHomeData.pet_name


            binding.communicationId.setOnClickListener {
                communityHomeData.pet_name
                val intent : Intent = Intent(it.context, CommunityActivity::class.java)
                it.context.startActivity(intent)
            }
        }
    }
}